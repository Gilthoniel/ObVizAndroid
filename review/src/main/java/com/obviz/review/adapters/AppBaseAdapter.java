package com.obviz.review.adapters;

import android.graphics.Bitmap;
import android.widget.BaseAdapter;
import com.obviz.review.managers.ImageObserver;
import com.obviz.review.models.AndroidApp;

import java.util.*;

/**
 * Created by gaylor on 08/12/2015.
 *
 */
public abstract class AppBaseAdapter extends BaseAdapter implements ImageObserver {

    protected Map<String, Bitmap> mImages;

    private List<AndroidApp> mApplications;
    private int maxItems;

    public AppBaseAdapter() {

        mApplications = new ArrayList<>();
        mImages = new HashMap<>();

        maxItems = 0;
    }

    public void addAll(Collection<? extends AndroidApp> collection) {

        for (AndroidApp app : collection) {
            mApplications.add(app);
        }

        notifyDataSetChanged();
    }

    public void add(AndroidApp app) {

        mApplications.add(app);
    }

    public void shuffle() {

        Collections.shuffle(mApplications);
    }

    public void clear() {

        mImages.clear();
        mApplications.clear();
        notifyDataSetChanged();
    }

    public void setMax(int max) {

        maxItems = max;
    }

    /* ImageObserver implementation */
    @Override
    public void onImageLoaded(String url, Bitmap bitmap) {
        mImages.put(url, bitmap);
        notifyDataSetChanged();
    }

    /* ArrayAdapter implementation */
    @Override
    public int getCount() {

        if (maxItems > 0 && mApplications.size() > maxItems) {

            return maxItems;
        } else {

            return mApplications.size();
        }
    }

    @Override
    public AndroidApp getItem(int i) {
        return mApplications.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }
}
