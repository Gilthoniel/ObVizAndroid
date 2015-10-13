package com.obviz.review;

import android.app.ActionBar;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ListView;
import com.obviz.review.adapters.DrawerAdapter;
import com.obviz.review.adapters.HomePagerAdapter;
import com.obviz.review.webservice.ConnectionService;
import com.obviz.reviews.R;

public class HomeActivity extends AppCompatActivity {

    private DrawerAdapter mAdapter;
    private MenuItem mItemSearchView;
    private DrawerLayout mDrawer;
    private ActionBarDrawerToggle mDrawerToggle;
    private ViewPager mPager;
    private HomePagerAdapter mPagerAdapter;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Load Default setting values
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setElevation(0);
        }

        /* Init View Pager */
        mPager = (ViewPager) findViewById(R.id.home_pager);
        mPagerAdapter = new HomePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.addOnPageChangeListener(new PageChangeListener());
        // Refresh the first fragment
        mPagerAdapter.getFragments().get(0).refresh();

        /* Init Drawer Menu */
        ListView listView = (ListView) findViewById(R.id.nav_list);
        listView.addHeaderView(LayoutInflater.from(getApplicationContext()).inflate(R.layout.drawer_header, listView, false));

        mAdapter = new DrawerAdapter(getApplicationContext(), mPagerAdapter);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (position > 0) {
                    if (position > mPagerAdapter.getCount()) {

                        Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                        startActivity(intent);
                    } else {
                        setTitle(mPagerAdapter.getPageTitle(position));

                        mPager.setCurrentItem(position - 1);
                        mDrawer.closeDrawers();
                    }
                }
            }
        });

        mDrawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View view) {
                super.onDrawerOpened(view);
            }

            @Override
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }
        };
        mDrawer.setDrawerListener(mDrawerToggle);
        setTitle(mPagerAdapter.getPageTitle(0));
    }

    /**
     * Call when the activity is back on foreground
     */
    @Override
    public void onResume() {
        super.onResume();

        // Collapse the search view when we go back to the home activity
        if (mItemSearchView != null) {
            mItemSearchView.collapseActionView();
        }

        mDrawerToggle.syncState();
        mDrawer.closeDrawers();

        mPager.post(new Runnable() {
            @Override
            public void run() {
                showTutorials(mPager.getCurrentItem());
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();

        ConnectionService.instance().cancel();
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

        // initiate the search view
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mItemSearchView = menu.findItem(R.id.menu_search);
        SearchView searchView = (SearchView) mItemSearchView.getActionView();
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setSubmitButtonEnabled(true);
            searchView.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

            ActionBar.LayoutParams params = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT,
                    ActionBar.LayoutParams.MATCH_PARENT);
            searchView.setLayoutParams(params);
            Point size = new Point();
            getWindowManager().getDefaultDisplay().getSize(size);
            searchView.setMaxWidth(size.x);
        } else {
            Log.e("_SearchView_", "SearchView is null");
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {

        Boolean isDiscoverFragment = mPagerAdapter.onBackPressed(mPager.getCurrentItem());
        Log.d("HomeActivity", "isDiscoverFragment "+isDiscoverFragment.toString());

        if(!isDiscoverFragment)
            super.onBackPressed();

    }

    /**
     * Show the tutorial for a certain fragment related to the position
     * @param position of the fragment in the pager
     */
    private void showTutorials(int position) {

        mPagerAdapter.getFragments().get(position).showTutorial();
    }

    /**
     * React when the user change the page with the menu or the fingers
     */
    private class PageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageSelected(final int index) {
            mPager.post(new Runnable() {
                @Override
                public void run() {
                    mPagerAdapter.getFragments().get(index).refresh();

                    // Tutorials
                    showTutorials(index);
                }
            });

            // Adapt title
            setTitle(mPagerAdapter.getPageTitle(index));
            mAdapter.setActiveItem(index);
        }

        @Override
        public void onPageScrolled(int index, float offset, int offsetPixels) {
            mAdapter.setActiveItem(index);
        }

        @Override
        public void onPageScrollStateChanged(int i) {}
    }
}
