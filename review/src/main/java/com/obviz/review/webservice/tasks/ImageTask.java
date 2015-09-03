package com.obviz.review.webservice.tasks;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import com.obviz.review.managers.CacheManager;
import com.obviz.review.managers.ImagesManager;
import com.obviz.review.webservice.ConnectionService;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by gaylor on 27.07.15.
 * Get an image and populate the cache when acquired
 */
public class ImageTask extends HttpTask<Bitmap> {

    private String url;
    private Future<Bitmap> mFuture = null;

    @Override
    public void cancel() {

        if (mFuture != null) {
            mFuture.cancel(true);
        }

        super.cancel(true);
    }

    @Override
    protected Bitmap doInBackground(final Uri.Builder... urls) {

        try {
            url = urls[0].toString();

            // Take the image with a square size of 100px
            String littleSizeUrl = url.replaceFirst("\\d+$", "100");

            InputStream in = new URL(littleSizeUrl).openStream();
            final Bitmap bitmap = BitmapFactory.decodeStream(in);

            mFuture = ConnectionService.instance.getExecutor().submit(new Callable<Bitmap>() {
                @Override
                public Bitmap call() throws Exception {

                    try {

                        // Put in disk cache
                        String key = CacheManager.KeyBuilder.forImage(url);
                        CacheManager.instance().add(key, bitmap);

                        return bitmap;

                    } catch (Exception e) {
                        Log.e("-- Image Loading --", "With message:" + e.getMessage());
                        e.printStackTrace();
                    }

                    return null;
                }
            });

            try {

                return mFuture.get();
            } catch (CancellationException | InterruptedException | ExecutionException ignored) {} finally {

                in.close();
            }

        } catch (IOException e) {

            Log.e("__IMAGE__", "IOException occurred: " + e.getLocalizedMessage());
        }

        return null;
    }

    @Override
    protected void onPostExecute(Bitmap result) {

        ConnectionService.instance.removeRequest(this);

        if (result != null) {
            ImagesManager.getInstance().addBitmap(url, result);
        }
    }

    @Override
    protected void onCancelled(Bitmap result) {

        ConnectionService.instance.removeRequest(this);
    }
}
