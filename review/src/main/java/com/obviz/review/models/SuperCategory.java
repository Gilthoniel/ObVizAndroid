package com.obviz.review.models;

/**
 * Created by gaylor on 08/31/2015.
 * Set of categories
 */
public class SuperCategory {

    private static SuperCategory all;

    public Integer _id;
    public String title;
    public String icon;
    public boolean active;

    public static SuperCategory getBaseSuperCategory() {
        if (all == null) {
            all = new SuperCategory();
            all._id = 0;
            all.title = "All";
        }

        return all;
    }
}
