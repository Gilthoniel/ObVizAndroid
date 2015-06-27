package com.obviz.review.webservice;

import com.obviz.review.models.PlayApplication;

import java.util.*;

/**
 * Created by gaylor on 26.06.15.
 * Requests to execute information
 */
public class GeneralWebService extends WebService {

    private static final String GET_APP = "Get_App";

    public void getApp(String id, RequestCallback<PlayApplication> callback) {

        Map<String, String> params = new HashMap<>();
        params.put("cmd", GET_APP);
        params.put("weight", "LIGHT");
        params.put("id", id);

        get(params, callback);
    }
}
