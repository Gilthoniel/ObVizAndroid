package com.obviz.review;

import android.app.Application;
import com.obviz.review.database.DatabaseService;
import com.obviz.review.managers.CacheManager;
import com.obviz.review.managers.CategoryManager;
import com.obviz.review.managers.TopicsManager;
import com.obviz.review.service.AlarmTaskReceiver;
import com.obviz.review.service.JsonReportSender;
import com.obviz.review.views.GaugeChart;
import com.obviz.review.webservice.ConnectionService;
import com.obviz.review.webservice.GeneralWebService;
import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.annotation.ReportsCrashes;

/**
 * Created by gaylor on 08/11/2015.
 * Base application to initiate the singletons
 */
@ReportsCrashes(customReportContent = {
        ReportField.ANDROID_VERSION,
        ReportField.APP_VERSION_CODE,
        ReportField.PHONE_MODEL,
        ReportField.TOTAL_MEM_SIZE,
        ReportField.AVAILABLE_MEM_SIZE,
        ReportField.STACK_TRACE,
        ReportField.INITIAL_CONFIGURATION,
        ReportField.CRASH_CONFIGURATION,
        ReportField.DISPLAY,
        ReportField.USER_CRASH_DATE,
        ReportField.SHARED_PREFERENCES
})
public class ObvizApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ConnectionService.init(getApplicationContext());
        GeneralWebService.init();
        CategoryManager.init(getApplicationContext());
        DatabaseService.init(getApplicationContext());
        CacheManager.init(getApplicationContext());
        TopicsManager.init(getApplicationContext());

        // Add segments for the gauges
        Constants.CHART_SEGMENTS.add(new GaugeChart.Segment(0, 20, 0xffcc4748, 0.8f));
        Constants.CHART_SEGMENTS.add(new GaugeChart.Segment(20, 40, 0xffcf6868, 0.8f));
        Constants.CHART_SEGMENTS.add(new GaugeChart.Segment(40, 60, 0xffe6e6e6, 0.8f));
        Constants.CHART_SEGMENTS.add(new GaugeChart.Segment(60, 80, 0xffa5d1a5, 0.8f));
        Constants.CHART_SEGMENTS.add(new GaugeChart.Segment(80, 100, 0xff84b761, 0.8f));

        // Set the alarm
        AlarmTaskReceiver.setAlarm(getApplicationContext());

        ACRA.init(this);
        ACRA.getErrorReporter().setReportSender(new JsonReportSender());
    }
}
