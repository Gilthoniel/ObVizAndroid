package com.obviz.review.fragments;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;
import com.obviz.review.Constants;
import com.obviz.review.DetailsActivity;
import com.obviz.review.adapters.FavoriteAdapter;
import com.obviz.review.managers.TutorialManager;
import com.obviz.review.models.AndroidApp;
import com.obviz.review.webservice.GeneralWebService;
import com.obviz.review.webservice.RequestCallback;
import com.obviz.reviews.R;

import java.lang.reflect.Type;

/**
 * Created by gaylor on 08/25/2015.
 * Fragment where we find the favorite application of the user
 */
public class FavoriteFragment extends ListFragment implements HomeFragment {

    private FavoriteAdapter mAdapter;
    private Context mContext;

    @Override
    public void showTutorial() {
        if (getView() == null) {
            return;
        }

        TutorialManager.single((Activity) mContext)
                .setTarget(getView().findViewById(R.id.showcase_target))
                .setContentText(getResources().getString(R.string.tutorial_favorite))
                .singleUse(Constants.TUTORIAL_FAVORITE_KEY)
                .show();
    }

    @Override
    public void refresh() {
        if (mAdapter != null) {
            mAdapter.update();
        }
    }

    @Override
    public String getTitle() {
        return "Favorite";
    }

    @Override
    public int getIcon() {
        return R.drawable.ic_favorite_black_24dp;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle states) {

        return inflater.inflate(R.layout.list_fragment, parent, false);
    }

    @Override
    public void onActivityCreated(Bundle states) {
        super.onActivityCreated(states);

        mAdapter = new FavoriteAdapter(getActivity());
        setListAdapter(mAdapter);

        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                final ProgressDialog dialog = new ProgressDialog(getActivity());
                dialog.setIndeterminate(true);
                dialog.setMessage(getResources().getString(R.string.loading));
                dialog.setCancelable(true);
                dialog.show();

                GeneralWebService.instance().getApp(mAdapter.getItem(i).id, new RequestCallback<AndroidApp>() {
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
    }
}
