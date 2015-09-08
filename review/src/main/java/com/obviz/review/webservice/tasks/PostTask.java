package com.obviz.review.webservice.tasks;

import android.net.Uri;
import android.util.Log;
import com.google.gson.JsonSyntaxException;
import com.obviz.review.Constants;
import com.obviz.review.webservice.ConnectionService;
import com.obviz.review.webservice.RequestCallback;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.*;

/**
 * Created by gaylor on 08/21/2015.
 *
 */
public class PostTask extends HttpTask<Boolean> {
    private Future<Boolean> mFuture;
    private RequestCallback<Boolean> callback;
    private RequestCallback.Errors error;

    public PostTask(Uri.Builder builder, RequestCallback<Boolean> callback) {
        super(builder);

        this.callback = callback;

        error = RequestCallback.Errors.SUCCESS;

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
    protected Boolean doInBackground(Void... voids) {

        try {
            final Uri uri = mUrl.build();
            URL url = new URL(uri.getPath());
            final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            Log.d("__INTERNET__", "Post request with params:" + uri);

            mFuture = ConnectionService.instance().getExecutor().submit(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    try {

                        String body = uri.getEncodedQuery();
                        connection.setDoOutput(true);
                        connection.setFixedLengthStreamingMode(body.length());

                        OutputStream output = connection.getOutputStream();
                        IOUtils.write(body, output, "UTF-8");
                        output.flush();
                        output.close();

                        return connection.getResponseCode() == 200;

                    } catch (IOException e) {

                        Log.e("__POST__", "IOException during POST request : " + e.getMessage());

                        return false;
                    } finally {

                        if (connection != null) {
                            connection.disconnect();
                        }
                    }
                }
            });

            try {
                return mFuture.get(Constants.TIMEOUT, TimeUnit.SECONDS);

            } catch (TimeoutException | CancellationException | ExecutionException | InterruptedException ignored) {} finally {

                connection.disconnect();
            }
        } catch (IOException e) {

            error = RequestCallback.Errors.CONNECTION;
            Log.e("IO Exception", "Message: " + e.getMessage());

        }

        return false;
    }

    @Override
    protected void onPostExecute(Boolean result) {

        // Remove from the active request list
        ConnectionService.instance().removeRequest(this);

        // Use the callback here, because this function is executed in the UI Thread !
        if (result) {

            Log.d("__POST__", "Post request successfully executed");
            callback.onSuccess(true);
        } else {

            Log.d("__POST__", "Post request failed");
            callback.onFailure(error);
        }
    }

    @Override
    protected void onCancelled(Boolean result) {

        ConnectionService.instance().removeRequest(this);
    }
}
