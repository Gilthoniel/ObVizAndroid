package com.obviz.review.tabs;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.obviz.review.models.PlayApplication;
import com.obviz.review.webservice.RequestCallback;
import com.obviz.review.webservice.GeneralWebService;
import com.obviz.reviews.R;

import java.lang.reflect.Type;

/**
 * Created by gaylor on 26.06.15.
 * This is the first tab where we see the search bar to find an application
 */
public class SearchFragment extends BaseFragment {

    private GeneralWebService wb;

    public SearchFragment() {
        super();
        wb = new GeneralWebService();
    }

    public void search(String query) {
        // TODO : searching
        RequestCallback<PlayApplication> callback = new RequestCallback<PlayApplication>() {
            @Override
            public void execute(PlayApplication result) {
                Log.i("Test ConnectionService", "Result : " + result.getImage());
            }

            @Override
            public Type getType() {
                return PlayApplication.class;
            }
        };

        wb.cancel();
        for (int i = 0; i < 5; i++) {
            wb.getApp("kb.Blek", callback);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.search_fragment, container, false);
    }
}