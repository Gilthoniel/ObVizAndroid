package com.obviz.review.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Spinner;
import com.obviz.review.Constants;
import com.obviz.review.DetailsActivity;
import com.obviz.review.adapters.TrendingAdapter;
import com.obviz.review.webservice.GeneralWebService;
import com.obviz.reviews.R;

/**
 * Created by gaylor on 05-Aug-15.
 *
 */
public class TrendingFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle states) {

        View view = inflater.inflate(R.layout.trending_fragment, parent, false);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle states) {
        super.onActivityCreated(states);

        if (getView() != null) {
            final GridView grid = (GridView) getView().findViewById(R.id.grid_view);
            final TrendingAdapter trendingAdapter = new TrendingAdapter(getActivity());
            grid.setAdapter(trendingAdapter);

            grid.setEmptyView(getView().findViewById(R.id.empty_view));
            grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent = new Intent(getActivity(), DetailsActivity.class);
                    intent.putExtra(Constants.INTENT_APP_ID, trendingAdapter.getItem(i).getAppID());
                }
            });

            GeneralWebService.getInstance().getTrendingApps(grid, new String[]{});

            /* Categories selection */
            Spinner spinner = (Spinner) getView().findViewById(R.id.spinner);
            final ArrayAdapter<Constants.Category> adapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, Constants.Category.values());
            adapter.setDropDownViewResource(R.layout.spinner_item_dropdown);

            spinner.setAdapter(adapter);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    GeneralWebService.getInstance().getTrendingApps(grid, new String[]{
                            adapter.getItem(i).name()
                    });
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    // Nothing
                }
            });
        }
    }
}
