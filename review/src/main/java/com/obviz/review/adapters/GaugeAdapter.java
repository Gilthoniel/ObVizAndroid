package com.obviz.review.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.obviz.review.Constants;
import com.obviz.review.ReviewsActivity;
import com.obviz.review.managers.TopicsManager;
import com.obviz.review.models.AndroidApp;
import com.obviz.review.models.OpinionValue;
import com.obviz.review.views.GaugeChart;
import com.obviz.review.views.GaugeChart.Arrow;
import com.obviz.review.views.GaugeChart.GaugeChartData;
import com.obviz.reviews.R;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

/**
 * Created by gaylor on 22.07.15.
 * Adapter for the gridview of the details activity where you find the gauge charts
 */
public class GaugeAdapter extends GridAdapter<OpinionValue> implements TopicsManager.TopicsObserver {

    private List<AndroidApp> mApplications;

    public GaugeAdapter(Context context) {

        GaugeAdaptable activity = (GaugeAdaptable) context;
        mApplications = activity.getListApplications();

        if (mApplications.size() > 0) {
            addAll(mApplications.get(0).getOpinions());
        }
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
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gauge, parent, false);
            return new GaugeHolder(v);
        } else {

            return super.onCreateViewHolder(parent, viewType);
        }
    }

    public class GaugeHolder extends GridAdapter<OpinionValue>.ViewHolder {

        private View mView;

        public GaugeHolder(View view) {
            super(view);

            mView = view;
        }

        @Override
        public void onPopulate(OpinionValue item) {

            GaugeChart gauge = (GaugeChart) mView.findViewById(R.id.gauge_chart);
            GaugeChartData data = new GaugeChartData(100);
            data.setText(TopicsManager.instance().getTitle(item.topicID, GaugeAdapter.this));
            data.addSegments(Constants.CHART_SEGMENTS);

            for (int i = 0; i < mApplications.size(); i++) {
                AndroidApp app = mApplications.get(i);

                for (OpinionValue opinion : app.getOpinions()) {

                    if (opinion.topicID == item.topicID) {
                        Arrow arrow = new Arrow();
                        arrow.setValue(opinion.percentage());
                        arrow.setHeight(1.0f);
                        arrow.setInnerRadius(0.4f);
                        arrow.setBaseLength(5);

                        if (i == 0) {
                            arrow.setColor(mView.getResources().getColor(R.color.appColor));
                        } else {
                            arrow.setColor(mView.getResources().getColor(R.color.comparisonColor));
                        }

                        data.addArrow(arrow);
                    }
                }
            }

            gauge.setData(data);
        }
    }

    public interface GaugeAdaptable {
        List<AndroidApp> getListApplications();
    }
}
