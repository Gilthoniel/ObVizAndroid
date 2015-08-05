package com.obviz.review.managers;

import android.support.annotation.NonNull;
import android.util.Log;

import java.io.*;
import java.util.*;

/**
 * Created by gaylor on 04-Aug-15.
 *
 * Cache on the disk with LRU policy and expiration time for each object
 */
public class DiskTimedCache {

    public static final String JOURNAL_FILE_NAME = "journal";

    private Map<File, Integer> mObjects;
    private LinkedList<File> mStack;
    private File mDirectory;
    private File mJournal;
    private long mMaxSize;
    private int mVersion;
    private boolean isInitiated;

    private DiskTimedCache() {

        // Where we can find the link File <-> Expiration time
        mObjects = new HashMap<>();
        // Stack to manage the LRU policy
        mStack = new LinkedList<>();

        isInitiated = false;
    }

    public File getDirectory() {

        return mDirectory;
    }

    public List<File> getStack() {
        return mStack;
    }

    /**
     * Get an editor to set a value
     * @param key Key of the object
     * @param expiredTime in millisecond
     * @return Editor
     */
    public Editor edit(String key, int expiredTime) {
        if (!key.matches("^[a-z0-9-_]{1,64}$")) {
            throw new BadKeyException();
        }

        // Check the state of the cache
        if (!isInitiated) {
            return null;
        }

        try {
            // File where to populate the object
            File file = new File(mDirectory, key);

            Editor editor = new Editor();
            // Stream for the user
            editor.mStream = new BufferedOutputStream(new FileOutputStream(file));

            // after stream instantiation to avoid glitch in the stack
            updateStack(file);
            if (expiredTime > 0) {
                mObjects.put(file, expiredTime);
            }

            return editor;
        } catch (FileNotFoundException e) {

            Log.e("__CACHE__", "When create object file");
            e.printStackTrace();

            return null;
        }
    }

    /**
     * Edit without cache
     * @param key Key of the object
     * @return an Editor which contains a stream
     */
    public Editor edit(String key) {
        return edit(key, -1);
    }

    /**
     * Get an accessor to read a object in the stack
     * @param key Key of the object
     * @return Accessor
     */
    public Accessor get(String key) {
        if (!key.matches("^[a-z0-9-_]{1,64}$")) {
            throw new BadKeyException();
        }

        // Check the state of the cache
        if (!isInitiated) {
            return null;
        }

        Accessor accessor = new Accessor();
        File file = new File(mDirectory, key);

        try {
            // throw exception if the key doesn't exist
            accessor.mStream = new BufferedInputStream(new FileInputStream(file));

            if (mObjects.containsKey(file) && mObjects.get(file) + file.lastModified() <= new Date().getTime()) {

                // Remove the value of the key because it's expired
                mObjects.remove(file);
                mStack.remove(file);
                if (!file.delete()) {
                    Log.e("__CACHE__", "Error when trying to remove file");
                }
                accessor.commit();

                return null;

            } else {

                // Set the last modified date because we want to read it
                if (!file.setLastModified(new Date().getTime())) {
                    Log.e("__CACHE__", "Error when trying to set lastModified date");
                }
            }

            // Put the file at the top of the stack if it exists
            updateStack(file);

            return accessor;

        } catch (FileNotFoundException e) {

            // This key doesn't exist so we return null
            return null;
        }
    }

    /**
     * Shutdown the cache, without removing the content
     */
    public void close() {

    }

    /* Private functions */

    private void updateJournal() {
        try {

            PrintWriter writer = new PrintWriter(new BufferedOutputStream(new FileOutputStream(mJournal, false)));
            for (File file : mStack) {

                int expiration = mObjects.containsKey(file) ? mObjects.get(file) : 0;

                writer.println(file.getName() + ":" + expiration);
            }

            writer.close();

        } catch (FileNotFoundException e) {

            Log.e("__CACHE__", "Can't write on the journal");
        }
    }

