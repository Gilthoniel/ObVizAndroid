package com.obviz.review.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.obviz.review.managers.CategoryManager;
import com.obviz.review.managers.TopicsManager;

/**
 * Created by gaylor on 09/04/2015.
 * Actions to perform when the network turn on again
 */
public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {

        if (isInternetAvailable(context)) {
            CategoryManager.instance().awake();
            TopicsManager.instance().awake();
        }
    }

    /**
     * Helper to get the connectivity info with a context
     * @param context Context of the application
     * @return true if Internet is available
     */
    public static boolean isInternetAvailable(Context context) {
        final ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        final NetworkInfo internetInfo = manager.getActiveNetworkInfo();
        return internetInfo != null && internetInfo.isConnected();
    }
}
