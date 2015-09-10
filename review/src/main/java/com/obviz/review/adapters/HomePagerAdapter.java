package com.obviz.review.adapters;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;


import com.obviz.review.fragments.DiscoverFragment;

import com.obviz.review.SettingsActivity;

import com.obviz.review.fragments.FavoriteFragment;
import com.obviz.review.fragments.HistoryFragment;
import com.obviz.review.fragments.PackageFragment;
import com.obviz.review.fragments.TrendingFragment;

/**
 * Created by gaylor on 31.07.15.
 * Pager for the fragments of the HomeActivity
 */
public class HomePagerAdapter extends FragmentPagerAdapter {

    public HomePagerAdapter(FragmentManager manager) {
        super(manager);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {

            case 1:
                return new DiscoverFragment();
            case 2:
                return new PackageFragment();
            case 3:
                return new HistoryFragment();
            case 4:
                return new FavoriteFragment();
            default:
                return new TrendingFragment();
        }
    }

    @Override
    public int getCount() {
        return DrawerAdapter.TITLES.length;
    }



}
