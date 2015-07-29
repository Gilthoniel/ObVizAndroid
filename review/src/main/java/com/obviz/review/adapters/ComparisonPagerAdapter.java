package com.obviz.review.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import com.obviz.review.fragments.ComparisonReviewsFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gaylor on 29.07.15.
 *
 */
public class ComparisonPagerAdapter extends FragmentStatePagerAdapter {

    private List<String> mTitles;

    public ComparisonPagerAdapter(FragmentManager manager) {
        super(manager);

        mTitles = new ArrayList<>();
    }

    public void addTitle(String title) {
        mTitles.add(title);

        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {

        ComparisonReviewsFragment fragment = new ComparisonReviewsFragment();
        fragment.setType(position);

        return fragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles.get(position);
    }

    @Override
    public int getCount() {
        return mTitles.size();
    }
}
