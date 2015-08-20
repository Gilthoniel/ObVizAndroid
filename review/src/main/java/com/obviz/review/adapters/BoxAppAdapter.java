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
import com.obviz.reviews.R;

import java.util.*;

/**
 * Created by gaylor on 05-Aug-15.
 * Adapter for the trending applications on the home activity
 */
public class BoxAppAdapter extends BaseAppAdapter {

    private Context mContext;

    public BoxAppAdapter(Context context) {

        mContext = context;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        RelativeLayout layout;
        if (view != null) {
            layout = (RelativeLayout) view;
        } else {
            layout = (RelativeLayout) LayoutInflater.from(mContext).inflate(R.layout.trending_app_item, parent, false);
        }

        AndroidApp app = getItem(position);

        TextView name = (TextView) layout.findViewById(R.id.app_name);
        name.setText(app.getName());

        TextView developer = (TextView) layout.findViewById(R.id.app_developer);
        developer.setText(app.getDeveloper());

        TextView category = (TextView) layout.findViewById(R.id.app_category);
        category.setText(app.getCategory().getTitle());

        RatingBar rating = (RatingBar) layout.findViewById(R.id.rating);
        rating.setRating(app.getScore().getTotal());

        TextView freedom = (TextView) layout.findViewById(R.id.app_freedom);
        if (app.isFree()) {
            freedom.setText("Free");
        } else {
            freedom.setText("Paid");
        }

        ImageView logo = (ImageView) layout.findViewById(R.id.app_logo);
        if (mImages.containsKey(app.getImage())) {
            logo.setImageBitmap(mImages.get(app.getImage()));
        } else {
            mImages.put(app.getImage(), null);
            ImagesManager.getInstance().get(app.getImage(), this);
        }

        return layout;
    }
}
