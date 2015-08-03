package com.obviz.review.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import com.obviz.review.Constants;
import com.obviz.review.DetailsActivity;
import com.obviz.review.adapters.PackageAdapter;
import com.obviz.reviews.R;


public class PackageFragment extends ListFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle states) {

        return inflater.inflate(R.layout.list_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle states) {
        super.onActivityCreated(states);

        /* Populate views */

        // ListView
        final PackageAdapter adapter = new PackageAdapter(getActivity());
        setListAdapter(adapter);

        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent(getActivity(), DetailsActivity.class);

                intent.putExtra(Constants.INTENT_APP_ID, (String) adapter.getItem(position));

                startActivity(intent);
            }
        });
    }
}
