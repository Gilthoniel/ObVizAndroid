package com.obviz.review.models;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by gaylor on 08/31/2015.
 * Category of an application
 */
public class Category {

    public static final Category instanceDefault = new Category();
    static {
        instanceDefault.category = "DEFAULT";
        instanceDefault.title = "Application";
        instanceDefault.types = new LinkedList<>();
    }

    public ID _id;
    public String category;
    public String title;
    public List<Integer> types;

    private Category() {}
}
