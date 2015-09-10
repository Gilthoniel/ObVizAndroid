package com.obviz.review.models;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.obviz.reviews.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Created by gaylor on 08/31/2015.
 * Category of an application
 */
public class Category implements CategoryBase, Parcelable{

    public static final Category instanceDefault = new Category();
    static {
        instanceDefault.category = "DEFAULT";
        instanceDefault.title = "Application";
        instanceDefault.types = new LinkedList<>();
    }


    public ID _id;
    public String category;
    public String title;
    public String icon;
    public List<Integer> types;

    private Category() {}

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getIcon() {
        return icon;
    }
    @Override
     public String getImage(Context c){
        return icon;

    }

    @Override
    public List<Category> getCategories() {
        return (Arrays.asList(this));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(category);
        parcel.writeString(title);
        parcel.writeString(icon);
        parcel.writeList(types);

    }
    public Category(Parcel parcel) {
        category = parcel.readString();
        title = parcel.readString();
        icon = parcel.readString();

        types = new ArrayList<>();
        parcel.readList(types,Integer.class.getClassLoader());
    }
    /**
     * Create a Category from a parcel
     */
    public static final Parcelable.Creator<Category> CREATOR = new Parcelable.Creator<Category>() {

        @Override
        public Category createFromParcel(Parcel parcel) {
            return new Category(parcel);
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };
}
