package com.obviz.review.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.obviz.review.managers.ImagesManager;
import com.obviz.review.models.AndroidApp;
import com.obviz.reviews.R;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.PieChartView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by gaylor on 21.07.15.
 * Adapter for the results of a search
 */
public class ListAppAdapter extends BaseAppAdapter {

    private Context mContext;

    public ListAppAdapter(Context context) {

        mContext = context;
    }

    public Context getContext() {
        return mContext;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        RelativeLayout layout;
        if (view == null) {
            layout = (RelativeLayout) LayoutInflater.from(mContext).inflate(R.layout.results_item, parent, false);
        } else {
            layout = (RelativeLayout) view;
        }

        AndroidApp app = getItem(position);

        TextView name = (TextView) layout.findViewById(R.id.app_name);
        name.setText(app.getName());

        TextView developer = (TextView) layout.findViewById(R.id.app_developer);
        developer.setText(app.getDeveloper());

        TextView category = (TextView) layout.findViewById(R.id.app_category);
        category.setText(app.getCategory().getTitle());

        ImageView logo = (ImageView) layout.findViewById(R.id.app_logo);
        if (mImages.containsKey(app.getImage())) {
            logo.setImageBitmap(mImages.get(app.getImage()));
        } else {
            logo.setImageBitmap(null);

            // Load the image only once to avoid infinite loop on the cache
            if (!mImages.containsKey(app.getImage())) {
                mImages.put(app.getImage(), null);
                ImagesManager.getInstance().get(app.getImage(), this);
            }
        }

        // TODO
        Random random = new Random();
        int value = random.nextInt(100);
        PieChartView gauge = (PieChartView) layout.findViewById(R.id.gauge_chart);
        List<SliceValue> values = new ArrayList<>();
        values.add(new SliceValue(value, ChartUtils.COLOR_GREEN));
        values.add(new SliceValue(100 - value, ChartUtils.COLOR_RED));

        PieChartData data = new PieChartData(values);
        data.setHasLabels(false);
        data.setHasCenterCircle(true);
        data.setSlicesSpacing(2);
        data.setCenterCircleScale(0.8f);

        gauge.setPieChartData(data);
        gauge.setCircleFillRatio(0.9f);

        return layout;
    }
}
