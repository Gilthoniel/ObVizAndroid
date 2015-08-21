package com.obviz.review.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
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
public class ComparisonReviewsFragment extends Fragment implements RequestObserver<AndroidApp> {

    private View mParent;
    private GridView mGridView;
    private int mType;

    public void setType(int type) {
        mType = type;
    }

    public int getType() {
        return mType;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle states) {

        mParent = inflater.inflate(R.layout.grid_review, container, false);
        return mParent;
    }

    @Override
    public void onActivityCreated(Bundle states) {
        super.onActivityCreated(states);

        ReviewsComparisonActivity parent = (ReviewsComparisonActivity) getActivity();

        // Set the adapter before subscribe in the observers to avoid null pointer
        ReviewsAdapter adapter = new ReviewsAdapter(getActivity());

        mGridView = (GridView) mParent.findViewById(R.id.grid_view);
        mGridView.setAdapter(adapter);
        mGridView.setEmptyView(mParent.findViewById(android.R.id.empty));

        parent.addApplicationObserver(this);
    }

    @Override
    public void onSuccess(AndroidApp app) {

        GeneralWebService.instance().getReviews(app.getAppID(), 1, 0, 10, mGridView);
    }
}
