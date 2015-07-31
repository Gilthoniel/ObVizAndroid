package com.obviz.review.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.obviz.review.adapters.PackageAdapter;
import com.obviz.reviews.R;


public class PackageFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle states) {

        return inflater.inflate(R.layout.fragment_package, container, false);
    }

    @Override
    public void onActivityCreated(Bundle states) {
        super.onActivityCreated(states);

        /* Populate views */

        // ListView
        PackageAdapter adapter = new PackageAdapter(getActivity());
        final ListFragment fragment = (ListFragment) getChildFragmentManager().findFragmentById(R.id.fragment);
        fragment.setListAdapter(adapter);
    }
}
