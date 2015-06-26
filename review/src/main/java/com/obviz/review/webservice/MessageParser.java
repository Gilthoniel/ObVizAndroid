package com.obviz.review.webservice;

import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.lang.reflect.Type;

/**
 * Created by gaylor on 26.06.15.
 * Parse json to Java class and inversely
 */
public class MessageParser {

    private static Gson gson = new Gson();

    public static <T> T fromJson(String json, Type type) {

        try {
            return gson.fromJson(json, type);
        } catch (JsonSyntaxException e) {
            Log.e("Json", e.getMessage());
            return null;
        }
    }
}
