package com.obviz.review.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;
import com.obviz.review.Constants;
import com.obviz.review.DetailsActivity;
import com.obviz.review.adapters.PackageAdapter;
import com.obviz.review.models.AndroidApp;
import com.obviz.review.webservice.GeneralWebService;
import com.obviz.review.webservice.RequestCallback;
import com.obviz.reviews.R;

import java.lang.reflect.Type;


public class PackageFragment extends ListFragment implements HomeFragment {

    private PackageAdapter adapter;

    @Override
    public void showTutorial() {}

    @Override
    public void refresh() {
        if (adapter != null) {
            adapter.update();
        }
    }

    @Override
    public String getTitle() {
        return "My Applications";
    }

    @Override
    public int getIcon() {
        return R.drawable.ic_apps_black_24dp;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle states) {

        return inflater.inflate(R.layout.list_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle states) {
        super.onActivityCreated(states);

        /* Populate views */

        // ListView
        adapter = new PackageAdapter(getActivity());
        setListAdapter(adapter);

        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                final ProgressDialog dialog = new ProgressDialog(getActivity());
                dialog.setIndeterminate(true);
                dialog.setMessage(getResources().getString(R.string.loading));
                dialog.setCancelable(true);
                dialog.show();

                GeneralWebService.instance().getApp(adapter.getItem(i), new RequestCallback<AndroidApp>() {
                    @Override
                    public void onSuccess(AndroidApp result) {
                        Intent intent = new Intent(getActivity(), DetailsActivity.class);
                        intent.putExtra(Constants.INTENT_APP, (Parcelable) result);

                        dialog.hide();
                        startActivity(intent);
                    }

                    @Override
                    public void onFailure(Errors error) {

                        dialog.hide();
                        Toast.makeText(getActivity(), getResources().getString(R.string.internet_error), Toast.LENGTH_LONG)
                                .show();
                    }
                });
            }
        });

        adapter.update();
    }
}
