package com.obviz.review.adapters;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.obviz.review.Constants;
import com.obviz.review.managers.ImageObserver;
import com.obviz.review.managers.ImagesManager;
import com.obviz.review.managers.TopicsManager;
import com.obviz.review.models.AndroidApp;
import com.obviz.review.views.GaugeChart;
import com.obviz.reviews.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by gaylor on 05-Aug-15.
 * Adapter for the trending applications on the home activity
 */
public class AppBoxAdapter extends GridAdapter<AndroidApp> implements TopicsManager.TopicsObserver, ImageObserver {

    private Map<String,Bitmap> mImages;

    public AppBoxAdapter() {

        mImages = new HashMap<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_app_box, parent, false);
            return new AppHolder(v);
        } else {

            return super.onCreateViewHolder(parent, viewType);
        }
    }

    @Override
    public void onTopicsLoaded() {
        notifyDataSetChanged();
    }

    @Override
    public void onImageLoaded(String url, Bitmap bitmap) {
        mImages.put(url, bitmap);
        notifyDataSetChanged();
    }

    public class AppHolder extends GridAdapter<AndroidApp>.ViewHolder {

        private View mView;

        public AppHolder(View v) {
            super(v);

            mView = v;
        }

        @Override
        public void onPopulate(AndroidApp app) {
            TextView name = (TextView) mView.findViewById(R.id.app_name);
            name.setText(app.getName());

            ImageView logo = (ImageView) mView.findViewById(R.id.app_logo);
            if (mImages.containsKey(app.getLogo())) {
                logo.setImageBitmap(mImages.get(app.getLogo()));
            } else {
                mImages.put(app.getLogo(), null);
                ImagesManager.getInstance().get(app.getLogo(), AppBoxAdapter.this);
            }

            TextView bestOpinion = (TextView) mView.findViewById(R.id.mostOpinion);
            bestOpinion.setText(TopicsManager.instance().getTitle(app.getBestOpinion(), AppBoxAdapter.this));

            TextView worstOpinion = (TextView) mView.findViewById(R.id.worstOpinion);
            worstOpinion.setText(TopicsManager.instance().getTitle(app.getWorstOpinion(), AppBoxAdapter.this));

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

            GaugeChart chart = (GaugeChart) mView.findViewById(R.id.gauge_chart);
            chart.setData(gaugeData);

            if (!app.isParsed()) {
                chart.setVisibility(View.INVISIBLE);
                mView.findViewById(R.id.trending_up).setVisibility(View.INVISIBLE);
                mView.findViewById(R.id.trending_down).setVisibility(View.INVISIBLE);
            } else {
                chart.setVisibility(View.VISIBLE);
                mView.findViewById(R.id.trending_up).setVisibility(View.VISIBLE);
                mView.findViewById(R.id.trending_down).setVisibility(View.VISIBLE);
            }
        }
    }
}
