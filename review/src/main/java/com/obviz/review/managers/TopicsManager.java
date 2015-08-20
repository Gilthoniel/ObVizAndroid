package com.obviz.review.managers;

import com.obviz.review.webservice.GeneralWebService;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by gaylor on 28.07.15.
 * Topic titles, etc ...
 */
public class TopicsManager {

    private static TopicsManager instance;

    private Map<Integer, String> topicTitles;
    private Set<TopicsObserver> mObservers;

    private TopicsManager() {
        topicTitles = new HashMap<>();
        mObservers = new HashSet<>();
    }

    public static void init() {
        instance = new TopicsManager();
    }

    public static TopicsManager instance() {

        return instance;
    }

    public void setTopicTitles(Map<Integer, String> collection) {

        topicTitles.putAll(collection);

        for (TopicsObserver observer : mObservers) {
            observer.onTopicsLoaded();
        }
        mObservers.clear();
    }

    public String getTitle(int topicID, TopicsObserver observer) {

        if (topicTitles.containsKey(topicID)) {

            return topicTitles.get(topicID);
        } else {

            mObservers.add(observer);
            GeneralWebService.instance().loadTopicTitles();

            return "";
        }
    }

    public interface TopicsObserver {

        void onTopicsLoaded();
    }
}
