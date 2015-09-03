package com.obviz.review.managers;

import android.util.Log;
import com.google.gson.reflect.TypeToken;
import com.obviz.review.models.Topic;
import com.obviz.review.webservice.GeneralWebService;
import com.obviz.review.webservice.RequestCallback;

import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by gaylor on 28.07.15.
 * Topic titles, etc ...
 */
public class TopicsManager {

    private static TopicsManager instance;
    private static final String DEFAULT = "";

    private Map<Integer, Topic> mTopics;
    private Set<TopicsObserver> mObservers;
    private ReentrantLock mLock;

    private TopicsManager() {
        mTopics = new HashMap<>();
        mObservers = new HashSet<>();

        mLock = new ReentrantLock();
        mLock.lock();

        GeneralWebService.instance().getTopics(new TopicsCallback());
    }

    public static void init() {
        instance = new TopicsManager();
    }

    public static TopicsManager instance() {
        return instance;
    }

    public Topic getTopic(int topicID, TopicsObserver observer) {
        if (topicID < 0) {
            return null;
        }

        mLock.lock();

        if (mTopics.containsKey(topicID)) {

            mLock.unlock();
            return mTopics.get(topicID);
        }

        mObservers.add(observer);
        GeneralWebService.instance().getTopics(new TopicsCallback());

        return null;
    }

    public String getTitle(int topicID, TopicsObserver observer) {

        Topic topic = getTopic(topicID, observer);
        if (topic != null) {
            return topic.getTitle();
        } else {
            return DEFAULT;
        }
    }

    public interface TopicsObserver {
        void onTopicsLoaded();
    }

    private class TopicsCallback implements RequestCallback<List<Topic>> {

        @Override
        public void onSuccess(List<Topic> result) {

            instance.mTopics.clear();

            for (Topic topic : result) {
                mTopics.put(topic.getID(), topic);
            }

            mLock.unlock();

            for (TopicsObserver observer : mObservers) {
                observer.onTopicsLoaded();
            }
            mObservers.clear();
        }

        @Override
        public void onFailure(Errors error) {
            mLock.unlock();
            Log.e("__TOPICS__", "Error when loading topics: " + error);
        }

        @Override
        public Type getType() {
            return new TypeToken<List<Topic>>(){}.getType();
        }
    }
}
