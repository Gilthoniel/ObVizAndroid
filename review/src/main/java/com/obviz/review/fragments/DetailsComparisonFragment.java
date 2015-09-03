package com.obviz.review.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.obviz.review.ComparisonActivity;
import com.obviz.review.Constants;
import com.obviz.review.DetailsActivity;
import com.obviz.review.adapters.AppBoxAdapter;
import com.obviz.review.adapters.GridAdapter;
import com.obviz.review.models.AndroidApp;
import com.obviz.review.views.GridRecyclerView;
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

    private View mParent;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle states) {

        mParent = inflater.inflate(R.layout.grid_app_box, container, false);
        return mParent;
    }

    @Override
    public void onActivityCreated(Bundle states) {
        super.onActivityCreated(states);

        DetailsActivity parent = (DetailsActivity) getActivity();
        AndroidApp mApplication = parent.getListApplications().get(0);

        populateAlternatives(mApplication, mParent);
    }

    public static void populateAlternatives(final AndroidApp application, final View view) {

        final AppBoxAdapter adapter = new AppBoxAdapter();
        GridRecyclerView grid = (GridRecyclerView) view.findViewById(R.id.grid_view);
        adapter.addOnItemClickListener(new GridAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                AndroidApp app = adapter.getItem(position);

                Intent intent = new Intent(view.getContext(), ComparisonActivity.class);
                intent.putExtra(Constants.INTENT_APP, (Parcelable) application);
                intent.putExtra(Constants.INTENT_COMPARISON_APP, (Parcelable) app);

                view.getContext().startActivity(intent);
            }
        });
        grid.setAdapter(adapter);

        final Iterator<String> it = application.getRelatedIDs().iterator();
        while (it.hasNext()) {
            String id = it.next();

            GeneralWebService.instance().getApp(id, new RequestCallback<AndroidApp>() {
                @Override
                public void onSuccess(AndroidApp result) {
                    if (result.isParsed()) {
                        adapter.add(result);
                    }

                    if (!it.hasNext()) {
                        // Notify only on the last elem
                        adapter.notifyDataSetChanged();
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
