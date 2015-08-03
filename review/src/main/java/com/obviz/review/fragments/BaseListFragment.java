package com.obviz.review.fragments;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.obviz.reviews.R;

/**
 * Created by gaylor on 8/3/2015.
 * Template for the most list fragments
 */
public class BaseListFragment extends ListFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle states) {

        return inflater.inflate(R.layout.list_fragment, parent, false);
    }
}
