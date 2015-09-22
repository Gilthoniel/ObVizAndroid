package com.obviz.review.models;

import java.io.Serializable;
import java.util.List;

/**
 * Created by gaylor on 09/18/2015.
 * Headline for the home fragment
 */
public class Headline implements Serializable {

    private String title;
    private Integer topicID;
    private List<AndroidApp> apps;

    public String getTitle() {
        return title;
    }

    public Integer getTopicID() {
        return topicID;
    }

    public List<AndroidApp> getApps() {
        return apps;
    }
}
