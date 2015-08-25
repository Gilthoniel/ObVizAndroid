package com.obviz.review.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.obviz.review.Constants;
import com.obviz.review.managers.ImagesManager;
import com.obviz.review.managers.TopicsManager;
import com.obviz.review.models.AndroidApp;
import com.obviz.review.views.GaugeChart;
import com.obviz.reviews.R;

/**
 * Created by gaylor on 05-Aug-15.
 * Adapter for the trending applications on the home activity
 */
public class AppBoxAdapter extends AppBaseAdapter implements TopicsManager.TopicsObserver {

    private Context mContext;

    public AppBoxAdapter(Context context) {

        mContext = context;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        RelativeLayout layout;
        if (view != null) {
            layout = (RelativeLayout) view;
        } else {
            layout = (RelativeLayout) LayoutInflater.from(mContext).inflate(R.layout.item_app_box, parent, false);
        }

        AndroidApp app = getItem(position);

        TextView name = (TextView) layout.findViewById(R.id.app_name);
        name.setText(app.getName());

        ImageView logo = (ImageView) layout.findViewById(R.id.app_logo);
        if (mImages.containsKey(app.getLogo())) {
            logo.setImageBitmap(mImages.get(app.getLogo()));
        } else {
            mImages.put(app.getLogo(), null);
            ImagesManager.getInstance().get(app.getLogo(), this);
        }

        TextView bestOpinion = (TextView) layout.findViewById(R.id.mostOpinion);
        bestOpinion.setText(TopicsManager.instance().getTitle(app.getBestOpinion(), this));

        TextView worstOpinion = (TextView) layout.findViewById(R.id.worstOpinion);
        worstOpinion.setText(TopicsManager.instance().getTitle(app.getWorstOpinion(), this));

        GaugeChart.GaugeChartData gaugeData = new GaugeChart.GaugeChartData(100);
        gaugeData.setTextSize(8);
        gaugeData.addSegments(Constants.CHART_SEGMENTS);

        GaugeChart.Arrow arrow = new GaugeChart.Arrow();
        arrow.setValue(app.getGlobalOpinion());
        arrow.setHeight(1.0f);
        arrow.setInnerRadius(0.2f);
        arrow.setBaseLength(3);
        arrow.setColor(0xff393939);
        gaugeData.addArrow(arrow);

        GaugeChart chart = (GaugeChart) layout.findViewById(R.id.gauge_chart);
        chart.setData(gaugeData);

        return layout;
    }

    @Override
    public void onTopicsLoaded() {
        notifyDataSetChanged();
    }
}
