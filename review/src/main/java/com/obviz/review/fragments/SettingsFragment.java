package com.obviz.review.fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import com.obviz.reviews.R;

/**
 * Created by gaylor on 09/04/2015.
 * Settings fragment
 */
public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle states) {
        super.onCreate(states);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }
}
