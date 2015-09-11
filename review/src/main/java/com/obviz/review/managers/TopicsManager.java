package com.obviz.review.managers;

import android.content.Context;
import android.os.AsyncTask;
import com.obviz.review.models.Topic;
import com.obviz.review.service.NetworkChangeReceiver;
import com.obviz.review.webservice.GeneralWebService;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by gaylor on 28.07.15.
 * Manager to get the Topics
 */
public class TopicsManager {

    private static TopicsManager instance;
    private static final String DEFAULT = "";

    private Map<Integer, Topic> mTopics;
    private Set<TopicsObserver> mObservers;
    private ReentrantLock mLock;
    private Context mContext;

    private TopicsManager(Context context) {

        mLock = new ReentrantLock();
        mObservers = new HashSet<>();
        mContext = context;

        new Initialization().execute();
    }

    /**
     * Call at the beginning of the application
     * @param context Context of the application
     */
    public static void init(Context context) {
        instance = new TopicsManager(context);
    }

    public static TopicsManager instance() {
        return instance;
    }

    /**
     * Retry to get the information if the lock is available
     * This function is called by the Network listener when the connection turn on
     */
    public void awake() {
        if (mObservers.size() > 0 && !mLock.isLocked()) {
            new Initialization().execute();
        }
    }

    /**
     * __FRIDAY_9112015__
     * @param type Type of the topic
     * @return List of filtered topics or an empty list if the topics aren't loaded
     */
    public List<Topic> getTopics(Topic.Type type, TopicsObserver observer){

        // __FRIDAY_9112015__
        if (mTopics != null) {
            List<Topic> ret = new ArrayList<>();
            for (Topic t:mTopics.values()){
                if(t.getType() == type)
                    ret.add(t);
            }
            return ret;
        }

        plannedInit(observer);

        return Collections.emptyList();
    }

    /**
     * Get a topic by its ID, or null if the data are not already loaded
     * @param topicID ID of the topic
     * @param observer instance of the observer where the signal must be sent
     * @return the topic or null
     */
    public Topic getTopic(int topicID, TopicsObserver observer) {

        if (mTopics != null && mTopics.containsKey(topicID)) {

            return mTopics.get(topicID);
        }

        if (topicID > 0) {
            plannedInit(observer);
        }
        return null;
    }

    /**
     * Get the title of the topic or an empty String if the data are not loaded
     * @param topicID ID of the topic
     * @param observer Observer where to send the signal of loading
     * @return Title or empty String
     */
    public String getTitle(int topicID, TopicsObserver observer) {

        Topic topic = getTopic(topicID, observer);
        if (topic != null) {
            return topic.getTitle();
        } else {
            return DEFAULT;
        }
    }

    /**
     * Check if we can plan an initialization
     * @param observer Observer of the initialization
     */
    private void plannedInit(TopicsObserver observer) {

        mObservers.add(observer);

        if (!mLock.isLocked() && NetworkChangeReceiver.isInternetAvailable(mContext)) {
            new Initialization().execute();
        }
    }

    /**
     * Background task for initialization
     */
    private class Initialization extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {

            mLock.lock();

            mTopics = new HashMap<>();

            List<Topic> topics = GeneralWebService.instance().getTopics();
            if (topics != null) {
                for (Topic topic : topics) {
                    mTopics.put(topic.getID(), topic);
                }
            }

            mLock.unlock();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            for (TopicsObserver observer : mObservers) {
                observer.onTopicsLoaded();
            }
            mObservers.clear();
        }
    }

    /**
     * Interface to signal observers that the topics are loaded
     */
    public interface TopicsObserver {
        void onTopicsLoaded();
    }
}
