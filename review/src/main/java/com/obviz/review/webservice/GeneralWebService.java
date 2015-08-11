package com.obviz.review.webservice;

import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;
import com.google.gson.reflect.TypeToken;
import com.obviz.review.Constants;
import com.obviz.review.adapters.ResultsAdapter;
import com.obviz.review.adapters.ReviewsAdapter;
import com.obviz.review.adapters.TrendingAdapter;
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

        Map<String, String> params = new HashMap<>();
        params.put("cmd", Constants.GET_APP);
        params.put("weight", "LIGHT");
        params.put("id", id);

        get(params, callback, key);
    }

    /**
     * Execute a research in the database
     * @param query of the search
     * @param view where sent the results
     */
    public void searchApp(String query, final ListView view) {

        // Display the loading icon
        toggleStateList(view, 1);

        // Key for the cache
        final String key = CacheManager.KeyBuilder.forSearch(query);

        Map<String, String> params = new HashMap<>();
        params.put("cmd", Constants.SEARCH_APP);
        params.put("name", query);

        RequestCallback<List<AndroidApp>> callback = new RequestCallback<List<AndroidApp>>() {
            @Override
            public void onSuccess(List<AndroidApp> result) {

                // Display the empty text of there's no result
                toggleStateList(view, 0);

                ResultsAdapter adapter = (ResultsAdapter) view.getAdapter();

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

        get(params, callback, key);
    }

    /**
     * Get the reviews of an android app
     * @param appID ID of the App
     * @param view where to populate the results
     */
    public void getReviews(final String appID, final ListView view) {

        // Display the loading
        toggleStateList(view, 1);

        // Key for the cache
        final String key = CacheManager.KeyBuilder.forReviews(appID);

        Map<String, String> params = new HashMap<>();
        params.put("cmd", Constants.GET_REVIEWS);
        params.put("id", appID);

        get(params, new RequestCallback<List<Review>>() {
            @Override
            public void onSuccess(List<Review> result) {

                // Display the empty text if there is no result
                toggleStateList(view, 0);

                ReviewsAdapter adapter = (ReviewsAdapter) view.getAdapter();

                adapter.addAll(result);
            }

            @Override
            public void onFailure(Errors error) {
                toggleStateList(view, 2);
            }

            @Override
            public Type getType() {
                return new TypeToken<List<Review>>(){}.getType();
            }
        }, key);
    }

    public void getTrendingApps(final AbsListView view, @NonNull List<Constants.Category> categories) {

        // Show the loading icon
        toggleStateList(view, 1);
        // Clear the list to show the empty view
        final TrendingAdapter adapter = (TrendingAdapter) view.getAdapter();
        adapter.clear();

        final String key = CacheManager.KeyBuilder.forTrending(categories);

        Map<String, String> params = new HashMap<>();
        params.put("cmd", Constants.GET_TRENDING_APPS);
        if (categories.size() > 0) {
            List<String> json = new ArrayList<>();
            for (Constants.Category category : categories) {
                json.add(category.name());
            }

            params.put("categories", MessageParser.toJson(json));
        }

        get(params, new RequestCallback<List<AndroidApp>>() {
            @Override
            public void onSuccess(List<AndroidApp> result) {

                // Display the empty text if there is no result
                toggleStateList(view, 0);

                if (result.size() > Constants.NUMBER_TRENDING_APPS) {

                    Set<Integer> indexes = new TreeSet<>();
                    Random random = new Random();
                    while (indexes.size() < Constants.NUMBER_TRENDING_APPS) {
                        indexes.add(random.nextInt(result.size()));
                    }

                    for (Integer index : indexes) {
                        adapter.add(result.get(index));
                    }
                    adapter.notifyDataChanged();

                } else {

                    adapter.addAll(result);
                }
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

        Map<String, String> params = new HashMap<>();
        params.put("cmd", Constants.GET_TOPIC_TITLES);

        get(params, new RequestCallback<List<TopicTitle>>() {
            @Override
            public void onSuccess(List<TopicTitle> result) {

                Log.d("__TOPICS__", result.size() + " topics loaded");
                TopicsManager.instance().setTopicTitles(result);
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
