package com.obviz.review.webservice;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by gaylor on 27.06.15.
 * Base of a web service implementation
 */
public abstract class WebService {
    private List<ConnectionService.HttpTask<?>> tasks;
    private String baseURL;

    public WebService() {
        tasks = new ArrayList<>();
        baseURL = ConnectionService.URL;
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
    public <T> void get(Map<String, String> params, RequestCallback<T> callback) {

        ConnectionService.HttpTask<T> task = ConnectionService.execute(baseURL, params, callback, false);
        tasks.add(task);
    }

    /**
     * POST HTTP request
     * @param params query parameters
     * @param callback callback function
     * @param <T> type of the return
     */
    public <T> void post(Map<String, String> params, RequestCallback<T> callback) {

        ConnectionService.HttpTask<T> task = ConnectionService.execute(baseURL, params, callback, true);
    }

    /**
     * Remove all the tasks currently running
     */
    public void cancel() {
        Iterator<ConnectionService.HttpTask<?>> iterator = tasks.iterator();
        while (iterator.hasNext()) {
            ConnectionService.HttpTask<?> task = iterator.next();
            if (!task.isCancelled()) {
                task.cancel(true);
            }

            iterator.remove();
        }
    }
}
