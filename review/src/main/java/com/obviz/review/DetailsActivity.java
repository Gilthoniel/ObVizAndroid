package com.obviz.review;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import com.obviz.review.adapters.DetailsPagerAdapter;
import com.obviz.review.managers.ImageObserver;
import com.obviz.review.managers.ImagesManager;
import com.obviz.review.models.AndroidApp;
import com.obviz.review.webservice.*;
import com.obviz.reviews.R;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;


public class DetailsActivity extends AppCompatActivity implements ImageObserver {

    private ViewPager mPager;
    private DetailsPagerAdapter mAdapter;
    private Set<RequestObserver<AndroidApp>> mObservers;
    private AndroidApp mApplication;

    public void AddRequestObserver(RequestObserver<AndroidApp> observer) {
        mObservers.add(observer);
    }

    @Override
    public void onImageLoaded(String url, Bitmap bitmap) {
        if (mApplication != null) {
            ImageView logo = (ImageView) findViewById(R.id.imageView);
            logo.setImageBitmap(bitmap);
        }
    }

    @Override
    protected void onCreate(Bundle states) {
        super.onCreate(states);
        setContentView(R.layout.activity_details);

        Intent intent = getIntent();
        String appID = intent.getStringExtra(Constants.INTENT_APP_ID);

        mObservers = new HashSet<>();

        // Tabs
        mPager = (ViewPager) findViewById(R.id.pager);
        mAdapter = new DetailsPagerAdapter(getSupportFragmentManager());

        mPager.setAdapter(mAdapter);

        SlidingTabLayout tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true);
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabsScrollColor);
            }
        });
        tabs.setViewPager(mPager);

        /* Get the app information */
        RequestCallback<AndroidApp> callback = new RequestCallback<AndroidApp>() {
            @Override
            public void onSuccess(AndroidApp app) {

                mApplication = app;
                DetailsActivity.this.setTitle(app.getName());

                TextView appDeveloper = (TextView) findViewById(R.id.app_developper);
                appDeveloper.setText(app.getDeveloper());

                TextView appCategory = (TextView) findViewById(R.id.app_category);
                appCategory.setText(app.getCategory().getTitle());

                TextView appDate = (TextView) findViewById(R.id.app_date);
                appDate.setText(app.getDate());

                TextView appInstallations = (TextView) findViewById(R.id.app_installs);
                appInstallations.setText(app.getInstallations() + " installations");

                TextView appFreedom = (TextView) findViewById(R.id.app_free);
                appFreedom.setText(app.isFree() ? "Free" : "Paid");

                RatingBar rating = (RatingBar) findViewById(R.id.ratingBar);
                rating.setRating(app.getScore().getTotal());

                ImagesManager.getInstance().get(app.getImage(), DetailsActivity.this);

                for (RequestObserver<AndroidApp> observer : mObservers) {
                    observer.onSuccess(app);
                }
            }

            @Override
            public void onFailure(Errors error) {

            }

            @Override
            public Type getType() {
                return AndroidApp.class;
            }
        };
        GeneralWebService.getInstance().getApp(appID, callback);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setElevation(0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }
}
