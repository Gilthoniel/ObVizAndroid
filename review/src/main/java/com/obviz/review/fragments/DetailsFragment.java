package com.obviz.review.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.obviz.review.Constants;
import com.obviz.review.DetailsActivity;
import com.obviz.review.managers.CategoryManager;
import com.obviz.review.managers.ImageObserver;
import com.obviz.review.managers.ImagesManager;
import com.obviz.review.models.AndroidApp;
import com.obviz.reviews.R;

/**
 * Created by gaylor on 09/02/2015.
 * Fragment where you find the information about an app
 */
public class DetailsFragment extends Fragment implements ImageObserver, CategoryManager.CategoryObserver {

    private ImageView mImage;
    private TextView mCategory;
    private AndroidApp mApplication;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle states) {

        mApplication = getArguments().getParcelable(Constants.STATE_APP);
        if (mApplication == null) {
            throw new IllegalStateException("Arguments must be defined to instantiate this fragment");
        }

        RelativeLayout view = (RelativeLayout) inflater.inflate(R.layout.fragment_details, parent, false);

        mImage = (ImageView) view.findViewById(R.id.app_logo);
        mCategory = (TextView) view.findViewById(R.id.app_category);

        populateDetailsField(mApplication, view, this);

        return view;
    }

    @Override
    public void onImageLoaded(String url, Bitmap bitmap) {
        mImage.setImageBitmap(bitmap);
    }

    @Override
    public void onCategoriesLoaded() {
        mCategory.setText(CategoryManager.instance().getFrom(mApplication.getCategory(), this).title);
    }

    public  static <T extends ImageObserver & CategoryManager.CategoryObserver> void populateDetailsField(AndroidApp app, View view, T activity) {
        ImagesManager.getInstance().get(app.getLogo(), activity);

        TextView name = (TextView) view.findViewById(R.id.app_name);
        name.setText(app.getName());

        TextView developer = (TextView) view.findViewById(R.id.app_developer);
        developer.setText(app.getDeveloper());

        RatingBar rating = (RatingBar) view.findViewById(R.id.rating);
        rating.setRating(app.getScore().getTotal());

        TextView date = (TextView) view.findViewById(R.id.app_date);
        date.setText(app.getDate());

        TextView category = (TextView) view.findViewById(R.id.app_category);
        category.setText(CategoryManager.instance().getFrom(app.getCategory(), activity).title);

        TextView description = (TextView) view.findViewById(R.id.app_description);
        description.setText(app.getDescription());
    }
}
