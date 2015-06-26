package com.obviz.review.webservice;

import android.os.AsyncTask;
import android.util.Log;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by gaylor on 26.06.15.
 * Asynchronous tasks to get information from the Back-End
 */
public class ConnectionService {

    public static String get(String url, List<NameValuePair> params) {
        try {
            URIBuilder builder = new URIBuilder(url);
            builder.addParameters(params);

            GetHTTPRequest task = new GetHTTPRequest();
            AsyncTask<URI, Integer, String> resultTask = task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, builder.build());
            return resultTask.get();

        } catch (URISyntaxException e) {
            Log.e("URI Exception", e.getMessage());
            return "null";
        } catch (InterruptedException | ExecutionException e) {
            Log.e("Concurrent error", e.getMessage());
            return "null";
        }
    }

    /* Private class */

    private static class GetHTTPRequest extends AsyncTask<URI, Integer, String> {

        @Override
        protected String doInBackground(URI... urls) {

            try {
                if (urls.length > 0) {

                    HttpGet request = new HttpGet(urls[0].toASCIIString());
                    HttpClient client = new DefaultHttpClient();
                    HttpResponse response = client.execute(request);

                    int status = response.getStatusLine().getStatusCode();

                    if (status == 200) {
                        HttpEntity entity = response.getEntity();
                        return EntityUtils.toString(entity);
                    } else {

                        Log.e("Bad status code", "Code " + status);
                        return "null";
                    }

                } else {
                    return "null";
                }

            } catch (IOException e) {
                Log.e("IO Exception", e.getMessage());
                return "null";
            }
        }
    }
}
