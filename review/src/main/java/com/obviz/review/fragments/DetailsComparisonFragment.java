package com.obviz.review.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import com.obviz.review.ComparisonActivity;
import com.obviz.review.Constants;
import com.obviz.review.DetailsActivity;
import com.obviz.review.adapters.AppBoxAdapter;
import com.obviz.review.models.AndroidApp;
import com.obviz.review.webservice.GeneralWebService;
import com.obviz.review.webservice.RequestCallback;
import com.obviz.reviews.R;

import java.lang.reflect.Type;
import java.util.Iterator;

/**
 * Created by gaylor on 23.07.15.
 *
 */
public class DetailsComparisonFragment extends Fragment {

    private AppBoxAdapter mAdapter;
    private View mParent;
    private AndroidApp mApplication;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle states) {

        mParent = inflater.inflate(R.layout.grid_app_box, container, false);
        return mParent;
    }

    @Override
    public void onActivityCreated(Bundle states) {
        super.onActivityCreated(states);

        mAdapter = new AppBoxAdapter(getActivity());
        GridView grid = (GridView) mParent.findViewById(R.id.grid_view);
        grid.setAdapter(mAdapter);
        grid.setEmptyView(mParent.findViewById(android.R.id.empty));
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View parent, int position, long l) {

                AndroidApp app = mAdapter.getItem(position);

                Intent intent = new Intent(parent.getContext(), ComparisonActivity.class);
                intent.putExtra(Constants.INTENT_APP, (Parcelable) mApplication);
                intent.putExtra(Constants.INTENT_COMPARISON_APP, (Parcelable) app);

                startActivity(intent);
            }
        });

        DetailsActivity parent = (DetailsActivity) getActivity();
        mApplication = parent.getListApplications().get(0);

        final Iterator<String> it = mApplication.getRelatedIDs().iterator();
        while (it.hasNext()) {
            String id = it.next();

            GeneralWebService.instance().getApp(id, new RequestCallback<AndroidApp>() {
                @Override
                public void onSuccess(AndroidApp result) {
                    mAdapter.add(result);

                    if (!it.hasNext()) {
                        // Notify only on the last elem
                        mAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onFailure(Errors error) {

                }

                @Override
                public Type getType() {
                    return AndroidApp.class;
                }
            });
        }
    }
}
