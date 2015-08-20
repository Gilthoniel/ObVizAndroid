package com.obviz.review.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.obviz.review.managers.TopicsManager;
import com.obviz.review.models.AndroidApp;
import com.obviz.reviews.R;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.PieChartView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gaylor on 28.07.15.
 * Adapter to show the comparison between two apps
 */
public class ComparisonAdapter extends BaseAdapter implements TopicsManager.TopicsObserver {

    private AndroidApp mApplication;
    private AndroidApp mComparison;
    private LayoutInflater inflater;

    public ComparisonAdapter(Context context, AndroidApp application, AndroidApp comparison) {
        inflater = LayoutInflater.from(context);

        mApplication = application;
        mComparison = comparison;
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
    public Integer getItem(int position) {

        return mApplication.getOpinions().get(position).topicID;
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        int topicID = getItem(position);

        RelativeLayout layout;
        if (view != null) {
            layout = (RelativeLayout) view;
        } else {
            layout = (RelativeLayout) inflater.inflate(R.layout.comparison_item_row, parent, false);
        }

        PieChartView pieApp = (PieChartView) layout.findViewById(R.id.pie_app);
        populatePieChart(pieApp, position, mApplication);

        PieChartView pieComparison = (PieChartView) layout.findViewById(R.id.pie_comparison);
        populatePieChart(pieComparison, position, mComparison);

        TextView topicTitle = (TextView) layout.findViewById(R.id.topic_title);
        topicTitle.setText(TopicsManager.instance().getTitle(topicID, this));
        // Set the color of the most appreciate app
        if (mApplication.getOpinions().get(position).percentage() > mComparison.getOpinions().get(position).percentage()) {
            topicTitle.setTextColor(parent.getResources().getColor(R.color.appColor));
        } else {
            topicTitle.setTextColor(parent.getResources().getColor(R.color.comparisonColor));
        }

        return layout;
    }

    /* PRIVATE */

    private void populatePieChart(PieChartView chart, int position, AndroidApp app) {

        int value = app.getOpinions().get(position).percentage();

        List<SliceValue> values = new ArrayList<>();
        values.add(new SliceValue(value, ChartUtils.COLOR_GREEN));
        values.add(new SliceValue(100 - value, ChartUtils.COLOR_RED));

        /** Options for the Pie Chart **/
        PieChartData data = new PieChartData(values);
        data.setHasLabels(false);
        data.setHasCenterCircle(true);
        data.setSlicesSpacing(3);
        data.setCenterCircleScale(0.9f);

        chart.setPieChartData(data);
        chart.setCircleFillRatio(0.8f);
        chart.setChartRotationEnabled(false);
        chart.setValueTouchEnabled(false);
        chart.setChartRotation(-45, false);
    }
}
