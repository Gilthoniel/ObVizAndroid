package com.obviz.review.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.obviz.review.Constants;
import com.obviz.review.adapters.ReviewsAdapter;
import com.obviz.review.models.AndroidApp;
import com.obviz.review.views.GridRecyclerView;
import com.obviz.review.webservice.GeneralWebService;
import com.obviz.reviews.R;

import java.util.logging.Logger;

/**
 * Created by gaylor on 29.07.15.
 * Display the reviews for the two apps and a specific topic
 */
public class ComparisonReviewsFragment extends Fragment {

    private View mParent;
    private AndroidApp mApplication;
    private int mTopicID;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle states) {

        if (states != null) {
            mApplication = states.getParcelable(Constants.STATE_APP);
        } else {
            mApplication = getArguments().getParcelable(Constants.STATE_APP);
            mTopicID = getArguments().getInt(Constants.STATE_TOPIC);
        }

        mParent = inflater.inflate(R.layout.grid_review, container, false);
        return mParent;
    }

    @Override
    public void onActivityCreated(Bundle states) {
        super.onActivityCreated(states);

        // Set the adapter before subscribe in the observers to avoid null pointer
        ReviewsAdapter adapter = new ReviewsAdapter(mApplication.getAppID(), mTopicID);

        GridRecyclerView mGridView = (GridRecyclerView) mParent.findViewById(R.id.grid_view);
        mGridView.setInfiniteAdapter(adapter);

        adapter.onLoadMore();
    }

    @Override
    public void onSaveInstanceState(Bundle states) {
        states.putParcelable(Constants.STATE_APP, mApplication);

        super.onSaveInstanceState(states);
    }

    public static ComparisonReviewsFragment newInstance(AndroidApp app, int topicID) {

        ComparisonReviewsFragment fragment = new ComparisonReviewsFragment();

        Bundle args = new Bundle();
        args.putParcelable(Constants.STATE_APP, app);
        args.putInt(Constants.STATE_TOPIC, topicID);

        fragment.setArguments(args);
        return fragment;
    }
}
