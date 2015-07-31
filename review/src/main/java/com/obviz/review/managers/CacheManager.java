package com.obviz.review.managers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import com.jakewharton.disklrucache.DiskLruCache;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by gaylor on 30.07.15.
 *
 */
public class CacheManager {

    public static final CacheManager instance = new CacheManager();
    private static final int DISK_CACHE_SIZE = 1024 * 1024 * 20; // 20mo
    private static final String DISK_CACHE_NAME = "";
    private static final int DISK_CACHE_VERSION = 2;

    private final Object mDiskCacheLock;
    private boolean mDiskCacheStarting;
    private DiskLruCache mDiskCache;

    private CacheManager() {
        mDiskCacheLock = new Object();

        mDiskCacheStarting = true;
    }

    /**
     * Initialize the disk cache in background
     * @param context of the application
     */
    public void initCacheDisk(Context context) {

        final String cachePath;
        if (context.getExternalCacheDir() != null) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }

        File cacheDir = new File(cachePath + File.separator + DISK_CACHE_NAME);
        new InitDiskCacheTask().execute(cacheDir);
    }

    /**
     * Get an object of the cache
     * @param key Key of the entry
     * @param <T> Type of the object
     * @return the object
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key) {

        synchronized (mDiskCacheLock) {

            try {
                // waiting for initialization of the cache
                while (mDiskCacheStarting) {
                    mDiskCacheLock.wait();
                }

                // Check that the cache's initialization has not failed
                if (mDiskCache == null) {
                    return null;
                }

                DiskLruCache.Snapshot snapshot = mDiskCache.get(key);

                if (snapshot != null) {
                    ObjectInputStream stream = new ObjectInputStream(new BufferedInputStream(snapshot.getInputStream(0)));

                    // We know what we put in the cache
                    return (T) stream.readObject();
                }
            } catch (IOException | ClassNotFoundException | InterruptedException e) {

                e.printStackTrace();
            }
        }

        return null;
    }

    /**
     * Get the image stores with the key, or get a null result
     * @param key key of the image
     * @return Bitmap or Null
     */
    public Bitmap getImage(String key) {

        synchronized (mDiskCacheLock) {

            try {
                // waiting for initialization of the cache
                while (mDiskCacheStarting) {
                    mDiskCacheLock.wait();
                }

                // Check that the cache's initialization has not failed
                if (mDiskCache == null) {
                    return null;
                }

                DiskLruCache.Snapshot snapshot = mDiskCache.get(key);

                if (snapshot != null) {
                    InputStream stream = new BufferedInputStream(snapshot.getInputStream(0));

                    Bitmap bitmap = BitmapFactory.decodeStream(stream);
                    stream.close();
                    snapshot.close();

                    return bitmap;
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    /**
     * Add an object to the cache
     * @param key Key of the entry
     * @param object Object : MUST implement Serializable
     */
    public void add(String key, Object object) {

        synchronized (mDiskCacheLock) {
            try {
                // waiting for initialization of the cache
                while (mDiskCacheStarting) {
                    mDiskCacheLock.wait();
                }

                // Check that the cache's initialization has not failed
                if (mDiskCache == null) {
                    return;
                }

                DiskLruCache.Editor editor = mDiskCache.edit(key);
                if (editor != null) {
                    ObjectOutputStream stream = new ObjectOutputStream(new BufferedOutputStream(editor.newOutputStream(0)));
                    stream.writeObject(object);
                    stream.flush();
                    stream.close();
                    editor.commit();
                }
            } catch (IOException | InterruptedException e) {

                e.printStackTrace();
            }
        }
    }

    /**
     * Store an image in the cache
     * @param key Key of the image
     * @param bitmap Bitmap format of the image
     */
    public void add(String key, Bitmap bitmap) {

        synchronized (mDiskCacheLock) {
            try {
                // waiting for initialization of the cache
                while (mDiskCacheStarting) {
                    mDiskCacheLock.wait();
                }

                // Check that the cache's initialization has not failed
                if (mDiskCache == null) {
                    return;
                }

                DiskLruCache.Editor editor = mDiskCache.edit(key);
                if (editor != null) {
                    OutputStream stream = new BufferedOutputStream(editor.newOutputStream(0));
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    stream.flush();
                    stream.close();

                    editor.commit();
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static class KeyBuilder {

        public static final KeyBuilder instance = new KeyBuilder();

        private static Pattern mPattern;
        private static Pattern mCleaner;

        private KeyBuilder() {
            mPattern = Pattern.compile("([a-zA-Z0-9-_]{1,64})");

            mCleaner = Pattern.compile("[^a-zA-Z0-9-_]");
        }

        public static String forReviews(String appID) {
            return "reviews_" + clean(appID);
        }

        public static String forSearch(String query) {
            return "search_" + clean(query);
        }

        public static String forApps(String appID) {
            return "app_" + clean(appID);
        }

        /**
         *
         * @param url url of the image
         * @return the key version
         */
        public static String forImage(String url) {
            if (url == null || url.length() == 0) {
                return null;
            }
            Matcher matcher = mPattern.matcher(url);

            if (matcher.find(url.lastIndexOf("/"))) {
                return matcher.group(1).toLowerCase();
            } else {
                return null;
            }
        }

        private static String clean(String sequence) {

            Matcher matcher = mCleaner.matcher(sequence);
            // Clear the wrong characters, the key need to satisfy [a-z0-9]
            return matcher.replaceAll("").toLowerCase();
        }
    }

    /**
     * Async task to init the disk cache mechanism
     */
    private class InitDiskCacheTask extends AsyncTask<File, Void, Void> {
        @Override
        protected Void doInBackground(File... params) {
            synchronized (mDiskCacheLock) {
                File cacheDir = params[0];
                try {
                    mDiskCache = DiskLruCache.open(cacheDir, DISK_CACHE_VERSION, 1, DISK_CACHE_SIZE);

                } catch (IOException e) {
                    Log.e("__CACHE__", "Error when initialize disk cache");
                    e.printStackTrace();
                } finally {

                    mDiskCacheStarting = false;
                    mDiskCacheLock.notifyAll();
                }
            }

            return null;
        }
    }

}
