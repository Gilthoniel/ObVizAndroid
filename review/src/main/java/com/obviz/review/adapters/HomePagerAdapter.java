package com.obviz.review.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import com.obviz.review.fragments.HistoryFragment;
import com.obviz.review.fragments.PackageFragment;
import com.obviz.review.fragments.TrendingFragment;

/**
 * Created by gaylor on 31.07.15.
 *
 */
public class HomePagerAdapter extends FragmentStatePagerAdapter {

    public HomePagerAdapter(FragmentManager manager) {
        super(manager);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {

            case 1:
                return new PackageFragment();
            case 2:
                return new HistoryFragment();
            default:
                return new TrendingFragment();
        }
    }

    @Override
    public int getCount() {
        return DrawerAdapter.TITLES.length;
    }
}
