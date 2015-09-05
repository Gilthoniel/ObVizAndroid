package com.obviz.review.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.obviz.review.managers.CategoryManager;
import com.obviz.review.models.SuperCategory;
import com.obviz.reviews.R;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by gaylor on 08/31/2015.
 * Adapter for the super categories
 */
public class SuperCategoryGridAdapter extends BaseAdapter {

    private Context mContext;
    private List<SuperCategory> mCategories;

    public SuperCategoryGridAdapter(Context context) {
        mContext = context;
        mCategories = new LinkedList<>();
        mCategories.add(SuperCategory.getBaseSuperCategory());
        for (SuperCategory category : CategoryManager.instance().getSupers()) {
            if (category.active) {
                mCategories.add(category);
            }
        }

        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mCategories.size();
    }

    @Override
    public SuperCategory getItem(int i) {
        return mCategories.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup parent) {

        TextView layout;
        if (view != null) {
            layout = (TextView) view;
        } else {
            layout = (TextView) LayoutInflater.from(mContext).inflate(R.layout.item_spinner, parent, false);
        }

        layout.setText(getItem(i).title);

        return layout;
    }

    @Override
    public View getDropDownView(int position, View view, ViewGroup parent) {

        TextView layout;
        if (view != null) {
            layout = (TextView) view;
        } else {
            layout = (TextView) LayoutInflater.from(mContext).inflate(R.layout.item_spinner_dropdown, parent, false);
        }

        layout.setText(getItem(position).title);

        return layout;
    }
}
