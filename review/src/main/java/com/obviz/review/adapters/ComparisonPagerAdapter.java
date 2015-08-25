package com.obviz.review.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import com.obviz.review.fragments.ComparisonReviewsFragment;
import com.obviz.review.models.AndroidApp;

import java.util.*;

/**
 * Created by gaylor on 29.07.15.
 *
 */
public class ComparisonPagerAdapter extends FragmentPagerAdapter {

    private Map<String, ComparisonReviewsFragment> mFragments;
    private List<String> mTitles;

    public ComparisonPagerAdapter(FragmentManager manager) {
        super(manager);

        mFragments = new HashMap<>();
        mTitles = new LinkedList<>();
    }

    public void addPage(AndroidApp app) {
        mFragments.put(app.getName(), ComparisonReviewsFragment.newInstance(app));
        mTitles.add(app.getName());

        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {

        return mFragments.get(mTitles.get(position));
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }
}
