package com.obviz.review.webservice.tasks;

import android.net.Uri;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Future;

/**
 * Created by gaylor on 27.07.15.
 *
 */
public abstract class HttpTask<T> extends AsyncTask<Void, Void, T> {

    Uri.Builder mUrl;

    public HttpTask(Uri.Builder url) {
        mUrl = url;
    }

    public abstract void cancel();

    @Override
    protected abstract T doInBackground(Void... voids);
}
