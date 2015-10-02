package com.obviz.review.webservice.tasks;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import com.obviz.review.webservice.ConnectionService;
import com.obviz.review.webservice.RequestCallback;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by gaylor on 08/21/2015.
 *
 */
public class PostTask extends AsyncTask<Void, Void, Boolean> {

    private Uri mUrl;
    private RequestCallback<Boolean> mCallback;
    private RequestCallback.Errors error;

    public PostTask(Uri url, RequestCallback<Boolean> callback) {

        mCallback = callback;
        mUrl = url;

        error = RequestCallback.Errors.SUCCESS;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {

        try {
            URL url = new URL(mUrl.getPath());
            final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            Log.d("__INTERNET__", "Post request with params:" + url.toString());

            try {

                String body = mUrl.getEncodedQuery();
                connection.setDoOutput(true);
                connection.setFixedLengthStreamingMode(body.length());

                if (isCancelled()) {
                    return false;
                }

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

        // Use the mCallback here, because this function is executed in the UI Thread !
        if (result) {

            Log.d("__POST__", "Post request successfully executed");
            mCallback.onSuccess(true);
        } else {

            Log.d("__POST__", "Post request failed");
            mCallback.onFailure(error);
        }
    }

    @Override
    protected void onCancelled(Boolean result) {

        ConnectionService.instance().removeRequest(this);
    }
}
