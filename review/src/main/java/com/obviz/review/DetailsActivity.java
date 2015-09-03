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
import android.widget.TextView;
import android.widget.Toast;
import com.obviz.review.adapters.DetailsPagerAdapter;
import com.obviz.review.adapters.GaugeAdapter;
import com.obviz.review.database.DatabaseService;
import com.obviz.review.fragments.DetailsComparisonFragment;
import com.obviz.review.fragments.DetailsFragment;
import com.obviz.review.fragments.DetailsOpinionsFragment;
import com.obviz.review.managers.CategoryManager;
import com.obviz.review.managers.ImageObserver;
import com.obviz.review.models.AndroidApp;
import com.obviz.review.views.GridRecyclerView;
import com.obviz.review.webservice.ConnectionService;
import com.obviz.review.webservice.GeneralWebService;
import com.obviz.reviews.R;

import java.util.LinkedList;
import java.util.List;

/**
 * Display general information about an application
 * @intent INTENT_APP_ID id of the application
 */
public class DetailsActivity extends AppCompatActivity
        implements GaugeAdapter.GaugeAdaptable, ImageObserver, CategoryManager.CategoryObserver
{

    private AndroidApp app;
    private boolean isInstalled = false;
    private Menu mMenu;

    @Override
    public List<AndroidApp> getListApplications() {

        List<AndroidApp> list = new LinkedList<>();
        list.add(app);

        return list;
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
        app = intent.getParcelableExtra(Constants.INTENT_APP);
        // Check if we have an old state before trying to get the application information
        if (states != null) {

            app = states.getParcelable(Constants.STATE_APP);
        }

        // Add a seen to the app
        GeneralWebService.instance().markAsViewed(app.getAppID());

        // Activity title
        setTitle(app.getName());

        /* Tabs */
        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        if (pager != null) {
            DetailsPagerAdapter adapter = new DetailsPagerAdapter(getSupportFragmentManager(), app);

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
        } else {
            DetailsFragment.populateDetailsField(app, findViewById(R.id.details), this);

            DetailsComparisonFragment.populateAlternatives(app, findViewById(R.id.details_id));

            DetailsOpinionsFragment.populateOpinions((GridRecyclerView) findViewById(R.id.opinions_grid), app);
        }

        List<PackageInfo> packages = getPackageManager().getInstalledPackages(0);
        for (PackageInfo info : packages) {
            if (info.packageName.equals(app.getAppID())) {
                isInstalled = true;
            }
        }

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

        states.putParcelable(Constants.STATE_APP, app);

        super.onSaveInstanceState(states);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_details, menu);

        mMenu = menu;
        if (app != null) {
            mMenu.findItem(R.id.action_install).setEnabled(true);

            if (DatabaseService.instance().isInFavorite(app.getAppID())) {

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
                } else if (app != null) {

                    final String packageName = app.getAppID();

                    // Catch if the Google Play Store isn't installed
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)));
                    } catch (ActivityNotFoundException e) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + packageName)));
                    }
                }
                break;

            case R.id.action_favorite:

                if (DatabaseService.instance().insertFavorite(app) > 0) {

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

                DatabaseService.instance().removeFavorite(app.getAppID());

                Toast.makeText(getApplicationContext(), getResources().getText(R.string.favorite_removed),
                        Toast.LENGTH_SHORT).show();

                mMenu.findItem(R.id.action_remove_favorite).setVisible(false);
                mMenu.findItem(R.id.action_favorite).setVisible(true);
                break;

            default:
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onImageLoaded(String url, Bitmap bitmap) {
        ((ImageView) findViewById(R.id.app_logo)).setImageBitmap(bitmap);
    }

    @Override
    public void onCategoriesLoaded() {
        ((TextView) findViewById(R.id.app_category))
                .setText(CategoryManager.instance().getFrom(app.getCategory(), this).title);
    }
}
