package com.obviz.review.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import com.obviz.review.Constants;
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
public class ComparisonReviewsFragment extends Fragment {

    private View mParent;
    private GridView mGridView;
    private AndroidApp mApplication;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle states) {

        if (states != null) {
            mApplication = states.getParcelable(Constants.STATE_APP);
        } else {
            mApplication = getArguments().getParcelable(Constants.STATE_APP);
        }

        mParent = inflater.inflate(R.layout.grid_review, container, false);
        return mParent;
    }

    @Override
    public void onActivityCreated(Bundle states) {
        super.onActivityCreated(states);

        // Set the adapter before subscribe in the observers to avoid null pointer
        ReviewsAdapter adapter = new ReviewsAdapter(getActivity());

        mGridView = (GridView) mParent.findViewById(R.id.grid_view);
        mGridView.setAdapter(adapter);
        mGridView.setEmptyView(mParent.findViewById(android.R.id.empty));

        GeneralWebService.instance().getReviews(mApplication.getAppID(), 1, 0, 10, mGridView);
    }

    @Override
    public void onSaveInstanceState(Bundle states) {
        states.putParcelable(Constants.STATE_APP, mApplication);

        super.onSaveInstanceState(states);
    }

    public static ComparisonReviewsFragment newInstance(AndroidApp app) {

        ComparisonReviewsFragment fragment = new ComparisonReviewsFragment();

        Bundle args = new Bundle();
        args.putParcelable(Constants.STATE_APP, app);

        fragment.setArguments(args);
        return fragment;
    }
}
