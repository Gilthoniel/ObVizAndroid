package com.obviz.review.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import com.obviz.review.fragments.PackageFragment;

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
        return new PackageFragment();
    }

    @Override
    public int getCount() {
        return DrawerAdapter.TITLES.length;
    }
}
