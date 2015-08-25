package com.obviz.review.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import com.obviz.review.Constants;
import com.obviz.review.managers.TopicsManager;
import com.obviz.review.models.AndroidApp;
import com.obviz.review.views.GaugeChart;
import com.obviz.review.views.GaugeChart.Arrow;
import com.obviz.review.views.GaugeChart.GaugeChartData;
import com.obviz.reviews.R;

import java.util.List;

/**
 * Created by gaylor on 22.07.15.
 * Adapter for the gridview of the details activity where you find the gauge charts
 */
public class GaugeAdapter extends BaseAdapter implements TopicsManager.TopicsObserver {

    private LayoutInflater inflater;
    private List<AndroidApp> mApplications;

    public GaugeAdapter(Context context) {

        inflater = LayoutInflater.from(context);

        GaugeAdaptable activity = (GaugeAdaptable) context;
        mApplications = activity.getListApplications();
    }

    public AndroidApp getApplication() {

        return mApplications.get(0);
    }

    /* TopicsObserver implementation */
    @Override
    public void onTopicsLoaded() {

        notifyDataSetChanged();
    }

    @Override
    public int getCount() {

        if (mApplications.size() > 0) {

            return mApplications.get(0).getOpinions().size();
        } else {

            return 0;
        }
    }

    @Override
    public Integer getItem(int i) {

        if (mApplications.size() > 0) {

            return mApplications.get(0).getOpinions().get(i).percentage();
        } else {

            return 0;
        }
    }

    @Override
    public long getItemId(int i) {

        if (mApplications.size() > 0) {
            return mApplications.get(0).getOpinions().get(i).topicID;
        } else {

            return -1;
        }
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {

        LinearLayout layout;
        if (view == null) {

            layout = (LinearLayout) inflater.inflate(R.layout.item_gauge, parent, false);
        } else {

            layout = (LinearLayout) view;
        }

        GaugeChart gauge = (GaugeChart) layout.findViewById(R.id.gauge_chart);
        GaugeChartData data = new GaugeChartData(100);
        data.setText(TopicsManager.instance().getTitle((int) getItemId(position), this));
        data.addSegments(Constants.CHART_SEGMENTS);

        for (AndroidApp app : mApplications) {

            if (app.getOpinions().size() > position) {
                Arrow arrow = new Arrow();
                arrow.setValue(app.getOpinions().get(position).percentage());
                arrow.setHeight(1.0f);
                arrow.setInnerRadius(0.4f);
                arrow.setBaseLength(5);

                if (app == mApplications.get(0)) {
                    arrow.setColor(parent.getResources().getColor(R.color.appColor));
                } else {
                    arrow.setColor(parent.getResources().getColor(R.color.comparisonColor));
                }

                data.addArrow(arrow);
            }
        }

        gauge.setData(data);

        return layout;
    }

    public interface GaugeAdaptable {
        List<AndroidApp> getListApplications();
    }
}