    private void updateStack(File file) {

        int index = mStack.indexOf(file);
        if (index > -1) {
            mStack.remove(index);
        }

        mStack.addFirst(file);
    }

    /* Public class */

    public class Editor {

        private OutputStream mStream;

        private Editor() {}

        public OutputStream getStream() {
            return mStream;
        }

        /**
         * End of transaction, to check max size
         */
        public void commit() {
            try {

                mStream.flush();
                mStream.close();

                // Check the size of the cache
                long size = 0;
                for (String filename : mDirectory.list()) {
                    if (!filename.equals(JOURNAL_FILE_NAME)) {
                        size += new File(mDirectory, filename).length();
                    }
                }

                // Fix if it's too big
                while (size > mMaxSize - mJournal.length()) {
                    File file = mStack.removeLast();
                    long fileSize = file.length();

                    if (file.delete()) {
                        size -= fileSize;
                    }
                    mObjects.remove(file);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            updateJournal();
        }
    }

    public class Accessor {

        private InputStream mStream;

        private Accessor() {}

        public InputStream getStream() {
            return mStream;
        }

        public void commit() {
            try {
                mStream.close();
            } catch (IOException ignored) {}

            updateJournal();
        }
    }

    /**
     * Singleton to create and instantiate the cache
     */
    public static class Factory {

        public static final Factory instance = new Factory();

        private Factory() {}

        /**
         * Create a cache in a specific directory
         * @param file Directory where to populate the cache
         * @param maxSize maximum size of the cache (NOT STRICT)
         * @return the instantiate cache or null if a failure occurred
         */
        public DiskTimedCache open(@NonNull File file, int version, long maxSize)
            throws IOException
        {

            DiskTimedCache cache = new DiskTimedCache();
            cache.mDirectory = file;
            cache.mMaxSize = maxSize;

            if (version > 0) {
                cache.mVersion = version;
            } else {
                cache.mVersion = -1 * version;
            }

            // Create the directory if it doesn't exist
            if (cache.mDirectory.mkdirs()) {
                Log.d("__CACHE__", "Cache directory created");
            }

            // Create log journal
            File journal = new File(file.getAbsolutePath(), JOURNAL_FILE_NAME + "_" + cache.mVersion);
            if (!journal.createNewFile() && !journal.canWrite()) {
                throw new IOException("Can't read/write the journal file");
            }
            cache.mJournal = journal;

            // Try to open the journal file
            try {
                BufferedReader stream = new BufferedReader(new FileReader(journal));

                String line;
                while ((line = stream.readLine()) != null) {

                    String[] parts = line.split(":");
                    File item = new File(cache.mDirectory, parts[0]);

                    cache.mStack.addLast(item);

                    int expiration = Integer.parseInt(parts[1]);
                    if (expiration > 0) {
                        cache.mObjects.put(file, expiration);
                    }
                }
                stream.close();

            } catch (IOException e) {
                // The file is not found or corrupt so we don't have anything to do
                // because the files will be deleted
            }

            // Clean cache directory
            String[] filesList = cache.mDirectory.list();
            if (filesList != null) {
                for (String filename : filesList) {
                    File item = new File(cache.mDirectory, filename);

                    if (!item.getName().equals(journal.getName()) && !cache.mStack.contains(item) && !item.delete()) {
                        Log.e("__CACHE__", "Error when trying to delete a file in the cache: " + item.getPath());
                    }
                }
            } else {

                throw new IOException("IO error when trying to get the list of files in the cache");
            }

            cache.isInitiated = true;
            return cache;
        }

        public DiskTimedCache empty() {

            DiskTimedCache cache = new DiskTimedCache();
            cache.isInitiated = false;

            return cache;
        }
    }

    /* Exceptions */
    public class BadKeyException extends IllegalArgumentException {

        @Override
        public String getMessage() {
            return "Key must match [a-z0-9-_]";
        }
    }
}
