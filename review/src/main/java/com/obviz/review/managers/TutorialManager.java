package com.obviz.review.managers;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.Target;
import com.obviz.reviews.R;

/**
 * Created by gaylor on 09/04/2015.
 * Manager to build tutorial for different activities
 */
public class TutorialManager {

    public static class Builder {

        public String mText;
        public String mTitle;
        public Target mTarget;
        public String mKey;

        public void show(Activity activity) {

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
            String keyTutorialEnabled = activity.getResources().getString(R.string.pref_key_tutorial_enable);

            if (preferences.getBoolean(keyTutorialEnabled, true) && !preferences.getBoolean(mKey, false)) {
                new ShowcaseView.Builder(activity, true)
                        .setTarget(mTarget)
                        .setContentTitle(mTitle)
                        .setContentText(mText)
                        .setShowcaseEventListener(new EventListener(mKey, activity))
                        .setStyle(R.style.CustomTutorialTheme)
                        .build();
            }
        }
    }

    /**
     * Event listener for the "Got it" button
     */
    public static class EventListener implements OnShowcaseEventListener{

        private String mKey;
        private Context mContext;

        public EventListener(String key, Context context) {
            mKey = key;
            mContext = context;
        }

        @Override
        public void onShowcaseViewHide(ShowcaseView showcaseView) {
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(mContext).edit();
            editor.putBoolean(mKey, true);
            editor.apply();
        }

        @Override
        public void onShowcaseViewDidHide(ShowcaseView showcaseView) {}

        @Override
        public void onShowcaseViewShow(ShowcaseView showcaseView) {}
    }
}
