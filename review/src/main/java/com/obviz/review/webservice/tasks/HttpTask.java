package com.obviz.review.webservice.tasks;

import android.net.Uri;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by gaylor on 27.07.15.
 *
 */
public class HttpTask<T> extends AsyncTask<Uri.Builder, Integer, T> {

    protected InputStream stream;

    public void closeStream() {
        try {
            if (stream != null) {
                stream.close();
            }
        } catch (IOException e) {
            // Nothing ...
        }
    }

    @Override
    protected T doInBackground(Uri.Builder... builders) {
        throw new UnsupportedOperationException();
    }
}
