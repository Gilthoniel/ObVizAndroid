package com.obviz.review.webservice;

import android.net.Uri;
import com.obviz.review.webservice.tasks.HttpTask;
import com.obviz.review.webservice.tasks.ImageTask;
import com.obviz.review.webservice.tasks.JsonTask;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by gaylor on 26.06.15.
 * Asynchronous tasks to interact with the back-end
 */
public class ConnectionService {

    public enum TypeRequest { GET, POST }

    public static final ConnectionService instance = new ConnectionService();

    private ExecutorService executor;
    private Set<HttpTask<?>> requests;

    private ConnectionService() {
        executor = Executors.newCachedThreadPool();
        requests = new HashSet<>();
    }

    /**
     * Change the implementation of the stack of requests
     * @param set the set
     */
    public synchronized void setRequestsSet(Set<HttpTask<?>> set) {
        requests = set;
    }

    public synchronized void addRequest(HttpTask<?> task) {
        requests.add(task);
    }

    public synchronized void removeRequest(HttpTask<?> task) {
        requests.remove(task);
    }

    /**
     * Cancel running requests
     */
    public void cancel() {
        for (HttpTask<?> task : requests) {
            task.closeStream();
            task.cancel(true);
        }

        requests.clear();
    }

    /**
     * Execute an HTTP request
     * @param url URL of the request
     * @param params Queries of the request
     * @param callback callback function when the request is over
     * @param isPostRequest True if the request is a POST request
     * @param <T> Type of the return object
     * @return An instance of the task (cancellable)
     */
    public <T> HttpTask<T> execute(String url, Map<String, String> params, RequestCallback<T> callback, String cacheKey,
                                          boolean isPostRequest) {

        // Create the url
        Uri.Builder builder = new Uri.Builder();
        builder.encodedPath(url);
        for (String key : params.keySet()) {
            builder.appendQueryParameter(key, params.get(key));
        }

        HttpTask<T> task = new JsonTask<>(callback, isPostRequest, cacheKey);
        addRequest(task);

        return (HttpTask<T>) task.executeOnExecutor(executor, builder);
    }

    /**
     * Load an image from the url
     * @param url URL of the image
     * @return Task
     */
    public ImageTask loadImage(String url) {

        Uri.Builder builder = new Uri.Builder();
        builder.encodedPath(url);

        ImageTask task = new ImageTask();
        requests.add(task);

        return (ImageTask) task.executeOnExecutor(executor, builder);
    }
}
