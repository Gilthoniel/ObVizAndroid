package com.obviz.review.models;

import android.content.Context;

import android.util.Log;
import com.obviz.reviews.R;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Created by gaylor on 08/31/2015.
 * Set of categories
 */
public class SuperCategory implements CategoryBase{

    private static SuperCategory all;

    public Integer _id;
    public String title;
    public String icon;

    public boolean active;
    public List<Category> categories;

    @Override
    public String getImage(Context c){
        return c.getString(R.string.category_image_link)+icon;

    }

    public static SuperCategory getBaseSuperCategory() {
        if (all == null) {
            all = new SuperCategory();
            all._id = 0;
            all.title = "All apps";
            all.categories = new LinkedList<>();
        }

        return all;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getIcon() {
        return icon;
    }

    @Override
    public List<Category> getCategories() {
        return categories;
    }
}
