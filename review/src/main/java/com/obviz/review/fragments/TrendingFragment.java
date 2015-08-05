package com.obviz.review.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Spinner;
import com.obviz.review.Constants;
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

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setTitle("Categories");

        return view;
    }

    @Override
    public void onActivityCreated(Bundle states) {
        super.onActivityCreated(states);

        if (getView() != null) {
            GridView grid = (GridView) getView().findViewById(R.id.grid_view);
            grid.setAdapter(new TrendingAdapter(getActivity()));

            grid.setEmptyView(getView().findViewById(R.id.empty_view));

            GeneralWebService.getInstance().getTrendingApps(grid, new String[]{});

            /* Categories selection */
            Spinner spinner = (Spinner) getView().findViewById(R.id.spinner);
            spinner.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.spinner_item, Constants.Category.values()));
        }
    }
}
