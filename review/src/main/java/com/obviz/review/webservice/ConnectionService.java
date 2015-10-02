package com.obviz.review.webservice;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.obviz.review.service.NetworkChangeReceiver;
import com.obviz.review.webservice.tasks.GetTask;
import com.obviz.review.webservice.tasks.PostTask;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by gaylor on 26.06.15.
 * Asynchronous tasks to interact with the back-end
 */
public class ConnectionService {

    private static ConnectionService instance;

    private ExecutorService executor;
    private Set<AsyncTask<Void,?,?>> requests;
    private Set<AsyncTask<Void,?,?>> waitingRequests;
    private Context mContext;

    private ConnectionService(Context context) {
        executor = Executors.newCachedThreadPool();
        requests = new HashSet<>();
        waitingRequests = new HashSet<>();
        mContext = context;
    }

    public static ConnectionService instance() {
        if (instance == null) {
            throw new IllegalStateException("Must be initialized");
        }

        return instance;
    }

    public static void init(Context context) {
        instance = new ConnectionService(context);
    }

    public ExecutorService getExecutor() {
        return executor;
    }

    /**
     * Change the implementation of the stack of requests
     * @param set the set
     */
    public synchronized void setRequestsSet(Set<AsyncTask<Void,?,?>> set) {
        requests = set;
    }

    public synchronized void addRequest(AsyncTask<Void,?,?> task) {
        requests.add(task);
    }

    public synchronized void removeRequest(AsyncTask<Void,?,?> task) {
        requests.remove(task);
    }

    public synchronized void awakeAll() {

        for (AsyncTask<Void,?,?> task : waitingRequests) {
            requests.add(task);
            task.executeOnExecutor(executor);
        }
        waitingRequests.clear();
    }

    /**
     * Cancel running requests
     */
    public void cancel() {
        Log.i("__CONNECTION__", "Cancellation asked");

        for (AsyncTask<?,?,?> task : requests) {
            task.cancel(true);
        }

        requests.clear();
    }

    /**
     * Execute an HTTP request
     * @param task GetTask
     * @param <T> Type of the return object
     * @return An instance of the task (cancellable)
     */
    public <T extends Serializable> AsyncTask<Void,?,?> executeGetRequest(GetTask<T> task) {

        // Execute only if we have internet, else we know that the request will fail
        if (NetworkChangeReceiver.isInternetAvailable(mContext)) {
            addRequest(task);
            task.executeOnExecutor(executor);
        } else {
            waitingRequests.add(task);
        }

        return task;
    }

    public AsyncTask<Void,?,?> executePostRequest(PostTask task) {

        addRequest(task);

        return task.executeOnExecutor(executor);
    }
}
