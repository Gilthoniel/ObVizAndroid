package com.obviz.review.webservice.tasks;

import android.net.Uri;
import android.util.Log;
import com.google.gson.JsonSyntaxException;
import com.obviz.review.Constants;
import com.obviz.review.managers.CacheManager;
import com.obviz.review.webservice.ConnectionService;
import com.obviz.review.json.MessageParser;
import com.obviz.review.webservice.RequestCallback;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.*;

/**
 * Created by gaylor on 27.07.15.
 * Execute an HTTP request to get JSON information and return into a java class
 */
public class GetTask<T> extends HttpTask<T> {

    private Future<T> mFuture;
    private RequestCallback<T> callback;
    private RequestCallback.Errors error;
    private String mKey;

    public GetTask(RequestCallback<T> callback, String cacheKey) {
        this.callback = callback;

        error = RequestCallback.Errors.SUCCESS;
        mKey = cacheKey;

        mFuture = null;
    }

    @Override
    public void cancel() {

        if (mFuture != null) {
            mFuture.cancel(true);
            Log.d("__FUTURE__", "Future cancelled");
        }

        super.cancel(true);
    }

    @Override
    protected T doInBackground(final Uri.Builder... builders) {
        // Try to acquire from the cache
        if (mKey != null) {
            T object = CacheManager.instance().get(mKey);
            if (object != null) {
                return object;
            }
        }

        if (builders.length <= 0) {
            return null;
        }

        try {
            URL url = new URL(builders[0].build().toString());
            final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            Log.d("__INTERNET__", "Connection open for params:" + url);

            try {
                mFuture = ConnectionService.instance.getExecutor().submit(new Callable<T>() {
                    @Override
                    public T call() throws Exception {

                        try {

                            T object = MessageParser.fromJson(new InputStreamReader(connection.getInputStream()), callback.getType());
                            if (object != null && mKey != null) {
                                CacheManager.instance().add(mKey, object, Constants.CACHE_EXPIRATION_TIME);
                            }

                            return object;

                        } catch (JsonSyntaxException e) {

                            error = RequestCallback.Errors.JSON;
                            Log.e("Json error", "Message: " + e.getMessage());

                        }

                        return null;
                    }
                });

                return mFuture.get(Constants.TIMEOUT, TimeUnit.SECONDS);

            } catch (ExecutionException e) {

                Log.i("__FUTURE__", "Future cancelled: "+e.getCause().getMessage());
                for (StackTraceElement trace : e.getCause().getStackTrace()) {
                    Log.i("__FUTURE__", "-> "+trace);
                }

            } catch (TimeoutException e) {

                Log.i("__FUTURE__", "Future timeout.");
                error = RequestCallback.Errors.CONNECTION;

            } catch (InterruptedException | CancellationException ignored) {} finally {

                connection.disconnect();
            }

        } catch (IOException e) {

            Log.e("__CONNECTION_SERVICE__", "Message: " + e.getMessage());
            error = RequestCallback.Errors.CONNECTION;
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
            error = RequestCallback.Errors.NULL;
            callback.onFailure(error);
        }
    }

    @Override
    protected void onCancelled(T result) {

        error = RequestCallback.Errors.CANCELLATION;
        callback.onFailure(error);
        ConnectionService.instance.removeRequest(this);
    }
}
