package com.obviz.review.service;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.obviz.review.HomeActivity;
import com.obviz.review.SettingsActivity;
import com.obviz.reviews.R;

import java.util.Calendar;

/**
 * Created by gaylor on 08/24/2015.
 * Scheduled task to send notifications to the user even if the app isn't running
 */
public class AlarmTaskReceiver extends BroadcastReceiver {

    public static final int NOTIFICATION_ID = 42;
    public static final CharSequence NOTIFICATION_TITLE = "There are some new trending apps!";
    public static final CharSequence NOTIFICATION_BODY = "Click to explore them";

    @Override
    public void onReceive(Context context, Intent intent) {

        /* Set an alarm to repeat this task */
        if (intent.getAction() != null && intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {

            setAlarm(context);

        } else {

            /* Notification for the user */
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.favicon)
                    .setContentTitle(NOTIFICATION_TITLE)
                    .setContentText(NOTIFICATION_BODY)
                    .setAutoCancel(true)
                    .setVibrate(new long[] {1000});

            Intent homeIntent = new Intent(context, HomeActivity.class);
            PendingIntent pending = PendingIntent.getActivity(context, 0, homeIntent, 0);
            builder.setContentIntent(pending);

            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(NOTIFICATION_ID, builder.build());
        }
    }

    /**
     * Helper to set an alarm if it is enable
     * @param context Context of the application
     */
    public static void setAlarm(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String key = context.getResources().getString(R.string.pref_key_notifs_enable);
        if (!preferences.getBoolean(key, true)) {
            Log.i("__ALARM__", "disabled");
            return;
        }

        // Set the notification alarm task if not already done
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(context, AlarmTaskReceiver.class);
        PendingIntent pending = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);

        // Remove the current alarm if it already exists
        alarmManager.cancel(pending);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 0);

        alarmManager.setInexactRepeating(AlarmManager.RTC, calendar.getTimeInMillis(),
                1000 * 60 * 60 * 24 * 7, pending);

        Log.d("__ALARM__", "Notification Task planned");
    }
}
