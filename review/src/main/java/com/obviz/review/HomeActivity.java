package com.obviz.review;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.SearchView;
import com.obviz.review.tabs.BaseFragment;
import com.obviz.review.tabs.MyAppFragment;
import com.obviz.review.tabs.SearchFragment;
import com.obviz.review.tabs.TopFragment;
import com.obviz.reviews.R;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends FragmentActivity {

    private MyPagerAdapter adapter;
    private ViewPager pager;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i("Activity created", "HomeActivity has been created");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // Initiate the pager for the sliding view
        adapter = new MyPagerAdapter(getSupportFragmentManager());

        pager = (ViewPager)findViewById(R.id.pager);
        pager.setAdapter(adapter);
        pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                getActionBar().setSelectedNavigationItem(position);
            }
        });

        // ActionBar for tab navigation
        final ActionBar actionBar = getActionBar();

        if (actionBar != null) {
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

            ActionBar.TabListener tabListener = new ActionBar.TabListener() {

                @Override
                public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
                    // Change the view of the ViewPager when the user clicks on a tab
                    pager.setCurrentItem(tab.getPosition());
                }

                @Override
                public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
                    // Nothing to do
                }

                @Override
                public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
                    // Nothing to do
                }
            };

            for (int i = 0; i < adapter.getCount(); i++) {
                ActionBar.Tab tab = actionBar.newTab();
                tab.setText(adapter.getPageTitle(i));
                tab.setTabListener(tabListener);

                actionBar.addTab(tab);
            }
        }

        // Manage Intent
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            adapter.getSearchFragment().search(query);
        }
    }

    /**
     * Create the menu options
     * @param menu Instance of the menu
     * @return true if successfully created the menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setSubmitButtonEnabled(true);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.d("New Intent", "Data send to search fragment : "+intent.getAction());
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            adapter.getSearchFragment().search(query);
        }
    }

    /* Private class */

    private class MyPagerAdapter extends FragmentPagerAdapter {

        List<BaseFragment> instances;
        SearchFragment searchFragment;
        MyAppFragment myappFragment;
        TopFragment topFragment;

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);

            instances = new ArrayList<>();
            // Search tab (First tab we see)
            searchFragment = new SearchFragment();
            searchFragment.setTitle("Google Play");
            instances.add(searchFragment);
            // My App tab
            myappFragment = new MyAppFragment();
            myappFragment.setTitle("My App");
            instances.add(myappFragment);
            // Top tab
            topFragment = new TopFragment();
            topFragment.setTitle("Top");
            instances.add(topFragment);
        }

        public SearchFragment getSearchFragment() {
            return searchFragment;
        }

        @Override
        public int getCount() {
            return instances.size();
        }

        @Override
        public Fragment getItem(int position) {
            if (position < instances.size()) {
                return instances.get(position);
            } else {
                Log.e("Tab", "Tab index doesn't exist : "+position);
                return null;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position < instances.size()) {
                return instances.get(position).getTitle();
            } else {
                Log.e("Tab", "Tab index doesn't exist : "+position);
                return "";
            }
        }
    }
}
