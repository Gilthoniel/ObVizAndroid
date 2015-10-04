package com.obviz.review.webservice.tasks;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import com.google.gson.JsonSyntaxException;
import com.obviz.review.Constants;
import com.obviz.review.managers.CacheManager;
import com.obviz.review.webservice.ConnectionService;
import com.obviz.review.json.MessageParser;
import com.obviz.review.webservice.RequestCallback;
import org.acra.ACRA;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by gaylor on 27.07.15.
 * Execute an HTTP request to get JSON information and return into a java class
 */
public class GetTask<T extends Serializable> extends AsyncTask<Void, Void, T> {

    private Uri mUrl;
    private RequestCallback<T> mCallback;
    private Type mClass;
    private RequestCallback.Errors error;
    private String mKey;

    public GetTask(Uri url, Type type, RequestCallback<T> callback) {

        mCallback = callback;
        mUrl = url;
        mClass = type;
        error = RequestCallback.Errors.SUCCESS;
    }

    public void setCacheKey(String key) {
        mKey = key;
    }

    @Override
    protected T doInBackground(Void... voids) {

        // Try to acquire from the cache
        if (mKey != null) {
            T object = CacheManager.instance().get(mKey);
            if (object != null) {
                return object;
            }
        }

        try {
            URL url = new URL(mUrl.toString());
            final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            // Timeout only for the connection, after that the data are computed depending of the device
            connection.setConnectTimeout(Constants.TIMEOUT * 1000);
            Log.d("__INTERNET__", "Connection open for params:" + url);

            // Check before heavy action that the request isn't cancelled
            if (isCancelled()) {
                return null;
            }

            try {

                T object = MessageParser.fromJson(new InputStreamReader(connection.getInputStream()), mClass);
                if (object != null && mKey != null) {
                    CacheManager.instance().add(mKey, object, Constants.CACHE_EXPIRATION_TIME);
                }

                return object;

            } catch (JsonSyntaxException e) {

                error = RequestCallback.Errors.JSON;
                Log.e("Json error", "Message: " + e.getMessage());
                ACRA.getErrorReporter().handleSilentException(e);
            }

            return null;

        } catch (IOException e) {

            Log.e("__CONNECTION_SERVICE__", "Message: " + e.getMessage());
            error = RequestCallback.Errors.CONNECTION;
        }

        return null;
    }

    @Override
    protected void onPostExecute(T result) {

        // Remove from the active request list
        ConnectionService.instance().removeRequest(this);

        // Use the mCallback here, because this function is executed in the UI Thread !
        if (result != null) {
            mCallback.onSuccess(result);
        } else {
            error = RequestCallback.Errors.NULL;
            mCallback.onFailure(error);
        }
    }

    @Override
    protected void onCancelled(T result) {

        error = RequestCallback.Errors.CANCELLATION;
        // Call the failure mCallback
        mCallback.onFailure(error);
        // Remove the task from the active requests list
        ConnectionService.instance().removeRequest(this);
    }
}
