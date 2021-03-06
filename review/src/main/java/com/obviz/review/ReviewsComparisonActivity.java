package com.obviz.review;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import com.obviz.review.adapters.ComparisonPagerAdapter;
import com.obviz.review.fragments.ComparisonReviewsFragment;
import com.obviz.review.managers.TopicsManager;
import com.obviz.review.models.AndroidApp;
import com.obviz.review.webservice.ConnectionService;
import com.obviz.review.webservice.GeneralWebService;
import com.obviz.review.webservice.RequestCallback;
import com.obviz.review.webservice.RequestObserver;
import com.obviz.reviews.R;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class ReviewsComparisonActivity extends AppCompatActivity implements TopicsManager.TopicsObserver {

    private AndroidApp mApplication;
    private AndroidApp mComparison;
    private int mTopicID;

    @Override
    protected void onCreate(Bundle states) {
        super.onCreate(states);
        setContentView(R.layout.activity_reviews_comparison);

        if (states != null) {
            mApplication = states.getParcelable(Constants.STATE_APP);
            mComparison = states.getParcelable(Constants.STATE_COMPARISON);
            mTopicID = states.getInt(Constants.STATE_TOPIC);
        } else {
            Intent intent = getIntent();
            mApplication = intent.getParcelableExtra(Constants.INTENT_APP);
            mComparison = intent.getParcelableExtra(Constants.INTENT_COMPARISON_APP);
            mTopicID = intent.getIntExtra(Constants.INTENT_TOPIC_ID, -1);
        }

        // Try to acquire the topic title
        onTopicsLoaded();

        /* Tabs initialization */
        ViewPager mPager = (ViewPager) findViewById(R.id.pager);
        ComparisonPagerAdapter mAdapter = new ComparisonPagerAdapter(getSupportFragmentManager(), mTopicID);
        mAdapter.addPage(mApplication);
        mAdapter.addPage(mComparison);

        mPager.setAdapter(mAdapter);

        SlidingTabLayout mTabs = (SlidingTabLayout) findViewById(R.id.tabs);
        mTabs.setDistributeEvenly(true);
        mTabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabsScrollColor);
            }
        });
        mTabs.setViewPager(mPager);

        /* Action Bar */
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white);
            getSupportActionBar().setElevation(0);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        ConnectionService.instance().cancel();
    }

    @Override
    public void onSaveInstanceState(Bundle states) {

        states.putParcelable(Constants.STATE_APP, mApplication);
        states.putParcelable(Constants.STATE_COMPARISON, mComparison);
        states.putInt(Constants.STATE_TOPIC, mTopicID);

        super.onSaveInstanceState(states);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_reviews_comparison, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTopicsLoaded() {

        setTitle(TopicsManager.instance().getTitle(mTopicID, this));
    }
}
