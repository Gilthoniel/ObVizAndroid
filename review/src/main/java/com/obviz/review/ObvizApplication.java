package com.obviz.review;

import android.app.Application;
import com.obviz.review.database.DatabaseService;
import com.obviz.review.managers.CacheManager;
import com.obviz.review.managers.CategoryManager;
import com.obviz.review.managers.ImagesManager;
import com.obviz.review.managers.TopicsManager;
import com.obviz.review.webservice.GeneralWebService;

/**
 * Created by gaylor on 08/11/2015.
 * Base application to initiate the singletons
 */
public class ObvizApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        GeneralWebService.init();
        DatabaseService.init(getApplicationContext());
        CacheManager.init(getApplicationContext());
        TopicsManager.init();
        ImagesManager.init();
        CategoryManager.init();
    }
}
