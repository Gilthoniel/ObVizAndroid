package com.obviz.review.service;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import com.obviz.review.Constants;
import com.obviz.review.json.MessageParser;
import org.acra.collector.CrashReportData;
import org.acra.sender.ReportSender;
import org.acra.sender.ReportSenderException;
import org.acra.util.JSONReportBuilder;
import org.apache.commons.io.IOUtils;
import org.json.JSONException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by gaylor on 10/02/2015.
 * Send reports to the back-end
 */
public class JsonReportSender implements ReportSender {
    @Override
    public void send(Context context, CrashReportData data) throws ReportSenderException {
        try {

            Log.d("--REPORT--", "Start sending report");
            sendReport(data.toJSON().toString());

        } catch (JSONReportBuilder.JSONReportException e) {

            Log.e("--REPORT--", "Error when trying to send report: "+e.getLocalizedMessage());
        }
    }

    private void sendReport(String json) {

        Uri.Builder builder = new Uri.Builder();
        builder.encodedPath(Constants.URL);
        builder.appendQueryParameter("cmd", Constants.REPORT_BUG);
        builder.appendQueryParameter("bug", json);

        Uri uri = builder.build();

        try {
            URL url = new URL(uri.getPath());
            final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            Log.d("__INTERNET__", "Post request:" + uri.toString());

            try {

                String body = uri.getEncodedQuery();
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");
                connection.setFixedLengthStreamingMode(body.length());

                Writer writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
                writer.write(body);
                writer.flush();
                writer.close();

                if (connection.getResponseCode() != 200) {
                    Log.e("--REPORT--", "Bad response code: "+connection.getResponseCode());
                    return;
                }

            } catch (IOException e) {

                Log.e("__POST__", "IOException during POST request : " + e.toString());
                Log.e("__POST__", "Cause: "+e.getCause().toString());

            } finally {

                if (connection != null) {
                    connection.disconnect();
                }
            }
        } catch (IOException e) {

            Log.e("IO Exception", "Message: " + e.getMessage());

        }

        Log.d("--REPORT--", "Report sent with no errors");
    }
}
