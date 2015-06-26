package com.obviz.review.tabs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.obviz.reviews.R;

/**
 * Created by gaylor on 26.06.15.
 * Tab where you find the list of your applications to find some alternatives
 */
public class MyAppFragment extends BaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.myapp_fragment, container, false);
    }
}