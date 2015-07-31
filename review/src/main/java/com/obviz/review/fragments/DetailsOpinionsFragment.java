package com.obviz.review.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import com.obviz.review.Constants;
import com.obviz.review.ReviewsActivity;
import com.obviz.review.adapters.GaugeAdapter;
import com.obviz.review.models.AndroidApp;
import com.obviz.reviews.R;

/**
 * Created by gaylor on 23.07.15.
 * Fragment of the Details Activity where we populate with Pie Chart of the opinions
 */
public class DetailsOpinionsFragment extends Fragment {

    private GaugeAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle states) {
        View view = inflater.inflate(R.layout.details_opinions_tab, container, false);

        GridView grid = (GridView) view.findViewById(R.id.gridView);
        mAdapter = new GaugeAdapter(view.getContext(), grid);
        grid.setAdapter(mAdapter);

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(parent.getContext(), ReviewsActivity.class);

                intent.putExtra(Constants.INTENT_APP_ID, mAdapter.getApplication().getAppID());
                intent.putExtra(Constants.INTENT_TOPIC_ID, id);

                startActivity(intent);
            }
        });

        return view;
    }
}