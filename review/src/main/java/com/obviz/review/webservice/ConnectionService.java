package com.obviz.review.webservice;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by gaylor on 26.06.15.
 * Asynchronous tasks to interact with the back-end
 */
public class ConnectionService {

    enum TypeRequest { GET, POST }

    public static final String URL = "http://vps40100.vps.ovh.ca/ObVizServiceAdmin";
    private static ExecutorService executor = Executors.newCachedThreadPool();

    public static <T> HttpTask<T> execute(String url, Map<String, String> params, RequestCallback<T> callback,
                                          boolean isPostRequest) {

        // Create the url
        Uri.Builder builder = new Uri.Builder();
        builder.encodedPath(url);
        for (String key : params.keySet()) {
            builder.appendQueryParameter(key, params.get(key));
        }

        HttpTask<T> task = new HttpTask<>(callback, isPostRequest);
        return (HttpTask<T>) task.executeOnExecutor(executor, builder.build());
    }

    /* Public class */

    public static class HttpTask<T> extends AsyncTask<Uri, Integer, T> {

        private RequestCallback<T> callback;
        private TypeRequest type;

        public HttpTask(RequestCallback<T> callback, boolean isPostRequest) {
            this.callback = callback;

            if (isPostRequest) {
                type = TypeRequest.POST;
            } else {
                type = TypeRequest.GET;
            }
        }

        @Override
        protected T doInBackground(Uri... urls) {

            if (urls.length > 0) {

                Uri uri = urls[0];
                HttpURLConnection connection = null;

                try {
                    URL url;
                    if (type == TypeRequest.POST) {
                        url = new URL(uri.getPath());
                    } else {
                        url = new URL(uri.toString());
                    }
                    connection = (HttpURLConnection) url.openConnection();

                    if (type == TypeRequest.POST) {
                        String body = uri.getEncodedQuery();
                        connection.setDoOutput(true);
                        connection.setFixedLengthStreamingMode(body.length());

                        OutputStream out = new BufferedOutputStream(connection.getOutputStream());
                        IOUtils.write(body, out);
                    }

                    InputStream in = connection.getInputStream();
                    String body = IOUtils.toString(in, "utf-8");

                    connection.disconnect();

                    return MessageParser.fromJson(body, callback.getType());

                } catch (IOException e) {
                    Log.e("IO Exception", e.getMessage());
                } finally {

                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(T result) {

            callback.execute(result);
        }
    }
}
