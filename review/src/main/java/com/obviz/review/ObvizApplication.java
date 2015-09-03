package com.obviz.review;

import android.app.Application;
import com.obviz.review.database.DatabaseService;
import com.obviz.review.managers.CacheManager;
import com.obviz.review.managers.CategoryManager;
import com.obviz.review.managers.ImagesManager;
import com.obviz.review.managers.TopicsManager;
import com.obviz.review.service.AlarmTaskReceiver;
import com.obviz.review.views.GaugeChart;
import com.obviz.review.webservice.GeneralWebService;

/**
 * Created by gaylor on 08/11/2015.
 * Base application to initiate the singletons
 */
public class ObvizApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        GeneralWebService.init();
        CategoryManager.init(getApplicationContext());
        DatabaseService.init(getApplicationContext());
        CacheManager.init(getApplicationContext());
        TopicsManager.init();
        ImagesManager.init();

        // Add segments for the gauges
        Constants.CHART_SEGMENTS.add(new GaugeChart.Segment(0, 20, 0xffcc4748, 0.8f));
        Constants.CHART_SEGMENTS.add(new GaugeChart.Segment(20, 40, 0xffcf6868, 0.8f));
        Constants.CHART_SEGMENTS.add(new GaugeChart.Segment(40, 60, 0xffe6e6e6, 0.8f));
        Constants.CHART_SEGMENTS.add(new GaugeChart.Segment(60, 80, 0xffa5d1a5, 0.8f));
        Constants.CHART_SEGMENTS.add(new GaugeChart.Segment(80, 100, 0xff84b761, 0.8f));

        // Set the alarm
        AlarmTaskReceiver.setAlarm(getApplicationContext());
    }
}
