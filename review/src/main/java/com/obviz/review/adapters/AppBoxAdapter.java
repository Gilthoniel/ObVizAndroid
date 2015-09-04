package com.obviz.review.adapters;

import android.graphics.Bitmap;
import android.util.Log;
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
 * Adapter for AndroidApp in a RecyclerView
 */
public class AppBoxAdapter extends GridAdapter<AndroidApp> implements TopicsManager.TopicsObserver, ImageObserver {

    private Map<String,Bitmap> mImages;

    public AppBoxAdapter() {

        mImages = new HashMap<>();
    }

    /**
     * Create the item view for the child or let the superclass return the good one
     * @param parent Parent of the Grid
     * @param viewType Type of the item which must be created
     * @return the child
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_app_box, parent, false);
            return new AppHolder(v);
        } else {

            return super.onCreateViewHolder(parent, viewType);
        }
    }

    /**
     * Update the view when the Topics are loaded
     */
    @Override
    public void onTopicsLoaded() {
        notifyDataSetChanged();
    }

    /**
     * Update the view when images are loaded
     * @param url of the image
     * @param bitmap the image itself
     */
    @Override
    public void onImageLoaded(String url, Bitmap bitmap) {
        mImages.put(url, bitmap); // fill the map
        notifyDataSetChanged(); // update the view
    }

    /**
     * Child of the adapter
     */
    public class AppHolder extends GridAdapter<AndroidApp>.ViewHolder {

        private View mView;

        public AppHolder(View v) {
            super(v);

            mView = v;
        }

        /**
         * Populate an AndroidApp item of a RecyclerView
         * @param app AndroidApp item for this child
         */
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
