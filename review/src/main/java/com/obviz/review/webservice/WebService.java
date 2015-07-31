package com.obviz.review.webservice;

import android.util.Log;
import com.obviz.review.Constants;

import java.util.Map;

/**
 * Created by gaylor on 27.06.15.
 * Base of a web service implementation
 */
public abstract class WebService {
    private String baseURL;

    public WebService() {
        baseURL = Constants.URL;
    }

    /**
     * Change the url of the requests
     * @param url the enw URL
     */
    public void setBaseURL(String url) {
        baseURL = url;
    }

    /**
     * GET HTTP request
     * @param params queries parameter
     * @param callback call when the request is over
     * @param <T> Type of the object requested
     */
    public <T> void get(Map<String, String> params, RequestCallback<T> callback, String cacheKey) {

        ConnectionService.instance.execute(baseURL, params, callback, cacheKey, false);
    }

    /**
     * POST HTTP request
     * @param params query parameters
     * @param callback callback function
     * @param <T> type of the return
     */
    public <T> void post(Map<String, String> params, RequestCallback<T> callback) {

        // POST request without caching
        ConnectionService.instance.execute(baseURL, params, callback, null, true);
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
