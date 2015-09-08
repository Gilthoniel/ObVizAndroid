package com.obviz.review.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import com.obviz.review.Constants;
import com.obviz.review.DetailsActivity;
import com.obviz.review.adapters.AppBoxAdapter;
import com.obviz.review.adapters.GridAdapter;
import com.obviz.review.adapters.SuperCategoryAdapter;
import com.obviz.review.managers.TutorialManager;
import com.obviz.review.views.GridRecyclerView;
import com.obviz.review.webservice.GeneralWebService;
import com.obviz.review.webservice.tasks.HttpTask;
import com.obviz.reviews.R;

/**
 * Created by gaylor on 05-Aug-15.
 *
 */
public class TrendingFragment extends Fragment implements HomeFragment {

    private HttpTask<?> request;
    private Context mContext;
    private boolean mTutoRequested = false;

    @Override
    public void showTutorial() {
        if (getView() == null) {
            mTutoRequested = true;
            return;
        } else {
            mTutoRequested = false;
        }

        TutorialManager.single((Activity) mContext)
                .setTarget(getView().findViewById(R.id.spinner))
                .setContentText(getResources().getString(R.string.tutorial_home_content))
                .singleUse(Constants.TUTORIAL_HOME_KEY)
                .show();
    }

    @Override
    public String getTitle() {

        return "Trending";
    }

    @Override
    public int getIcon() {
        return R.drawable.ic_trending_up_black_24dp;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle states) {

        return inflater.inflate(R.layout.fragment_trending, parent, false);
    }

    @Override
    public void onActivityCreated(Bundle states) {
        super.onActivityCreated(states);

        if (getView() == null) {
            return;
        }

        final GridRecyclerView grid = (GridRecyclerView) getView().findViewById(R.id.grid_view);
        final AppBoxAdapter trendingAdapter = new AppBoxAdapter();
        // Launch the details activity when the user click on a app box
        trendingAdapter.addOnItemClickListener(new GridAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                Intent intent = new Intent(getActivity(), DetailsActivity.class);
                intent.putExtra(Constants.INTENT_APP, (Parcelable) trendingAdapter.getItem(position));

                startActivity(intent);
            }
        });

        // Define the maximum number of displayed items related to the screen size
        int max;
        switch (getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) {
            case Configuration.SCREENLAYOUT_SIZE_LARGE:
                max = 20;
                break;
            case Configuration.SCREENLAYOUT_SIZE_XLARGE:
                max = 30;
                break;
            default:
                max = 10;
        }
        trendingAdapter.setMax(max);

        grid.setAdapter(trendingAdapter);

        /* Categories selection */
        final Spinner spinner = (Spinner) getView().findViewById(R.id.spinner);
        final SuperCategoryAdapter adapter = new SuperCategoryAdapter(getActivity());

        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                if (spinner.getTag() == null || (Integer) spinner.getTag() != position) {
                    spinner.setTag(position);

                    // If a request is already launch, we cancelled it before
                    if (request != null) {
                        request.cancel();
                    }
                    request = GeneralWebService.instance().getTrendingApps(trendingAdapter, adapter.getItem(position));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // If the tutorial has been called before the view is created
        if (mTutoRequested) {
            showTutorial();
        }
    }
}
