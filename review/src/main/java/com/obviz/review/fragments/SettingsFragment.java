package com.obviz.review.fragments;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import com.obviz.reviews.R;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;

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

        findPreference(getString(R.string.pref_key_tutorial_refresh))
                .setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {

                        MaterialShowcaseView.resetAll(preference.getContext());
                        return true;
                    }
                });
    }
}
