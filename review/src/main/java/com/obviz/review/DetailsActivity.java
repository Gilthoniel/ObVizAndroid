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
import com.obviz.review.adapters.GaugeAdapter;
import com.obviz.review.database.DatabaseService;
import com.obviz.review.managers.ImageObserver;
import com.obviz.review.managers.ImagesManager;
import com.obviz.review.models.AndroidApp;
import com.obviz.review.webservice.ConnectionService;
import com.obviz.review.webservice.GeneralWebService;
import com.obviz.reviews.R;

import java.util.LinkedList;
import java.util.List;

/**
 * Display general information about an application
 * @intent INTENT_APP_ID id of the application
 */
public class DetailsActivity extends AppCompatActivity implements ImageObserver, GaugeAdapter.GaugeAdaptable {

    private AndroidApp mApplication;
    private boolean isInstalled = false;
    private Menu mMenu;

    @Override
    public List<AndroidApp> getListApplications() {

        List<AndroidApp> list = new LinkedList<>();
        list.add(mApplication);

        return list;
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

        /* Init the toolbar */
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setElevation(0);
        }

        /* Intent */
        Intent intent = getIntent();
        mApplication = intent.getParcelableExtra(Constants.INTENT_APP);
        // Check if we have an old state before trying to get the application information
        if (states != null) {

            mApplication = states.getParcelable(Constants.STATE_APP);
        }

        // Add a seen to the app
        GeneralWebService.instance().markAsViewed(mApplication.getAppID());

        // Activity title
        setTitle(mApplication.getName());

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

        List<PackageInfo> packages = getPackageManager().getInstalledPackages(0);
        for (PackageInfo info : packages) {
            if (info.packageName.equals(mApplication.getAppID())) {
                isInstalled = true;
            }
        }

        TextView appDeveloper = (TextView) findViewById(R.id.app_developper);
        appDeveloper.setText(mApplication.getDeveloper());

        TextView appCategory = (TextView) findViewById(R.id.app_category);
        appCategory.setText(mApplication.getCategory().getTitle());

        TextView appDate = (TextView) findViewById(R.id.app_date);
        appDate.setText(mApplication.getDate());

        TextView appFreedom = (TextView) findViewById(R.id.app_free);
        appFreedom.setText(mApplication.isFree() ? "Free" : "Paid");

        RatingBar rating = (RatingBar) findViewById(R.id.ratingBar);
        rating.setRating(mApplication.getScore().getTotal());

        ImagesManager.getInstance().get(mApplication.getLogo(), DetailsActivity.this);

        // Enable the install action
        if (mMenu != null) {
            mMenu.findItem(R.id.action_install).setEnabled(true);
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

            if (DatabaseService.instance().isInFavorite(mApplication.getAppID())) {

                mMenu.findItem(R.id.action_remove_favorite).setVisible(true);
            } else {

                mMenu.findItem(R.id.action_favorite).setVisible(true);
            }
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

            case R.id.action_favorite:

                if (DatabaseService.instance().insertFavorite(mApplication) > 0) {

                    Toast.makeText(getApplicationContext(), getResources().getText(R.string.favorite_inserted),
                            Toast.LENGTH_SHORT).show();

                    mMenu.findItem(R.id.action_favorite).setVisible(false);
                    mMenu.findItem(R.id.action_remove_favorite).setVisible(true);
                } else {

                    Toast.makeText(getApplicationContext(), getResources().getText(R.string.favorite_failed_insertion),
                            Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.action_remove_favorite:

                DatabaseService.instance().removeFavorite(mApplication.getAppID());

                Toast.makeText(getApplicationContext(), getResources().getText(R.string.favorite_removed),
                        Toast.LENGTH_SHORT).show();

                mMenu.findItem(R.id.action_remove_favorite).setVisible(false);
                mMenu.findItem(R.id.action_favorite).setVisible(true);
                break;

            default:
        }

        return super.onOptionsItemSelected(item);
    }
}
