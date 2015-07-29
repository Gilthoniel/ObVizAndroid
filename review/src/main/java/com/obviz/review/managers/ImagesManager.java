package com.obviz.review.managers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import com.jakewharton.disklrucache.DiskLruCache;
import com.obviz.review.webservice.GeneralWebService;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by gaylor on 23.07.15.
 * Management to load and distribute images to the views
 */
public class ImagesManager {

    private static final ImagesManager instance = new ImagesManager();
    private static final String DISK_CACHE_NAME = "cache_images";
    private static final int DISK_CACHE_SIZE = 1024 * 1024 * 10;

    private Map<String, List<ImageObserver>> mObservers;
    private DiskLruCache mDiskCache;
    private final Object mDiskCacheLock;
    private boolean mDiskCacheStarting;
    private Matcher mMatcher;

    private ImagesManager() {
        mObservers = new HashMap<>();
        mDiskCacheLock = new Object();
        mDiskCacheStarting = true;

        Pattern pattern = Pattern.compile("([a-zA-Z0-9-_]{1,64})");
        mMatcher = pattern.matcher("");
    }

    /**
     * Get the singleton
     * @return instance
     */
    static public ImagesManager getInstance() {
        return instance;
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
     * Return the image to the observer or execute a request and put the observer in the waiting list
     * @param url of the image use as key
     * @param observer to warn when the image is loaded
     */
    public void get(String url, ImageObserver observer) {

        String key = getKeyFromUrl(url);
        if (key == null) {
            return;
        }

        new GetImageTask(url, observer).execute(key);
    }

    /**
     * Add the bitmap to the storage with an certain key
     * @param url key of the cache
     * @param bitmap image
     */
    public void addBitmap(String url, Bitmap bitmap) {
        String key = getKeyFromUrl(url);
        if (key == null) {
            return;
        }

        // Put in disk cache
        synchronized (mDiskCacheLock) {
            if (mDiskCache != null) {
                try {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] bytes = stream.toByteArray();

                    DiskLruCache.Editor creator = mDiskCache.edit(key);
                    creator.set(0, Base64.encodeToString(bytes, Base64.DEFAULT));
                    creator.commit();
                } catch (IOException ignored) {}
            }
        }

        // Inform the observers that the image is there
        if (mObservers.containsKey(key)) {
            for (ImageObserver observer : mObservers.get(key)) {
                observer.onImageLoaded(url, bitmap);
            }

            mObservers.remove(key);
        }
    }

    /**
     *
     * @param url url of the image
     * @return the key version
     */
    private String getKeyFromUrl(String url) {
        if (url == null || url.length() == 0) {
            return null;
        }
        mMatcher.reset(url);

        if (mMatcher.find(url.lastIndexOf("/"))) {
            return mMatcher.group(1).toLowerCase();
        } else {
            return null;
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
                    mDiskCache = DiskLruCache.open(cacheDir, 1, 1, DISK_CACHE_SIZE);
                    mDiskCacheStarting = false;
                    mDiskCacheLock.notifyAll();

                } catch (IOException e) {
                    Log.e("__CACHE__", "Error when initialize disk cache");
                }
            }

            return null;
        }
    }

    /**
     * Background task to get the image from the disk or from the web
     */
    private class GetImageTask extends AsyncTask<String, Void, Bitmap> {

        private String mUrl;
        private ImageObserver mObserver;

        public GetImageTask(String url, ImageObserver observer) {
            mUrl = url;
            mObserver = observer;
        }

        @Override
        protected Bitmap doInBackground(String... keys) {

            String key = keys[0];

            // Try to acquire from the disk cache
            synchronized (mDiskCacheLock) {
                while (mDiskCacheStarting) {
                    try {
                        mDiskCacheLock.wait();
                    } catch (InterruptedException ignored) {}
                }

                if (mDiskCache != null) {
                    try {
                        DiskLruCache.Snapshot snapshot = mDiskCache.get(key);
                        if (snapshot != null) {
                            byte[] bytes = Base64.decode(snapshot.getString(0), Base64.DEFAULT);
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            snapshot.close();

                            return bitmap;
                        }
                    } catch (IOException ignored) {}
                }
            }

            // Load with an http request

            // add the observer for this image
            if (!mObservers.containsKey(key)) {
                List<ImageObserver> observers = new ArrayList<>();
                observers.add(mObserver);

                mObservers.put(key, observers);

                GeneralWebService.getInstance().loadImage(mUrl);
            } else {
                // The image is already loading
                mObservers.get(key).add(mObserver);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Bitmap image) {

            // If we found the image in the disk cache
            if (image != null) {
                mObserver.onImageLoaded(mUrl, image);
            }
        }
    }
}
