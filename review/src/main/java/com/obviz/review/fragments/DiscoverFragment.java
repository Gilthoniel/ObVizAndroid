package com.obviz.review.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.obviz.review.Constants;
import com.obviz.review.DiscoverAppsActivity;
import com.obviz.review.ReviewsActivity;
import com.obviz.review.adapters.GridAdapter;
import com.obviz.review.adapters.SuperCategoryGridAdapter;
import com.obviz.review.models.AndroidApp;
import com.obviz.review.models.CategoryBase;
import com.obviz.review.views.GridRecyclerView;
import com.obviz.reviews.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Created by gaylor on 05-Aug-15.
 *
 */
public class DiscoverFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle states) {
        //Log.d("Pointer", "We are in the DiscoverFragment onCreateView");
        return inflater.inflate(R.layout.fragment_discover, parent, false);
    }


    @Override
    public void onActivityCreated(Bundle states) {
        super.onActivityCreated(states);

        if (getView() != null) {
            final GridRecyclerView grid = (GridRecyclerView) getView().findViewById(R.id.grid_view);

            final SuperCategoryGridAdapter discoverAdapter = new SuperCategoryGridAdapter();

            //final AppBoxAdapter trendingAdapter = new AppBoxAdapter();

            discoverAdapter.addOnItemClickListener(new GridAdapter.OnItemClickListener() {
                @Override
                public void onClick(int position) {
                    CategoryBase cat = discoverAdapter.getItem(position);
                    discoverAdapter.clear();
                    if(cat.getCategories().size()>1)
                        discoverAdapter.getChildCategories(cat);
                    else
                    {
                        Intent intent = new Intent(getContext(), DiscoverAppsActivity.class);

                        intent.putExtra(Constants.INTENT_CATEGORY, (Parcelable) cat);

                        //TODO: pass the real topics here
                        ArrayList<Integer> topics = new ArrayList<Integer>(Arrays.asList(1));
                        //int[] topics = new int[1];
                        //topics[0]=1;
                        intent.putIntegerArrayListExtra(Constants.INTENT_TOPIC_IDS, topics);

                        //TODO: this will crash if the contents of the best apps array is null!
                        intent.putParcelableArrayListExtra(Constants.INTENT_APPS_BEST, new ArrayList<AndroidApp>(discoverAdapter.getBestApps(cat)));
                        intent.putParcelableArrayListExtra(Constants.INTENT_APPS_WORST, new ArrayList<AndroidApp>(discoverAdapter.getWorstApps(cat)));

                        intent.putIntegerArrayListExtra(Constants.INTENT_TOPIC_IDS, topics);

                        startActivity(intent);

                    }
                }
            });

            grid.setAdapter(discoverAdapter);


        }
    }
}
