package com.obviz.review.managers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import com.obviz.review.webservice.ConnectionService;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

/**
 * Created by gaylor on 09/19/2015.
 *
 */
public class ImageLoader {

    private static final int MAXIMUM_IMAGES_STACK = 50;

    private static ImageLoader instance = new ImageLoader();

    private LinkedList<String> mStack;
    private Map<String, Bitmap> mImages;
    private Map<String, Set<Wrapper>> mViews;

    private ImageLoader() {
        mStack = new LinkedList<>();
        mViews = new HashMap<>();
        mImages = new HashMap<>();
    }

    public static ImageLoader instance() {
        return instance;
    }

    public void get(String url, ImageView view) {
        get(url, new Wrapper(view));
    }

    public void get(String url, TextView view) {
        get(url, new Wrapper(view));
    }

    private void get(String url, Wrapper wrapper) {

        if (url == null || !wrapper.isValid()) {
            return;
        }

        // Looking for the bitmap in the memory cache
        if (mImages.containsKey(url)) {
            wrapper.setBitmap(mImages.get(url));
            return;
        }

        // Check if the view isn't already in waiting
        for (Set<Wrapper> wrappers : mViews.values()) {
            wrappers.remove(wrapper);
        }

        // Looking for a request already launched
        if (mViews.containsKey(url)) {
            mViews.get(url).add(wrapper);
            return;
        } else {

            mViews.put(url, new HashSet<Wrapper>());
            mViews.get(url).add(wrapper);
        }

        new ImageTask().executeOnExecutor(ConnectionService.instance().getExecutor(), url);
    }

    private class ImageTask extends AsyncTask<String, Void, Bitmap> {

        private String mUrl;

        @Override
        protected Bitmap doInBackground(String... urls) {

            mUrl = urls[0];

            String key = CacheManager.KeyBuilder.forImage(mUrl);
            Bitmap bitmap = CacheManager.instance().getImage(key);
            if (bitmap != null) {
                return bitmap;
            }

            try {
                InputStream stream = new URL(mUrl).openConnection().getInputStream();

                bitmap = BitmapFactory.decodeStream(new BufferedInputStream(stream));

                if (bitmap != null) {
                    CacheManager.instance().add(key, bitmap);
                }

                return bitmap;

            } catch (IOException e) {
                Log.e("--IMAGE--", "Error when loading image");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {

            if (bitmap != null) {

                mStack.remove(mUrl);
                mStack.push(mUrl);
                mImages.put(mUrl, bitmap);

                if (mStack.size() > MAXIMUM_IMAGES_STACK) {

                    String key = mStack.pop();
                    mImages.remove(key);
                }

                for (Wrapper wrapper : mViews.get(mUrl)) {
                    wrapper.setBitmap(bitmap);
                }

            } else {

                Log.e("--IMAGE--", "Null value for an image");
            }

            mViews.remove(mUrl);
        }
    }

    public class Wrapper {
        private ImageView imageView;
        private TextView textView;

        public Wrapper(ImageView view) {
            imageView = view;
        }

        public Wrapper(TextView view) {
            textView = view;
        }

        public void setBitmap(Bitmap bitmap) {
            if (imageView != null) {
                imageView.setImageBitmap(bitmap);
                imageView.invalidate();
            }

            if (textView != null) {
                textView.setCompoundDrawablesWithIntrinsicBounds(new BitmapDrawable(null, bitmap), null, null, null);
                textView.invalidate();
            }
        }

        public boolean isValid() {
            return imageView != null || textView != null;
        }

        @Override
        public boolean equals(Object object) {
            if (object.getClass() == Wrapper.class) {

                if (imageView != null) {
                    return imageView.equals(((Wrapper) object).imageView);
                }

                return textView != null && textView.equals(((Wrapper) object).textView);
            }

            return false;
        }

        @Override
        public int hashCode() {
            if (imageView != null) {
                return imageView.hashCode();
            }

            if (textView != null) {
                return textView.hashCode();
            }

            return 0;
        }
    }
}
