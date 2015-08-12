package com.obviz.review;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import com.obviz.review.adapters.DetailsPagerAdapter;
import com.obviz.review.managers.ImageObserver;
import com.obviz.review.managers.ImagesManager;
import com.obviz.review.models.AndroidApp;
import com.obviz.review.webservice.ConnectionService;
import com.obviz.review.webservice.GeneralWebService;
import com.obviz.review.webservice.RequestCallback;
import com.obviz.review.webservice.RequestObserver;
import com.obviz.reviews.R;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Display general information about an application
 * @intent INTENT_APP_ID id of the application
 */
public class DetailsActivity extends AppCompatActivity implements ImageObserver {

    private Set<RequestObserver<AndroidApp>> mObservers;
    private AndroidApp mApplication;
    private boolean isInstalled = false;
    private Menu mMenu;

    public void AddRequestObserver(RequestObserver<AndroidApp> observer) {

        if (mApplication != null) {
            observer.onSuccess(mApplication);
        } else {
            mObservers.add(observer);
        }
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

        mObservers = new HashSet<>();

        /* Init the toolbar */
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setElevation(0);
        }

        /* Intent */
        Intent intent = getIntent();
        String appID = intent.getStringExtra(Constants.INTENT_APP_ID);

        /* Tabs */
        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        DetailsPagerAdapter adapter = new DetailsPagerAdapter(getSupportFragmentManager());

        pager.setAdapter(adapter);

        SlidingTabLayout tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true);
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabsScrollColor);
            }
        });
        tabs.setViewPager(pager);

        /* Get the app information */
        RequestCallback<AndroidApp> callback = new RequestCallback<AndroidApp>() {
            @Override
            public void onSuccess(AndroidApp app) {

                mApplication = app;
                DetailsActivity.this.setTitle(app.getName());

                List<PackageInfo> packages = getPackageManager().getInstalledPackages(0);
                for (PackageInfo info : packages) {
                    if (info.packageName.equals(mApplication.getAppID())) {
                        isInstalled = true;
                    }
                }

                TextView appDeveloper = (TextView) findViewById(R.id.app_developper);
                appDeveloper.setText(app.getDeveloper());

                TextView appCategory = (TextView) findViewById(R.id.app_category);
                appCategory.setText(app.getCategory().getTitle());

                TextView appDate = (TextView) findViewById(R.id.app_date);
                appDate.setText(app.getDate());

                TextView appFreedom = (TextView) findViewById(R.id.app_free);
                appFreedom.setText(app.isFree() ? "Free" : "Paid");

                RatingBar rating = (RatingBar) findViewById(R.id.ratingBar);
                rating.setRating(app.getScore().getTotal());

                ImagesManager.getInstance().get(app.getImage(), DetailsActivity.this);

                for (RequestObserver<AndroidApp> observer : mObservers) {
                    observer.onSuccess(app);
                }

                // Enable the install action
                if (mMenu != null) {
                    mMenu.findItem(R.id.action_install).setEnabled(true);
                }
            }

            @Override
            public void onFailure(Errors error) {
                Toast.makeText(getApplicationContext(), getResources().getText(R.string.app_not_found), Toast.LENGTH_LONG).show();
            }

            @Override
            public Type getType() {
                return AndroidApp.class;
            }
        };

        // Check if we have an old state before trying to get the application informations
        if (states != null) {

            mApplication = states.getParcelable(Constants.STATE_APP);
            callback.onSuccess(mApplication);

            // Add a seen to the app
            GeneralWebService.instance().markAsViewed(mApplication.getAppID());
        } else if (appID != null) {

            GeneralWebService.instance().getApp(appID, callback);

            // Add a seen to the app
            GeneralWebService.instance().markAsViewed(appID);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        // Stop web requests
        ConnectionService.instance.cancel();
    }

    @Override
    public void onSaveInstanceState(Bundle states) {

        states.putParcelable(Constants.STATE_APP, mApplication);

        super.onSaveInstanceState(states);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_details, menu);

        mMenu = menu;
        if (mApplication != null) {
            mMenu.findItem(R.id.action_install).setEnabled(true);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_install:

                if (isInstalled) {

                    Toast.makeText(getApplicationContext(), "Already installed!", Toast.LENGTH_LONG).show();
                } else if (mApplication != null) {

                    final String packageName = mApplication.getAppID();

                    // Catch if the Google Play Store isn't installed
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)));
                    } catch (ActivityNotFoundException e) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + packageName)));
                    }
                }
                break;

            default:
        }

        return super.onOptionsItemSelected(item);
    }
}
