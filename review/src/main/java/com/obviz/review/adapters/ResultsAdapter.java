package com.obviz.review.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.obviz.review.managers.ImageObserver;
import com.obviz.review.managers.ImagesManager;
import com.obviz.review.models.AndroidApp;
import com.obviz.review.webservice.GeneralWebService;
import com.obviz.reviews.R;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.PieChartView;

import java.util.*;

/**
 * Created by gaylor on 21.07.15.
 * Adapter for the results of a search
 */
public class ResultsAdapter extends BaseAdapter implements ImageObserver {

    private List<AndroidApp> mApplications;
    private Map<String, Bitmap> mImages;
    private LayoutInflater mInflater;

    public ResultsAdapter(Context context) {
        mApplications = new ArrayList<>();
        mImages = new HashMap<>();

        mInflater = LayoutInflater.from(context);
    }

    /**
     * Execute a search with the query and populate the adapter
     * @param query the query
     */
    public void search(String query) {
        GeneralWebService.getInstance().searchApp(query, this);
    }

    public void addAll(List<AndroidApp> applications) {
        mApplications.addAll(applications);
        notifyDataSetChanged();

        // Get the logo's images
        for (AndroidApp app : mApplications) {
            ImagesManager.getInstance().get(app.getImage(), this);
        }
    }

    public void add(AndroidApp app) {
        mApplications.add(app);
        ImagesManager.getInstance().get(app.getImage(), this);
    }

    public void clear() {
        clearImages();
        mApplications.clear();
        notifyDataSetChanged();
    }

    public void clearImages() {
        mImages.clear();
    }

    public void notifyEndLoading() {
        notifyDataSetChanged();
    }

    public String getIDWithPosition(int position) {
        return mApplications.get(position).getAppID();
    }

    @Override
    public void onImageLoaded(String url, Bitmap bitmap) {

        mImages.put(url, bitmap);
        // notify that the image is loaded
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mApplications.size();
    }

    @Override
    public Object getItem(int position) {
        return mApplications.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        RelativeLayout layout;
        if (view == null) {
            layout = (RelativeLayout) mInflater.inflate(R.layout.results_item, parent, false);
        } else {
            layout = (RelativeLayout) view;
        }

        AndroidApp app = mApplications.get(position);

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
