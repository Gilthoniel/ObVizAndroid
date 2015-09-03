package com.obviz.review.webservice;

import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;
import com.google.gson.reflect.TypeToken;
import com.obviz.review.Constants;
import com.obviz.review.adapters.GridAdapter;
import com.obviz.review.adapters.ReviewsAdapter;
import com.obviz.review.json.MessageParser;
import com.obviz.review.managers.CacheManager;
import com.obviz.review.managers.CategoryManager;
import com.obviz.review.models.*;
import com.obviz.review.webservice.tasks.HttpTask;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gaylor on 26.06.15.
 * Requests to onSuccess information
 */
public class GeneralWebService extends WebService {

    private static GeneralWebService instance;

    private GeneralWebService() {}

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

        RequestCallback<List<AndroidApp>> callback = new RequestCallback<List<AndroidApp>>() {
            @Override
            public void onSuccess(List<AndroidApp> result) {

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
                return new TypeToken<List<AndroidApp>>(){}.getType();
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
        final String key = CacheManager.KeyBuilder.forReviews(appID, page, size);

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
                return new TypeToken<Review.Pager>() {}.getType();
            }
        }, key);
    }

    public HttpTask<?> getTrendingApps(final GridAdapter<AndroidApp> adapter, @Nullable SuperCategory superCategory) {

        // Show the loading icon
        adapter.setState(GridAdapter.State.LOADING);
        // Clear the list to show the empty view
        adapter.clear();

        final String key = CacheManager.KeyBuilder.forTrending(superCategory);

        Uri.Builder builder = new Uri.Builder();
        builder.encodedPath(Constants.URL);
        builder.appendQueryParameter("cmd", Constants.GET_TRENDING_APPS);
        if (superCategory != null) {
            List<String> json = new ArrayList<>();
            for (Category category : CategoryManager.instance().getCategories(superCategory._id)) {
                json.add(category.category);
            }

            if (json.size() > 0) {
                builder.appendQueryParameter("categories", MessageParser.toJson(json));
            }
        }

        return get(builder, new RequestCallback<List<AndroidApp>>() {
            @Override
            public void onSuccess(List<AndroidApp> result) {

                // Display the empty text if there is no result
                adapter.setState(GridAdapter.State.NONE);

                adapter.addAll(result);
                adapter.shuffle(); // Random selection of the trending apps
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Errors error) {

                adapter.setState(GridAdapter.State.ERRORS);
            }

            @Override
            public Type getType() {
                return new TypeToken<List<AndroidApp>>(){}.getType();
            }
        }, key);
    }

    /**
     * Load the list of topics for the opinions of the app
     */
    public void getTopics(RequestCallback<List<Topic>> callback) {

        Uri.Builder builder = new Uri.Builder();
        builder.encodedPath(Constants.URL);
        builder.appendQueryParameter("cmd", Constants.GET_TOPIC_TITLES);

        get(builder, callback, null);
    }

    public void getCategories(RequestCallback<List<Category>> callback) {

        Uri.Builder builder = new Uri.Builder();
        builder.encodedPath(Constants.URL);
        builder.appendQueryParameter("cmd", Constants.GET_CATEGORIES);

        get(builder, callback, null);
    }

    public void getSuperCategories(RequestCallback<List<SuperCategory>> callback) {

        Uri.Builder builder = new Uri.Builder();
        builder.encodedPath(Constants.URL);
        builder.appendQueryParameter("cmd", Constants.GET_CATEGORIES_TYPES);

        get(builder, callback, null);
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
