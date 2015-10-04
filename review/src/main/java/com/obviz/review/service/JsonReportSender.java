package com.obviz.review.service;

import android.content.Context;
import android.util.Log;
import org.acra.collector.CrashReportData;
import org.acra.sender.ReportSender;
import org.acra.sender.ReportSenderException;
import org.acra.util.JSONReportBuilder;

/**
 * Created by gaylor on 10/02/2015.
 * Send reports to the back-end
 */
public class JsonReportSender implements ReportSender {
    @Override
    public void send(Context context, CrashReportData data) throws ReportSenderException {
        try {
            Log.i("--REPORT--", data.toJSON().toString());
        } catch (JSONReportBuilder.JSONReportException e) {
            Log.e("--REPORT--", "Error when trying to send report: "+e.getLocalizedMessage());
        }
    }
}
