package com.obviz.review.webservice;

import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;
import com.google.gson.reflect.TypeToken;
import com.obviz.review.Constants;
import com.obviz.review.adapters.AppBoxFullAdapter;
import com.obviz.review.adapters.GridAdapter;
import com.obviz.review.adapters.ReviewsAdapter;
import com.obviz.review.json.MessageParser;
import com.obviz.review.managers.CacheManager;
import com.obviz.review.models.*;
import com.obviz.review.webservice.tasks.GetTask;
import com.obviz.review.webservice.tasks.PostTask;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by gaylor on 26.06.15.
 * Requests to onSuccess information
 */
public class GeneralWebService {

    private static GeneralWebService instance;

    public static void init() {

        instance = new GeneralWebService();
    }

    public static GeneralWebService instance() {
        return instance;
    }

    /**
     * Get information about an application on the Google Play Store
     * @param id id of the application
     * @param callback callback functions call when the request is over
     */
    public void getApp(String id, RequestCallback<AndroidApp> callback) {

        // Key for the cache
        final String key = CacheManager.KeyBuilder.forApps(id);

        Uri.Builder builder = new Uri.Builder();
        builder.encodedPath(Constants.URL);
        builder.appendQueryParameter("cmd", Constants.GET_APP);
        builder.appendQueryParameter("weight", "LIGHT");
        builder.appendQueryParameter("id", id);

        GetTask<AndroidApp> task = new GetTask<>(builder.build(), AndroidApp.class, callback);
        task.setCacheKey(key);

        ConnectionService.instance().executeGetRequest(task);
    }

    /**
     * Execute a research in the database
     * @param query of the search
     * @param adapter where sent the results
     */
    public void searchApp(String query, final GridAdapter<AndroidApp> adapter) {

        // State loading
        adapter.setState(GridAdapter.State.LOADING);

        // Key for the cache
        final String key = CacheManager.KeyBuilder.forSearch(query);

        Uri.Builder builder = new Uri.Builder();
        builder.encodedPath(Constants.URL);
        builder.appendQueryParameter("cmd", Constants.SEARCH_APP);
        builder.appendQueryParameter("name", query);

        RequestCallback<LinkedList<AndroidApp>> callback = new RequestCallback<LinkedList<AndroidApp>>() {
            @Override
            public void onSuccess(LinkedList<AndroidApp> result) {

                // Display the empty text of there's no result
                adapter.setState(GridAdapter.State.NONE);

                adapter.clear();
                adapter.addAll(result);
            }

            @Override
            public void onFailure(Errors error) {
                Log.e("--SEARCH--", "An error occurred during a search: " + error);

                adapter.setState(GridAdapter.State.ERRORS);
            }
        };

        Type type = new TypeToken<LinkedList<AndroidApp>>(){}.getType();
        GetTask task = new GetTask<>(builder.build(), type, callback);
        task.setCacheKey(key);

        ConnectionService.instance().executeGetRequest(task);
    }

    /**
     * Get the reviews of an android app
     * @param appID ID of the App
     * @param adapter where to populate the results
     */
    public void getReviews(final String appID, final int topicID, final int page, final int size, final ReviewsAdapter adapter) {

        // Turn on the loading
        adapter.setState(GridAdapter.State.LOADING);

        // Key for the cache
        final String key = CacheManager.KeyBuilder.forReviews(appID, topicID, page, size);

        Uri.Builder builder = new Uri.Builder();
        builder.encodedPath(Constants.URL);
        builder.appendQueryParameter("cmd", Constants.GET_REVIEWS);
        builder.appendQueryParameter("id", appID);
        builder.appendQueryParameter("topic_id", String.valueOf(topicID));
        builder.appendQueryParameter("nb_per_page", String.valueOf(size));
        builder.appendQueryParameter("page_nr", String.valueOf(page));

        GetTask task = new GetTask<>(builder.build(), Review.Pager.class, new RequestCallback<Review.Pager>() {
            @Override
            public void onSuccess(Review.Pager pager) {

                adapter.setMaxPage(pager.nbTotalPages);
                adapter.addAll(pager.reviews);
                adapter.setState(GridAdapter.State.NONE);
            }

            @Override
            public void onFailure(Errors error) {
                adapter.setState(GridAdapter.State.ERRORS);
            }

        });
        task.setCacheKey(key);

        ConnectionService.instance().executeGetRequest(task);
    }

    ///
    // C
    // get apps given a specific query (e.g. design) from a category or a type:

    public void getApps(final CategoryBase category, final int pageNo, final int noAppsPerPage, final AppBoxFullAdapter adapter, List<Integer> topicIds){

        adapter.setState(GridAdapter.State.LOADING);

        //last param - review number:
        Uri.Builder builder = constructBuilder(category, Constants.GET_APPS_FILTERED, topicIds, "POSITIVE", noAppsPerPage, pageNo, 1);
        final String key = null;

        GetTask task = new GetTask<>(builder.build(), AndroidFullApp.Pager.class, new RequestCallback<AndroidFullApp.Pager>() {
            @Override
            public void onSuccess(AndroidFullApp.Pager result) {

                // Display the empty text if there is no result
                adapter.setMaxPage(result.nbTotalPages);
                adapter.addAll(result.apps);
                adapter.setState(GridAdapter.State.NONE);
            }

            @Override
            public void onFailure(Errors error) {

                adapter.setState(GridAdapter.State.ERRORS);
            }
        });
        task.setCacheKey(key);

        ConnectionService.instance().executeGetRequest(task);
    }

