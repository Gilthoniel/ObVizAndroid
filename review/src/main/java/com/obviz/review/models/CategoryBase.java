package com.obviz.review.models;

import android.content.Context;

import java.util.List;

/**
 * Created by musat on 08/09/15.
 */
public interface CategoryBase {

    String getTitle();
    String getIcon();
    String getImage(Context c);
    List<Category> getCategories();
}
