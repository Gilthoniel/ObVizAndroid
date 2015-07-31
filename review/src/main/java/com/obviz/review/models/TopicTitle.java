package com.obviz.review.models;

import java.io.Serializable;
import java.util.List;

/**
 * Created by gaylor on 24.07.15.
 *
 */
public class TopicTitle implements Serializable {

    private static final long serialVersionUID = 8634619334632273342L;

    private int _id;
    private List<String> keys;

    public int getID() {
        return _id;
    }

    public List<String> getTitles() {
        return keys;
    }
}