    private Uri.Builder constructBuilder(CategoryBase categoryBase, String command, List<Integer> topicIds, String posNeg, int nbPerPage, int pageNo, int revNo){
        Uri.Builder builder = new Uri.Builder();
        builder.encodedPath(Constants.URL);
        builder.appendQueryParameter("cmd", command);
        if (categoryBase != null) {
            List<String> json = new ArrayList<>();
            for (Category category : categoryBase.getCategories()) {
                json.add(category.category);
            }

            if (json.size() > 0) {
                builder.appendQueryParameter("categories", MessageParser.toJson(json));
            }
        }


        builder.appendQueryParameter("topic_ids", MessageParser.toJson(topicIds));
        builder.appendQueryParameter("type", posNeg);
        if(nbPerPage>0)
            builder.appendQueryParameter("nb_per_page", Integer.toString(nbPerPage));
        if(pageNo>=0)
            builder.appendQueryParameter("page_nr", Integer.toString(pageNo));

        if(revNo>0)
            builder.appendQueryParameter("nb_reviews", Integer.toString(revNo));

        Log.d("QUERY", builder.toString());

        return builder;
    }


    private Uri.Builder constructBuilder(CategoryBase categoryBase, String command){
        Uri.Builder builder = new Uri.Builder();
        builder.encodedPath(Constants.URL);
        builder.appendQueryParameter("cmd", command);
        if (categoryBase != null) {
            List<String> json = new ArrayList<>();
            for (Category category : categoryBase.getCategories()) {
                json.add(category.category);
            }

            if (json.size() > 0) {
                builder.appendQueryParameter("categories", MessageParser.toJson(json));
            }
        }
        return builder;
    }

    public void getTrendingApps(final GridAdapter<AndroidApp> adapter, @Nullable SuperCategory superCategory) {

        // Show the loading icon
        adapter.setState(GridAdapter.State.LOADING);
        // Clear the list to show the empty view
        adapter.clear();

        Uri.Builder builder = constructBuilder(superCategory, Constants.GET_TRENDING_APPS);

        Type type = new TypeToken<LinkedList<AndroidApp>>(){}.getType();
        GetTask task = new GetTask<>(builder.build(), type, new RequestCallback<LinkedList<AndroidApp>>() {
            @Override
            public void onSuccess(LinkedList<AndroidApp> result) {

                // Display the empty text if there is no result
                adapter.setState(GridAdapter.State.NONE);

                adapter.addAll(result);
                adapter.shuffle(); // Random selection of the trending apps
            }

            @Override
            public void onFailure(Errors error) {

                adapter.setState(GridAdapter.State.ERRORS);
            }
        });

        ConnectionService.instance().executeGetRequest(task);
    }

    /**
     * Get the headline information
     * @param callback action to perform after loading
     */
    public void getHeadline(CategoryBase categoryBase, RequestCallback<Headline> callback) {

        Uri.Builder builder = new Uri.Builder();
        builder.encodedPath(Constants.URL);
        builder.appendQueryParameter("cmd", Constants.GET_HEADLINE);
        if(categoryBase!=null){
            List<String> l = new ArrayList<>();
            for (Category c:categoryBase.getCategories()){
                l.add(c.category);
            }
            if(l.size()>0)
                builder.appendQueryParameter("categories", MessageParser.toJson(l));
        }

        ConnectionService.instance().executeGetRequest(new GetTask<>(builder.build(), Headline.class, callback));
    }

    /**
     * Load the list of topics for the opinions of the app
     */
    public List<Topic> getTopics() {

        try {
            Uri.Builder builder = new Uri.Builder();
            builder.encodedPath(Constants.URL);
            builder.appendQueryParameter("cmd", Constants.GET_TOPIC_TITLES);

            URL url = new URL(builder.build().toString());
            URLConnection connection = url.openConnection();

            return MessageParser.fromJson(new InputStreamReader(connection.getInputStream()),
                    new TypeToken<List<Topic>>(){}.getType());

        } catch (IOException e) {

            Log.e("__TOPICS__", "Message: " + e.getMessage());
            return null;
        }
    }

    public List<Category> getCategories() {

        try {
            Uri.Builder builder = new Uri.Builder();
            builder.encodedPath(Constants.URL);
            builder.appendQueryParameter("cmd", Constants.GET_CATEGORIES);

            URL url = new URL(builder.build().toString());
            URLConnection connection = url.openConnection();
            return MessageParser.fromJson(new InputStreamReader(connection.getInputStream()),
                    new TypeToken<List<Category>>(){}.getType());

        } catch (IOException e) {

            Log.e("__CATEGORIES__", "Message: " + e.getMessage());
            return null;
        }
    }

    public List<SuperCategory> getSuperCategories() {

        try {
            Uri.Builder builder = new Uri.Builder();
            builder.encodedPath(Constants.URL);
            builder.appendQueryParameter("cmd", Constants.GET_CATEGORIES_TYPES);

            URL url = new URL(builder.build().toString());
            URLConnection connection = url.openConnection();
            return MessageParser.fromJson(new InputStreamReader(connection.getInputStream()),
                    new TypeToken<List<SuperCategory>>(){}.getType());

        } catch (IOException e) {

            Log.e("__CATEGORIES__", "Message: " + e.getMessage());
            return null;
        }
    }

    /* POST requests */

    public void markAsViewed(String appID) {

        Uri.Builder builder = new Uri.Builder();
        builder.encodedPath(Constants.URL);
        builder.appendQueryParameter("cmd", Constants.APP_VIEWED);
        builder.appendQueryParameter("id", appID);

        PostTask task = new PostTask(builder.build(), new RequestCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                Log.d("__MARK_VIEWED__", "Mark as viewed successfully: " + result);
            }

            @Override
            public void onFailure(Errors error) {
                Log.e("__MARK_VIEWED__", "An error occurred during the request: "+error.name());
            }
        });
        ConnectionService.instance().executePostRequest(task);
    }
}
