package com.obviz.review;

/**
 * Created by gaylor on 21.07.15.
 *
 */
public class Constants {

    /** SETTINGS **/

    public static final int CACHE_EXPIRATION_TIME = 60 * 60 * 1000; // in ms
    public static final int NUMBER_TRENDING_APPS = 10;

    /** Commands **/
    public static final String URL = "http://vps186042.ovh.net/ObVizService";

    public static final String GET_APP = "Get_App";
    public static final String SEARCH_APP = "Search_Apps";
    public static final String GET_TOPIC_TITLES = "Get_App_Topics";
    public static final String GET_REVIEWS = "Get_Reviews";
    public static final String GET_TRENDING_APPS = "Get_Trending_Apps";

    /** INTENTS **/

    public static final String INTENT_APP_ID = "com.obviz.review.INTENT_APP_ID";
    public static final String INTENT_APP = "com.obviz.review.INTENT_APP";
    public static final String INTENT_TOPIC_ID = "com.obviz.review.INTENT_TOPIC_ID";
    public static final String INTENT_COMPARISON_APP = "com.obviz.review.INTENT_COMPARISON_APP";
    public static final String INTENT_COMPARISON_APP_ID = "com.obviz.review.INTENT_COMPARISON_APP_ID";
    public static final String INTENT_SEARCH = "com.obviz.review.INTENT_SEARCH";

    /** Categories **/

    /* Category */

    public enum Category {
        SOCIAL("social", "Social"),
        GAME_ADVENTURE("games", "Adventure"),
        GAME_SPORTS("games", "Sport"),
        GAME_STRATEGY("games", "Strategy"),
        GAME_ACTION("games", "Action"),
        GAME_PUZZLE("games", "Puzzle"),
        GAME_ARCADE("games", "Arcade"),
        GAME_CARD("games", "Card"),
        GAME_CASUAL("games", "Casual"),
        GAME_CASINO("games", "Casino"),
        GAME_TRIVIA("games", "Trivia"),
        GAME_SIMULATION("games", "Simulation"),
        GAME_RACING("games", "Racing"),
        GAME_ROLE_PLAYING("games", "RPG"),
        GAME_WORD("games", "Word"),
        GAME_BOARD("games", "Board"),
        GAME_EDUCATIONAL("games", "Educational"),
        GAME_MUSIC("games", "Music"),
        COMMUNICATION("communication", "Communication"),
        MUSIC_AND_AUDIO("audio", "Music and Audio"),
        ENTERTAINMENT("entertainment", "Entertainment"),
        TOOLS("tools", "Tools"),
        BOOKS_AND_REFERENCE("books", "Books"),
        PERSONALIZATION("personalization", "Personalization"),
        PRODUCTIVITY("productivity", "Productivity"),
        WEATHER("weather", "Weather"),
        SHOPPING("shopping", "Shopping"),
        TRANSPORTATION("transportation", "Transportation"),
        LIFESTYLE("lifestyle", "Lifestyle"),
        TRAVEL_AND_LOCAL("travel", "Travel and Local"),
        PHOTOGRAPHY("photo", "Photography"),
        BUSINESS("business", "Business"),
        HEALTH_AND_FITNESS("health", "Health"),
        FINANCE("finance", "Finance"),
        SPORTS("sports", "Sports"),
        EDUCATION("education", "Education"),
        NEWS_AND_MAGAZINES("news", "News and Magazines"),
        MEDICAL("medical", "Medical"),
        COMICS("comics", "Comics"),
        DEFAULT("default", "Application");

        private final String name;
        private final String title;
        Category(String value, String title) {
            name = value;
            this.title = title;
        }

        public String getName() {
            return name;
        }

        public String getTitle() {
            return title;
        }

        public static Category fromName(String name) {
            for (Category category : values()) {
                if (category.name().equals(name)) {
                    return category;
                }
            }

            return DEFAULT;
        }

        @Override
        public String toString() {
            return title;
        }
    }
}
