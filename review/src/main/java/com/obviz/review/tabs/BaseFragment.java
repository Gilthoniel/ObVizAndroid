package com.obviz.review.tabs;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Created by gaylor on 26.06.15.
 * Base of the page of the navigation tab
 */
public class BaseFragment extends Fragment {

    private String title;

    public void setTitle(String title)  {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}