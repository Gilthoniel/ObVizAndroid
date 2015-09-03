package com.obviz.review.models;

import java.io.Serializable;
import java.util.List;

/**
 * Created by gaylor on 24.07.15.
 *
 */
public class Topic implements Serializable {

    private static final long serialVersionUID = 8634619334632273342L;

    private int _id;
    private String title;
    private String[] keys;
    private boolean useSpecialGauge;
    private double  gaugeThreshold;

    public int getID() {
        return _id;
    }

    public String getTitle() {

        if (title != null) {
            return title;
        } else if (keys != null && keys.length > 0) {

            return keys[0];
        } else {
            return "Unknown";
        }
    }

    public boolean isSpecial() {
        return useSpecialGauge;
    }

    public double getThreshold() {
        return gaugeThreshold;
    }
}
