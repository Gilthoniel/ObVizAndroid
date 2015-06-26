package com.obviz.review.tabs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.obviz.reviews.R;

/**
 * Created by gaylor on 26.06.15.
 * This is the first tab where we see the search bar to find an application
 */
public class SearchFragment extends BaseFragment {

    public void search(String query) {
        // TODO : searching
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.search_fragment, container, false);
    }
}