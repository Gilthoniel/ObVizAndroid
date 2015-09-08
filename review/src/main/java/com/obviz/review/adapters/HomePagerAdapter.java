package com.obviz.review.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import com.obviz.review.Constants;
import com.obviz.review.HomeActivity;
import com.obviz.review.SettingsActivity;
import com.obviz.review.fragments.*;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by gaylor on 31.07.15.
 * Pager for the fragments of the HomeActivity
 */
public class HomePagerAdapter extends FragmentPagerAdapter {

    public List<HomeFragment> mFragments;

    public HomePagerAdapter(FragmentManager manager) {
        super(manager);

        mFragments = new LinkedList<>();
        for (Type type : Constants.HOME_FRAGMENTS) {

            if (type == TrendingFragment.class) {

                mFragments.add(new TrendingFragment());
            } else if (type == PackageFragment.class) {

                mFragments.add(new PackageFragment());
            } else if (type == HistoryFragment.class) {

                mFragments.add(new HistoryFragment());
            } else if (type == FavoriteFragment.class) {

                mFragments.add(new FavoriteFragment());
            }
        }

        mFragments.add(SettingsActivity.homeFragment);
    }

    public List<HomeFragment> getFragments() {

        return mFragments;
    }

    @Override
    public String getPageTitle(int position) {

        return mFragments.get(position).getTitle();
    }

    @Override
    public Fragment getItem(int position) {

        return (Fragment) mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size() - 1;
    }
}
