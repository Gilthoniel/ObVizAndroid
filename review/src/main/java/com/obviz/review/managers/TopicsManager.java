package com.obviz.review.managers;

import com.obviz.review.models.TopicTitle;

import java.util.*;

/**
 * Created by gaylor on 28.07.15.
 * Topic titles, etc ...
 */
public class TopicsManager {

    public static final TopicsManager instance = new TopicsManager();

    private Map<Integer, List<String>> topicTitles;
    private List<Integer> ids;

    private TopicsManager() {
        topicTitles = new HashMap<>();
        ids = new ArrayList<>();
    }

    public void setTopicTitles(Collection<TopicTitle> collection) {

        topicTitles.clear();
        for (TopicTitle topic : collection) {
            topicTitles.put(topic.getID(), topic.getTitles());
        }

        ids = new ArrayList<>(topicTitles.keySet());
    }

    public List<Integer> getIDs() {
        return ids;
    }

    public List<String> getTopicsTitle() {
        List<String> titles = new ArrayList<>();
        for (Integer id : ids) {
            titles.add(getTopicTitle(id));
        }

        return titles;
    }

    public String getTopicTitle(int topicID) {

        if (topicTitles == null) {
            return "";
        } else {
            if (topicTitles.containsKey(topicID)) {
                String title = topicTitles.get(topicID).get(0);

                return title.substring(0, 1).toUpperCase() + title.substring(1);
            } else {
                return "Unknown title";
            }
        }
    }
}
