package com.obviz.review.webservice;

import com.google.gson.Gson;

import java.lang.reflect.Type;

/**
 * Created by gaylor on 26.06.15.
 * Parse json to Java class and inversely
 */
public class MessageParser {

    private static Gson gson = new Gson();

    public static <T> T fromJson(String json, Type type) {

        return gson.fromJson(json, type);
    }
}
