package com.obviz.review.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import com.obviz.review.DetailsActivity;
import com.obviz.review.ReviewsActivity;
import com.obviz.review.fragments.DetailsOpinionsFragment;
import com.obviz.review.managers.TopicsManager;
import com.obviz.review.models.AndroidApp;
import com.obviz.review.webservice.GeneralWebService;
import com.obviz.review.webservice.RequestObserver;
import com.obviz.review.webservice.WebService;
import com.obviz.reviews.R;
import lecho.lib.hellocharts.listener.PieChartOnValueSelectListener;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.PieChartView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gaylor on 22.07.15.
 * Adapter for the gridview of the details activity where you find the gauge charts
 */
public class GaugeAdapter extends BaseAdapter implements RequestObserver<AndroidApp> {

    private LayoutInflater inflater;
    private GridView mParent;
    private AndroidApp mApplication;

    public GaugeAdapter(Context context, GridView parent) {
        inflater = LayoutInflater.from(context);

        mParent = parent;

        DetailsActivity activity = (DetailsActivity) context;
        activity.AddRequestObserver(this);
    }

    public AndroidApp getApplication() {
        return mApplication;
    }

    /* RequestObserver implementation */
    @Override
    public void onSuccess(AndroidApp app) {
        mApplication = app;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (mApplication != null) {
            return mApplication.getTopics().size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int i) {
        int topicID = TopicsManager.instance().getIDs().get(i);

        if (mApplication != null) {
            return mApplication.getTopics().get(String.valueOf(topicID));
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int i) {
        return TopicsManager.instance().getIDs().get(i);
    }

    @Override
    public View getView(final int i, View view, ViewGroup parent) {

        final int topicID = (int) getItemId(i);
        int value = 0;
        if (mApplication != null) {
            value = mApplication.getTopics().getInt(String.valueOf(topicID));
        }

        LinearLayout layout;
        if (view == null) {
            layout = (LinearLayout) inflater.inflate(R.layout.details_gauge, parent, false);
        } else {
            layout = (LinearLayout) view;
        }

        PieChartView gauge = (PieChartView) layout.findViewById(R.id.gauge_chart);
        List<SliceValue> values = new ArrayList<>();
        values.add(new SliceValue(value, ChartUtils.COLOR_GREEN));
        values.add(new SliceValue(100 - value, ChartUtils.COLOR_RED));

        PieChartData data = new PieChartData(values);
        data.setHasLabels(false);
        data.setHasCenterCircle(true);
        data.setSlicesSpacing(5);
        data.setCenterText1(TopicsManager.instance().getTopicTitle(topicID));
        data.setCenterText1FontSize(12);
        data.setCenterCircleScale(0.9f);

        gauge.setPieChartData(data);
        gauge.setCircleFillRatio(0.8f);
        gauge.setChartRotationEnabled(false);
        gauge.setValueTouchEnabled(false);
        gauge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GaugeAdapter.this.mParent.performItemClick(null, i, topicID);
            }
        });

        return layout;
    }
}
