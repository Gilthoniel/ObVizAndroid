package com.obviz.review.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.obviz.review.Constants;
import com.obviz.review.DetailsActivity;
import com.obviz.review.DiscoverAppsActivity;

import com.obviz.review.ReviewsActivity;
import com.obviz.review.adapters.GaugeAdapter;
import com.obviz.review.adapters.GridAdapter;
import com.obviz.review.managers.TutorialManager;
import com.obviz.review.models.AndroidApp;
import com.obviz.review.models.OpinionValue;
import com.obviz.review.views.GridRecyclerView;
import com.obviz.reviews.R;

/**
 * Created by gaylor on 23.07.15.
 * Fragment of the Details Activity where we populate with Pie Chart of the opinions
 */
public class DetailsOpinionsFragment extends Fragment {

    GridRecyclerView mGridView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle states) {
        View view = inflater.inflate(R.layout.grid_app_box, container, false);

        mGridView = (GridRecyclerView) view.findViewById(R.id.grid_view);
        AndroidApp app = getArguments().getParcelable(Constants.STATE_APP);

        populateOpinions(mGridView, app);

        return view;
    }

    public static void populateOpinions(final GridRecyclerView grid, final AndroidApp app) {

        final GaugeAdapter adapter = new GaugeAdapter(grid.getContext());
        adapter.addOnItemClickListener(new GridAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                OpinionValue opinion = adapter.getItem(position);

                Intent intent = new Intent(grid.getContext(), DiscoverAppsActivity.class);

                intent.putExtra(Constants.INTENT_APP, (Parcelable) app);
                intent.putExtra(Constants.INTENT_TOPIC_ID, opinion.topicID);

                grid.getContext().startActivity(intent);
            }
        });
        grid.setAdapter(adapter);
    }
}
