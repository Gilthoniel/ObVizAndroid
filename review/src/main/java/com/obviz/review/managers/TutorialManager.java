package com.obviz.review.managers;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import com.obviz.reviews.R;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

/**
 * Created by gaylor on 09/04/2015.
 * Manager to build tutorial for different activities
 */
public class TutorialManager {

    public static MaterialShowcaseView.Builder single(Activity activity) {

        return new MaterialShowcaseView.Builder(activity)
                .setDismissText("Got it!")
                .setMaskColour(getColor(activity, R.color.tutorialBackground));
    }

    public static MaterialShowcaseSequence sequence(Activity activity){

        ShowcaseConfig config = new ShowcaseConfig();
        config.setMaskColor(getColor(activity, R.color.tutorialBackground));

        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(activity);
        sequence.setConfig(config);

        return sequence;
    }

    private static int getColor(Context context, int id) {

        if (Build.VERSION.SDK_INT >= 23) {

            return context.getResources().getColor(id, context.getTheme());
        } else {

            return context.getResources().getColor(id);
        }
    }
}
