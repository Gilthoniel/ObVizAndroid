package com.obviz.review.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.obviz.review.ComparisonActivity;
import com.obviz.review.Constants;
import com.obviz.review.DetailsActivity;
import com.obviz.review.adapters.ResultsAdapter;
import com.obviz.review.models.AndroidApp;
import com.obviz.review.webservice.GeneralWebService;
import com.obviz.review.webservice.RequestCallback;
import com.obviz.review.webservice.RequestObserver;
import com.obviz.reviews.R;

import java.lang.reflect.Type;
import java.util.Iterator;

/**
 * Created by gaylor on 23.07.15.
 *
 */
public class DetailsComparisonFragment extends ListFragment implements RequestObserver<AndroidApp> {

    private ResultsAdapter mAdapter;
    private AndroidApp mApplication;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle states) {

        return inflater.inflate(R.layout.details_comparison_tab, container, false);
    }

    @Override
    public void onActivityCreated(Bundle states) {
        super.onActivityCreated(states);

        mAdapter = new ResultsAdapter(getActivity());
        setListAdapter(mAdapter);

        DetailsActivity parent = (DetailsActivity) getActivity();
        parent.AddRequestObserver(this);
    }

    @Override
    public void onListItemClick(ListView list, View v, int position, long id) {

        if (mApplication != null) {
            AndroidApp app = (AndroidApp) mAdapter.getItem(position);

            Intent intent = new Intent(list.getContext(), ComparisonActivity.class);
            intent.putExtra(Constants.INTENT_APP, (Parcelable) mApplication);
            intent.putExtra(Constants.INTENT_COMPARISON_APP, (Parcelable) app);

            startActivity(intent);
        }
    }

    @Override
    public void onSuccess(final AndroidApp app) {

        mApplication = app;

        final Iterator<String> it = app.getRelatedIDs().iterator();
        while (it.hasNext()) {
            String id = it.next();

            GeneralWebService.instance().getApp(id, new RequestCallback<AndroidApp>() {
                @Override
                public void onSuccess(AndroidApp result) {
                    mAdapter.add(result);

                    if (!it.hasNext()) {
                        // Notify only on the last elem
                        mAdapter.notifyEndLoading();
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
