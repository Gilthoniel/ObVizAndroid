package com.obviz.review;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
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
import com.obviz.review.database.DatabaseService;
import com.obviz.review.managers.CacheManager;
import com.obviz.review.webservice.GeneralWebService;
import com.obviz.reviews.R;

public class HomeActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ListView mListView;
    private DrawerAdapter mAdapter;
    private MenuItem mItemSearchView;
    private DrawerLayout mDrawer;

    ActionBarDrawerToggle mDrawerToggle;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setElevation(0);
        }

        /* Init View Pager */
        final ViewPager pager = (ViewPager) findViewById(R.id.home_pager);
        pager.setAdapter(new HomePagerAdapter(getSupportFragmentManager()));
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int index) {
                setTitle(DrawerAdapter.TITLES[index]);
            }

            @Override
            public void onPageScrolled(int i, float v, int i1) {}

            @Override
            public void onPageScrollStateChanged(int i) {}
        });

        /* Init Drawer Menu */
        mListView = (ListView) findViewById(R.id.nav_list);
        mListView.addHeaderView(LayoutInflater.from(getApplicationContext()).inflate(R.layout.drawer_header, mListView, false));

        mAdapter = new DrawerAdapter(getApplicationContext());
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                setTitle((String) mAdapter.getItem(position - 1));

                pager.setCurrentItem(position - 1);
                mDrawer.closeDrawers();
            }
        });

        mDrawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawer, mToolbar, R.string.drawer_open, R.string.drawer_close) {
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
        mDrawerToggle.syncState();
        setTitle((String) mAdapter.getItem(0));

        /* -- INIT DISK CACHE -- */
        CacheManager.instance.initCacheDisk(getApplicationContext());

        /* -- INIT TOPICS -- */
        GeneralWebService.getInstance().loadTopicTitles();

        /* -- INIT DATABASE -- */
        DatabaseService.instance.initHelper(getApplicationContext());
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
        } else {
            Log.e("--NULL--", "SearchView is null");
        }

        return super.onCreateOptionsMenu(menu);
    }
}
