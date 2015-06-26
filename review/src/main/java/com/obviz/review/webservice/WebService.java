package com.obviz.review.webservice;

import com.obviz.review.models.PlayApplication;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by gaylor on 26.06.15.
 * Requests to get information
 */
public class WebService {

    private static final String GET_APP = "Get_App";

    public static PlayApplication getApp(String id) {

        Map<String, String> params = new HashMap<>();
        params.put("cmd", GET_APP);
        params.put("weight", "LIGHT");
        params.put("id", id);

        String result = ConnectionService.get(ConnectionService.URL, params);
        return MessageParser.fromJson(result, PlayApplication.class);
    }
}
