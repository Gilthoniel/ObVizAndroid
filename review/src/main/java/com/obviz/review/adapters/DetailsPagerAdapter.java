package com.obviz.review.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import com.obviz.review.fragments.DetailsComparisonFragment;
import com.obviz.review.fragments.DetailsOpinionsFragment;

/**
 * Created by gaylor on 23.07.15.
 *
 */
public class DetailsPagerAdapter extends FragmentStatePagerAdapter {

    private CharSequence TITLES[] = new CharSequence[] { "Opinions", "Alternatives" };

    public DetailsPagerAdapter(FragmentManager manager) {
        super(manager);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 1:
                return new DetailsComparisonFragment();
            default:
                return new DetailsOpinionsFragment();
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return TITLES[position];
    }

    @Override
    public int getCount() {
        return TITLES.length;
    }
}
