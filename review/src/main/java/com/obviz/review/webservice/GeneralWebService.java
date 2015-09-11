package com.obviz.review.webservice;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;
import com.google.gson.reflect.TypeToken;
import com.obviz.review.Constants;
import com.obviz.review.DiscoverAppsActivity;
import com.obviz.review.adapters.AppBoxAdapter;
import com.obviz.review.adapters.GridAdapter;
import com.obviz.review.adapters.SuperCategoryGridAdapter;
import com.obviz.review.adapters.ReviewsAdapter;
import com.obviz.review.json.MessageParser;
import com.obviz.review.managers.CacheManager;
import com.obviz.review.models.*;
import com.obviz.review.webservice.tasks.HttpTask;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by gaylor on 26.06.15.
 * Requests to onSuccess information
 */
public class GeneralWebService extends WebService {

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

        get(builder, callback, key);
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

            @Override
            public Type getType() {
                return new TypeToken<LinkedList<AndroidApp>>(){}.getType();
            }
        };

        get(builder, callback, key);
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

        get(builder, new RequestCallback<Review.Pager>() {
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

            @Override
            public Type getType() {
                return Review.Pager.class;
            }
        }, key);
    }

    ///
    // C
    // get apps given a specific query (e.g. design) from a category or a type:

    public void getApps(final CategoryBase category, final int pageNo, final int noAppsPerPage, final AppBoxAdapter adapter){
        //for each element in the grid i want to do what is done in getTrendingApps.
        Uri.Builder builder = new Uri.Builder();

        Log.d("WEBSERVICE GET APPS", "PAGENO "+pageNo);

        //TODO: CHANGE THIS TO THE REAL IDS OF THE TOPICS WE WANT:
        //then the list of parameters that we want to add:
        List<Integer> topicIds = Arrays.asList(1);
        builder = constructBuilder(category, Constants.GET_APPS_FILTERED, topicIds, "POSITIVE", noAppsPerPage, pageNo);
        final String key = null;

        get(builder, new RequestCallback<AndroidApp.Pager>() {
            @Override
            public void onSuccess(AndroidApp.Pager result) {

                // Display the empty text if there is no result
                adapter.setMaxPage(result.nbTotalPages);
                adapter.addAll(result.apps);
                adapter.setState(GridAdapter.State.NONE);
            }

            @Override
            public void onFailure(Errors error) {

                adapter.setState(GridAdapter.State.ERRORS);
            }

            @Override
            public Type getType() {
                return AndroidApp.Pager.class;
            }
        }, key);
    }


    public HttpTask<?> getTopApps(final SuperCategoryGridAdapter adapter, final CategoryBase category, final String appType) {

        // have to create the builder for each category!

        if(category.getTitle()!=null)
            Log.d("BASE-CAT",category.getTitle());
        else
            Log.d("BASE-CAT", "Nameless Category - THIS SHOULD NOT HAPPEN");

        //for each element in the grid i want to do what is done in getTrendingApps.
        Uri.Builder builder = new Uri.Builder();
        //TODO: CHANGE THIS TO THE REAL IDS OF THE TOPICS WE WANT:
        //then the list of parameters that we want to add:
        List<Integer> topicIds = Arrays.asList(1);
        if(appType.equals("best"))
            builder = constructBuilder(category, Constants.GET_APPS_FILTERED, topicIds, "POSITIVE", 7, -1);
        if(appType.equals("worst"))
            builder = constructBuilder(category, Constants.GET_APPS_FILTERED, topicIds, "NEGATIVE", 7, -1);

        final String key = null;
        // Here return the httpTask like below and update the map in the adapter

        return get(builder, new RequestCallback<AndroidApp.Pager>() {
            @Override
            public void onSuccess(AndroidApp.Pager result) {

                // Display the empty text if there is no result
                adapter.addAlltoMap(result.apps,category, appType);
            }

            @Override
            public void onFailure(Errors error) {

                adapter.setState(GridAdapter.State.ERRORS);
            }

            @Override
            public Type getType() {
                return AndroidApp.Pager.class;
            }
        }, key);
    }

    //
    ///


    private Uri.Builder constructTopicBuilder(String command, String topicType){
        Uri.Builder builder = new Uri.Builder();
        builder.encodedPath(Constants.URL);
        builder.appendQueryParameter("cmd", command);
        builder.appendQueryParameter("type", topicType);
        Log.d("QUERY", builder.toString());

        return builder;
    }


    private Uri.Builder constructBuilder(CategoryBase categoryBase, String command, List<Integer> topicIds, String posNeg, int nbPerPage, int pageNo){
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

    public HttpTask<?> getTrendingApps(final GridAdapter<AndroidApp> adapter, @Nullable SuperCategory superCategory) {

        // Show the loading icon
        adapter.setState(GridAdapter.State.LOADING);
        // Clear the list to show the empty view
        adapter.clear();



        final String key = null;
                //CacheManager.KeyBuilder.forTrending(superCategory);

        Uri.Builder builder = constructBuilder(superCategory, Constants.GET_TRENDING_APPS);


        return get(builder, new RequestCallback<LinkedList<AndroidApp>>() {
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

            @Override
            public Type getType() {
                return new TypeToken<LinkedList<AndroidApp>>(){}.getType();
            }
        }, key);
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

        post(builder, new RequestCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                Log.d("__MARK_VIEWED__", "Mark as viewed successfully: "+result);
            }

            @Override
            public void onFailure(Errors error) {
                Log.e("__MARK_VIEWED__", "An error occurred during the request: "+error.name());
            }

            @Override
            public Type getType() {
                return Boolean.class;
            }
        });
    }
}
