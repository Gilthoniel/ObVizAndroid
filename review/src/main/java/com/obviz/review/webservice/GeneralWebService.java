package com.obviz.review.webservice;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Toast;
import com.google.gson.reflect.TypeToken;
import com.obviz.review.Constants;
import com.obviz.review.adapters.AppBaseAdapter;
import com.obviz.review.adapters.ReviewsAdapter;
import com.obviz.review.adapters.AppBoxAdapter;
import com.obviz.review.managers.CacheManager;
import com.obviz.review.managers.TopicsManager;
import com.obviz.review.models.AndroidApp;
import com.obviz.review.models.Review;
import com.obviz.review.models.TopicTitle;
import com.obviz.reviews.R;

import java.lang.reflect.Type;
import java.util.*;

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
     * @param view where sent the results
     */
    public void searchApp(String query, final AbsListView view) {

        // Display the loading icon
        toggleStateList(view, 1);

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
                toggleStateList(view, 0);

                AppBaseAdapter adapter = (AppBaseAdapter) view.getAdapter();

                adapter.clear();
                if (result.size() == 0) {
                    toggleStateList(view, 0);
                }
                adapter.addAll(result);
            }

            @Override
            public void onFailure(Errors error) {
                Log.e("--SEARCH--", "An error occurred during a search");

                toggleStateList(view, 2);

                Toast.makeText(view.getContext(), "Check you internet connection", Toast.LENGTH_LONG).show();
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
     * @param view where to populate the results
     */
    public void getReviews(final String appID, final int topicID, final int page, final int size, final AbsListView view) {

        // Display the loading
        toggleStateList(view, 1);

        // Key for the cache
        final String key = CacheManager.KeyBuilder.forReviews(appID);

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

                // Display the empty text if there is no result
                toggleStateList(view, 0);

                ReviewsAdapter adapter = (ReviewsAdapter) view.getAdapter();

                adapter.addAll(pager.reviews);
            }

            @Override
            public void onFailure(Errors error) {
                toggleStateList(view, 2);
            }

            @Override
            public Type getType() {
                return new TypeToken<Review.Pager>() {}.getType();
            }
        }, key);
    }

    public void getTrendingApps(final AbsListView view, @NonNull List<Constants.Category> categories) {

        // Show the loading icon
        toggleStateList(view, 1);
        // Clear the list to show the empty view
        final AppBoxAdapter adapter = (AppBoxAdapter) view.getAdapter();
        adapter.clear();

        final String key = CacheManager.KeyBuilder.forTrending(categories);

        Uri.Builder builder = new Uri.Builder();
        builder.encodedPath(Constants.URL);
        builder.appendQueryParameter("cmd", Constants.GET_TRENDING_APPS);
        if (categories.size() > 0) {
            List<String> json = new ArrayList<>();
            for (Constants.Category category : categories) {
                json.add(category.name());
            }

            builder.appendQueryParameter("categories", MessageParser.toJson(json));
        }

        get(builder, new RequestCallback<List<AndroidApp>>() {
            @Override
            public void onSuccess(List<AndroidApp> result) {

                // Display the empty text if there is no result
                toggleStateList(view, 0);

                adapter.addAll(result);
                adapter.shuffle(); // Random selection of the trending apps
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Errors error) {

                toggleStateList(view, 2);
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
    public void loadTopicTitles() {

        Uri.Builder builder = new Uri.Builder();
        builder.encodedPath(Constants.URL);
        builder.appendQueryParameter("cmd", Constants.GET_TOPIC_TITLES);

        get(builder, new RequestCallback<List<TopicTitle>>() {
            @Override
            public void onSuccess(List<TopicTitle> result) {
                Map<Integer, String> topics = new HashMap<Integer, String>();
                for (TopicTitle topic : result) {
                    topics.put(topic.getID(), topic.getTitle());
                }

                TopicsManager.instance().setTopicTitles(topics);
            }

            @Override
            public void onFailure(Errors error) {
                Log.e("__TOPIC_TITLES__", "Error when loading the topic titles");
            }

            @Override
            public Type getType() {
                return new TypeToken<List<TopicTitle>>(){}.getType();
            }
        }, null); // Cache is useless because we load that on starting and no more after that
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

    /* Private */

    /**
     * Display the view of the given state in a list view
     * @param view the list
     * @param state the state: 0 - 1 - 2
     */
    private void toggleStateList(AbsListView view, int state) {

        // Hide the views
        view.getEmptyView().findViewById(R.id.empty_text).setVisibility(View.GONE);
        view.getEmptyView().findViewById(R.id.error_message).setVisibility(View.GONE);
        view.getEmptyView().findViewById(R.id.progressBar).setVisibility(View.GONE);

        // Display the good one
        switch (state) {
            case 0:
                view.getEmptyView().findViewById(R.id.empty_text).setVisibility(View.VISIBLE);
                break;
            case 1:
                view.getEmptyView().findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
                break;
            default:
                view.getEmptyView().findViewById(R.id.error_message).setVisibility(View.VISIBLE);
        }
    }
}
