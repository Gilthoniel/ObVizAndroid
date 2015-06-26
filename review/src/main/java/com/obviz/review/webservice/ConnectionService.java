package com.obviz.review.webservice;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by gaylor on 26.06.15.
 * Asynchronous tasks to get information from the Back-End
 */
public class ConnectionService {

    public static final String URL = "http://vps40100.vps.ovh.ca/ObVizServiceAdmin";

    public static String get(String url, Map<String,String> params) {
        try {
            // Create the url
            Uri.Builder builder = new Uri.Builder();
            builder.encodedPath(url);
            for (String key : params.keySet()) {
                builder.appendQueryParameter(key, params.get(key));
            }

            GetHTTPRequest task = new GetHTTPRequest();
            AsyncTask<Uri, Integer, String> resultTask = task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, builder.build());
            return resultTask.get();

        } catch (InterruptedException | ExecutionException e) {
            Log.e("Concurrent error", e.getMessage());
            return "null";
        }
    }

    /* Private class */

    private static class GetHTTPRequest extends AsyncTask<Uri, Integer, String> {

        @Override
        protected String doInBackground(Uri... urls) {

            if (urls.length > 0) {

                try {
                    URL url = new URL(urls[0].toString());
                    Log.i("URL", url.toString());
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                    InputStream in = connection.getInputStream();
                    return IOUtils.toString(in, "utf-8");

                } catch (IOException e) {
                    Log.e("IO Exception", e.getMessage());
                    return "null";
                }
            } else {
                return "null";
            }
        }
    }
}
