package com.obviz.review.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.obviz.review.ReviewsComparisonActivity;
import com.obviz.review.adapters.ReviewsAdapter;
import com.obviz.review.models.AndroidApp;
import com.obviz.review.webservice.GeneralWebService;
import com.obviz.review.webservice.RequestObserver;
import com.obviz.reviews.R;

/**
 * Created by gaylor on 29.07.15.
 *
 */
public class ComparisonReviewsFragment extends ListFragment implements RequestObserver<AndroidApp> {

    private ReviewsAdapter mAdapter;
    private int mType;

    public void setType(int type) {
        mType = type;
    }

    public int getType() {
        return mType;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle states) {

        return inflater.inflate(R.layout.list_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle states) {
        super.onActivityCreated(states);

        ReviewsComparisonActivity parent = (ReviewsComparisonActivity) getActivity();

        // Set the adapter before subscribe in the observers to avoid null pointer
        mAdapter = new ReviewsAdapter(getActivity(), Integer.parseInt(parent.getTopicID()));
        setListAdapter(mAdapter);

        parent.addApplicationObserver(this);
    }

    @Override
    public void onSuccess(AndroidApp app) {

        GeneralWebService.getInstance().getReviews(app.getAppID(), getListView());
    }
}
