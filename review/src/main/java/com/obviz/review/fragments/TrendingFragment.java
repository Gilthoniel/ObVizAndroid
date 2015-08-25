package com.obviz.review.fragments;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Spinner;
import com.obviz.review.Constants;
import com.obviz.review.DetailsActivity;
import com.obviz.review.adapters.AppBoxAdapter;
import com.obviz.review.managers.CategoryManager;
import com.obviz.review.managers.CategoryManager.*;
import com.obviz.review.webservice.ConnectionService;
import com.obviz.review.webservice.GeneralWebService;
import com.obviz.reviews.R;

/**
 * Created by gaylor on 05-Aug-15.
 *
 */
public class TrendingFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle states) {

        return inflater.inflate(R.layout.trending_fragment, parent, false);
    }

    @Override
    public void onActivityCreated(Bundle states) {
        super.onActivityCreated(states);

        if (getView() != null) {
            final GridView grid = (GridView) getView().findViewById(R.id.grid_view);
            final AppBoxAdapter trendingAdapter = new AppBoxAdapter(getActivity());

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
            grid.setEmptyView(getView().findViewById(R.id.empty_view));
            grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent = new Intent(getActivity(), DetailsActivity.class);
                    intent.putExtra(Constants.INTENT_APP, (Parcelable) trendingAdapter.getItem(i));

                    startActivity(intent);
                }
            });

            /* Categories selection */
            final Spinner spinner = (Spinner) getView().findViewById(R.id.spinner);
            final ArrayAdapter<SuperCategory> adapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, CategoryManager.instance().getSupers());
            adapter.setDropDownViewResource(R.layout.spinner_item_dropdown);

            spinner.setAdapter(adapter);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                    if (spinner.getTag() == null || (Integer) spinner.getTag() != position) {
                        spinner.setTag(position);

                        ConnectionService.instance.cancel();
                        GeneralWebService.instance().getTrendingApps(grid, adapter.getItem(position).getCategories());
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }
    }
}
