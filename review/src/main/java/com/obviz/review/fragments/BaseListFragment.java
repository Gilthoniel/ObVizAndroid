package com.obviz.review.fragments;

import android.app.ListFragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.obviz.reviews.R;

/**
 * Created by gaylor on 28.07.15.
 *
 */
public class BaseListFragment extends ListFragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent, Bundle states) {

        return inflater.inflate(R.layout.list_fragment, parent, false);
    }
}
