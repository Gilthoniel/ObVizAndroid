package com.obviz.review;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import com.obviz.review.fragments.HomeFragment;
import com.obviz.reviews.R;

/**
 * Settings activity for the user
 */
public class SettingsActivity extends AppCompatActivity {

    /*
     * Override anonymous for Settings activity because we can't
     * create a fragment with support v4 for the settings
     */
    public static final HomeFragment homeFragment = new HomeFragment() {
        @Override
        public void showTutorial() {}

        @Override
        public String getTitle() {
            return "Settings";
        }

        @Override
        public int getIcon() {
            return R.drawable.ic_settings_black_24dp;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        /* Toolbar */
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getString(R.string.title_activity_settings));
        }
    }
}
