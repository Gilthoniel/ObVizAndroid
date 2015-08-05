package com.obviz.review.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.obviz.review.managers.ImageObserver;
import com.obviz.review.managers.ImagesManager;
import com.obviz.review.models.AndroidApp;
import com.obviz.reviews.R;

import java.util.*;

/**
 * Created by gaylor on 05-Aug-15.
 * Adapter for the trending applications on the home activity
 */
public class TrendingAdapter extends BaseAdapter implements ImageObserver {

    private List<AndroidApp> mApplications;
    private Map<String, Bitmap> mImages;
    private Context mContext;

    public TrendingAdapter(Context context) {
        mApplications = new ArrayList<>();
        mImages = new HashMap<>();

        mContext = context;
    }

    public void addAll(Collection<AndroidApp> collection) {

        for (AndroidApp app : collection) {
            mApplications.add(app);
        }

        notifyDataSetChanged();
    }

    public void clear() {
        mApplications.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mApplications.size();
    }

    @Override
    public Object getItem(int i) {
        return mApplications.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        RelativeLayout layout;
        if (view != null) {
            layout = (RelativeLayout) view;
        } else {
            layout = (RelativeLayout) LayoutInflater.from(mContext).inflate(R.layout.trending_app_item, parent, false);
        }

        AndroidApp app = mApplications.get(position);

        TextView name = (TextView) layout.findViewById(R.id.app_name);
        name.setText(app.getName());

        ImageView logo = (ImageView) layout.findViewById(R.id.app_logo);
        if (mImages.containsKey(app.getImage())) {
            logo.setImageBitmap(mImages.get(app.getImage()));
        } else {
            mImages.put(app.getImage(), null);
            ImagesManager.getInstance().get(app.getImage(), this);
        }

        return layout;
    }

    @Override
    public void onImageLoaded(String url, Bitmap bitmap) {
        mImages.put(url, bitmap);
        notifyDataSetChanged();
    }
}
