package com.obviz.review.webservice.tasks;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import com.obviz.review.managers.ImagesManager;
import com.obviz.review.webservice.ConnectionService;

import java.io.InputStream;
import java.net.URL;

/**
 * Created by gaylor on 27.07.15.
 * Get an image and populate the cache when acquired
 */
public class ImageTask extends HttpTask<Bitmap> {

    private String url;

    @Override
    protected Bitmap doInBackground(Uri.Builder... urls) {
        url = urls[0].toString();

        try {
            InputStream in = new URL(url).openStream();
            return BitmapFactory.decodeStream(in);

        } catch (Exception e) {
            Log.e("-- Image Loading --", e.getMessage());
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
