package com.obviz.review.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import com.obviz.review.DetailsActivity;
import com.obviz.review.managers.TopicsManager;
import com.obviz.review.models.AndroidApp;
import com.obviz.review.views.GaugeChart;
import com.obviz.review.views.GaugeChart.*;
import com.obviz.reviews.R;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by gaylor on 22.07.15.
 * Adapter for the gridview of the details activity where you find the gauge charts
 */
public class GaugeAdapter extends BaseAdapter implements TopicsManager.TopicsObserver {

    private LayoutInflater inflater;
    private GridView mParent;
    private AndroidApp mApplication;
    private List<Segment> mChartSegments;

    public GaugeAdapter(Context context, GridView parent) {

        inflater = LayoutInflater.from(context);
        mParent = parent;

        DetailsActivity activity = (DetailsActivity) context;
        mApplication = activity.getAndroidApp();

        mChartSegments = new LinkedList<>();
        mChartSegments.add(new Segment(0, 20, 0xffcc4748, 0.8f));
        mChartSegments.add(new Segment(20, 40, 0xffcf6868, 0.8f));
        mChartSegments.add(new Segment(40, 60, 0xffe6e6e6, 0.8f));
        mChartSegments.add(new Segment(60, 80, 0xffa5d1a5, 0.8f));
        mChartSegments.add(new Segment(80, 100, 0xff84b761, 0.8f));
    }

    public AndroidApp getApplication() {
        return mApplication;
    }

    /* TopicsObserver implementation */
    @Override
    public void onTopicsLoaded() {

        notifyDataSetChanged();
    }

    @Override
    public int getCount() {

        return mApplication.getOpinions().size();
    }

    @Override
    public Integer getItem(int i) {

        return mApplication.getOpinions().get(i).percentage();
    }

    @Override
    public long getItemId(int i) {

        return mApplication.getOpinions().get(i).topicID;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {

        int value = 0;
        if (mApplication != null) {
            value = getItem(position);
        }

        LinearLayout layout;
        if (view == null) {

            layout = (LinearLayout) inflater.inflate(R.layout.details_gauge, parent, false);
        } else {

            layout = (LinearLayout) view;
        }

        GaugeChart gauge = (GaugeChart) layout.findViewById(R.id.gauge_chart);
        GaugeChartData data = new GaugeChartData(100);
        data.setText(TopicsManager.instance().getTitle((int) getItemId(position), this));
        data.setSegments(mChartSegments);

        Arrow arrow = new Arrow(1.0f, 0.4f, 30, 0xff333333);
        arrow.setValue(value);

        List<Arrow> arrows = new LinkedList<>();
        arrows.add(arrow);
        data.addArrows(arrows);

        gauge.setData(data);

        return layout;
    }
}
