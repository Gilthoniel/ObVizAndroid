package com.obviz.review.webservice.tasks;

import android.net.Uri;
import android.util.Log;
import com.google.gson.JsonSyntaxException;
import com.obviz.review.Constants;
import com.obviz.review.managers.CacheManager;
import com.obviz.review.webservice.ConnectionService;
import com.obviz.review.webservice.MessageParser;
import com.obviz.review.webservice.RequestCallback;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by gaylor on 27.07.15.
 * Execute an HTTP request to get JSON information and return into a java class
 */
public class JsonTask<T> extends HttpTask<T> {

    private RequestCallback<T> callback;
    private ConnectionService.TypeRequest type;
    private RequestCallback.Errors error;
    private String mKey;

    public JsonTask(RequestCallback<T> callback, boolean isPostRequest, String cacheKey) {
        this.callback = callback;

        if (isPostRequest) {
            type = ConnectionService.TypeRequest.POST;
        } else {
            type = ConnectionService.TypeRequest.GET;
        }

        error = RequestCallback.Errors.SUCCESS;
        mKey = cacheKey;
    }

    @Override
    protected T doInBackground(Uri.Builder... builders) {
        // Try to acquire from the cache
        if (mKey != null) {
            T object = CacheManager.instance.get(mKey);
            if (object != null) {
                return object;
            }
        }

        if (builders.length <= 0) {
            return null;
        }

        Uri uri = builders[0].build();
        HttpURLConnection connection = null;

        try {
            URL url;
            if (type == ConnectionService.TypeRequest.POST) {
                url = new URL(uri.getPath());
            } else {
                url = new URL(uri.toString());
            }
            connection = (HttpURLConnection) url.openConnection();
            Log.d("__INTERNET__", "Connection open");

            // In case of POST request, we write the information before getting the result
            if (type == ConnectionService.TypeRequest.POST) {
                String body = uri.getEncodedQuery();
                connection.setDoOutput(true);
                connection.setFixedLengthStreamingMode(body.length());

                IOUtils.write(body, connection.getOutputStream());
            }

            stream = connection.getInputStream();
            if (!isCancelled()) {
                // If this is launched, cancel the task will close the stream and interrupt this
                T object = MessageParser.fromJson(new InputStreamReader(stream), callback.getType());
                if (object != null && mKey != null) {
                    CacheManager.instance.add(mKey, object, Constants.CACHE_EXPIRATION_TIME);

                    return object;
                } else {

                    return object;
                }
            }

        } catch (IOException e) {

            error = RequestCallback.Errors.CONNECTION;
            Log.e("IO Exception", e.getMessage());
        } catch (JsonSyntaxException e) {

            error = RequestCallback.Errors.JSON;
            Log.e("Json error", e.getMessage());
        } finally {

            closeStream();

            if (connection != null) {
                connection.disconnect();
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(T result) {

        // Remove from the active request list
        ConnectionService.instance.removeRequest(this);

        // Use the callback here, because this function is executed in the UI Thread !
        if (result != null) {
            callback.onSuccess(result);
        } else {
            callback.onFailure(error);
        }
    }

    @Override
    protected void onCancelled(T result) {

        ConnectionService.instance.removeRequest(this);
    }
}
