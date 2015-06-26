package com.obviz.review.tabs;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.obviz.review.webservice.ConnectionService;
import com.obviz.reviews.R;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gaylor on 26.06.15.
 * This is the first tab where we see the search bar to find an application
 */
public class SearchFragment extends BaseFragment {

    public void search(String query) {
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("cmd", "Get_app"));
        params.add(new BasicNameValuePair("id", "kb.Blek"));
        params.add(new BasicNameValuePair("weight", "LIGHT"));
        String result = ConnectionService.get("http://vps40100.vps.ovh.ca/ObVizServiceAdmin", params);

        Log.i("Result", result);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.search_fragment, container, false);
    }
}