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
import com.obviz.review.webservice.GeneralWebService;
import com.obviz.review.webservice.RequestCallback;
import com.obviz.review.webservice.RequestObserver;
import com.obviz.reviews.R;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class ReviewsComparisonActivity extends AppCompatActivity {

    private ViewPager mPager;
    private SlidingTabLayout mTabs;
    private ComparisonPagerAdapter mAdapter;
    private AndroidApp mApplication;
    private AndroidApp mComparison;
    private List<RequestObserver<AndroidApp>> appObservers = new ArrayList<>();
    private List<RequestObserver<AndroidApp>> comparisonObservers = new ArrayList<>();
    private String mTopicID;

    public void addApplicationObserver(ComparisonReviewsFragment observer) {
        if (observer.getType() == 0) {

            if (mApplication != null) {
                observer.onSuccess(mApplication);
            } else {
                appObservers.add(observer);
            }
        } else {

            if (mComparison != null) {
                observer.onSuccess(mComparison);
            } else {
                comparisonObservers.add(observer);
            }
        }
    }

    public String getTopicID() {

        return mTopicID;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews_comparison);

        Intent intent = getIntent();
        String appID = intent.getStringExtra(Constants.INTENT_APP_ID);
        String comparisonID = intent.getStringExtra(Constants.INTENT_COMPARISON_APP_ID);
        mTopicID = intent.getStringExtra(Constants.INTENT_TOPIC_ID);
        setTitle(TopicsManager.instance.getTopicTitle(Integer.parseInt(mTopicID)));

        /* Tabs initialization */
        mPager = (ViewPager) findViewById(R.id.pager);
        mAdapter = new ComparisonPagerAdapter(getSupportFragmentManager());

        mPager.setAdapter(mAdapter);

        mTabs = (SlidingTabLayout) findViewById(R.id.tabs);
        mTabs.setDistributeEvenly(true);
        mTabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabsScrollColor);
            }
        });
        mTabs.setViewPager(mPager);

        /* Get the information for the two apps */
        populateApp(appID, true);
        populateApp(comparisonID, false);

        /* Action Bar */
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setElevation(0);
        }
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

    /* PRIVATE */

    private void populateApp(String id, final boolean isApp) {

        GeneralWebService.getInstance().getApp(id, new RequestCallback<AndroidApp>() {
            @Override
            public void onSuccess(AndroidApp result) {

                mAdapter.addTitle(result.getName());
                mTabs.setViewPager(mPager);

                if (isApp) {
                    fillApp(result);
                } else {
                    fillComparison(result);
                }
            }

            @Override
            public void onFailure(Errors error) {

            }

            @Override
            public Type getType() {
                return AndroidApp.class;
            }
        });
    }

    private void fillApp(AndroidApp app) {
        mApplication = app;

        // Send the application to the observer
        warnObservers(appObservers, mApplication);
    }

    private void fillComparison(AndroidApp app) {
        mComparison = app;

        // Send the application to the observers
        warnObservers(comparisonObservers, mComparison);
    }

    private void warnObservers(List<RequestObserver<AndroidApp>> observers, AndroidApp app) {

        for (RequestObserver<AndroidApp> observer : observers) {
            observer.onSuccess(app);
        }

        observers.clear();
    }
}
