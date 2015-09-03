package com.obviz.review.webservice;

import android.net.Uri;
import android.util.Log;
import com.obviz.review.Constants;
import com.obviz.review.webservice.tasks.HttpTask;

import java.util.Map;

/**
 * Created by gaylor on 27.06.15.
 * Base of a web service implementation
 */
public abstract class WebService {

    /**
     * GET HTTP request
     * @param builder queries parameter
     * @param callback call when the request is over
     * @param <T> Type of the object requested
     */
    public <T> HttpTask<T> get(Uri.Builder builder, RequestCallback<T> callback, String cacheKey) {

        return ConnectionService.instance.executeGetRequest(builder, callback, cacheKey);
    }

    /**
     * POST HTTP request
     * @param builder query parameters
     * @param callback callback function
     */
    public void post(Uri.Builder builder, RequestCallback<Boolean> callback) {

        // POST request without caching
        ConnectionService.instance.executePostRequest(builder, callback);
    }

    /**
     * Load an image and populate the targeted view
     * @param url of the image
     */
    public void loadImage(String url) {

        Log.i("__IMAGE__", "Try to acquire image");

        ConnectionService.instance.loadImage(url);
    }
}
