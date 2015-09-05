package com.obviz.review;

import android.app.Activity;
import android.os.Bundle;
import com.obviz.review.fragments.SettingsFragment;

/**
 * Settings activity for the user
 */
public class SettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
}
