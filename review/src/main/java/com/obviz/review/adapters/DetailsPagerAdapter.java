package com.obviz.review.adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import com.obviz.review.Constants;
import com.obviz.review.fragments.DetailsComparisonFragment;
import com.obviz.review.fragments.DetailsFragment;
import com.obviz.review.fragments.DetailsOpinionsFragment;
import com.obviz.review.models.AndroidApp;

/**
 * Created by gaylor on 23.07.15.
 *
 */
public class DetailsPagerAdapter extends FragmentStatePagerAdapter {

    private CharSequence TITLES[] = new CharSequence[] { "Details", "Opinions", "Alternatives" };
    private AndroidApp mApplication;

    public DetailsPagerAdapter(FragmentManager manager, AndroidApp application) {
        super(manager);

        mApplication = application;
    }

    @Override
    public Fragment getItem(int position) {

        Fragment fragment;
        Bundle args = new Bundle();
        args.putParcelable(Constants.STATE_APP, mApplication);

        switch (position) {
            case 2:
                fragment = new DetailsComparisonFragment();
                break;
            case 1:
                fragment = new DetailsOpinionsFragment();
                break;
            default:
                fragment = new DetailsFragment();
                break;
        }

        fragment.setArguments(args);
        return fragment;
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
