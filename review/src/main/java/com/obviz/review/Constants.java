package com.obviz.review;

import com.obviz.review.views.GaugeChart;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by gaylor on 21.07.15.
 *
 */
public class Constants {

    /** SETTINGS **/

    public static final int CACHE_EXPIRATION_TIME = 1000 * 60 * 60 * 24; // in ms
    public static final int NUMBER_TRENDING_APPS = 10;
    public static final int NUMBER_REVIEW_PER_BLOCK = 20;

    /** Commands **/
    public static final String URL = "http://vps186042.ovh.net/ObVizService";

    public static final String GET_APP = "Get_App";
    public static final String SEARCH_APP = "Search_Apps";
    public static final String GET_TOPIC_TITLES = "Get_App_Topics";
    public static final String GET_REVIEWS = "Get_Reviews";
    public static final String GET_TRENDING_APPS = "Get_Trending_Apps";
    public static final String GET_CATEGORIES = "Get_App_Categories";
    public static final String GET_CATEGORIES_TYPES = "Get_App_Categories_Types";

    public static final String APP_VIEWED = "App_Viewed";

    public static final int TIMEOUT = 20;

    /** INTENTS **/

    public static final String INTENT_APP_ID = "com.obviz.review.INTENT_APP_ID";
    public static final String INTENT_APP = "com.obviz.review.INTENT_APP";
    public static final String INTENT_LIST_APP = "com.obviz.review.INTENT_LIST_APP";
    public static final String INTENT_TOPIC_ID = "com.obviz.review.INTENT_TOPIC_ID";
    public static final String INTENT_COMPARISON_APP = "com.obviz.review.INTENT_COMPARISON_APP";
    public static final String INTENT_COMPARISON_APP_ID = "com.obviz.review.INTENT_COMPARISON_APP_ID";
    public static final String INTENT_SEARCH = "com.obviz.review.INTENT_SEARCH";

    /** STATES **/

    public static final String STATE_APP = "com.obviz.review.APP";
    public static final String STATE_TOPIC = "com.obviz.review.TOPIC";
    public static final String STATE_SEARCH = "com.obviz.review.SEARCH";
    public static final String STATE_COMPARISON = "com.obviz.reviewe.COMPARISON";

    /** Gauge **/
    public static final List<GaugeChart.Segment> CHART_SEGMENTS = new LinkedList<>();

    public static final int POSITIVE_COLOR = 0xff57B05E;
    public static final int NEGATIVE_COLOR = 0xffCC6B6B;

    /** Tutorial **/
    public static final String KEY_HOME = "TUTORIAL_HOME";
    public static final String KEY_DETAILS = "TUTORIAL_DETAILS";
}
