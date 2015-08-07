package com.obviz.review.managers;

import com.obviz.review.Constants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by gaylor on 08/07/2015.
 * Manage the different categories
 */
public class CategoryManager {

    public static final CategoryManager instance = new CategoryManager();

    private List<SuperCategory> supers;

    private CategoryManager() {

        supers = new ArrayList<>();

        SuperCategory all = new SuperCategory("All");
        supers.add(all);

        /** Games **/
        SuperCategory games = new SuperCategory("Games");

        games.addAll(new Constants.Category[] {
                Constants.Category.GAME_ACTION,
                Constants.Category.GAME_ADVENTURE,
                Constants.Category.GAME_ARCADE,
                Constants.Category.GAME_BOARD,
                Constants.Category.GAME_CARD,
                Constants.Category.GAME_CASINO,
                Constants.Category.GAME_CASUAL,
                Constants.Category.GAME_EDUCATIONAL,
                Constants.Category.GAME_MUSIC,
                Constants.Category.GAME_PUZZLE,
                Constants.Category.GAME_RACING,
                Constants.Category.GAME_ROLE_PLAYING,
                Constants.Category.GAME_SIMULATION,
                Constants.Category.GAME_SPORTS,
                Constants.Category.GAME_STRATEGY,
                Constants.Category.GAME_TRIVIA,
                Constants.Category.GAME_WORD
        });
        supers.add(games);

        /** Social **/
        SuperCategory social = new SuperCategory("Social");

        social.addAll(new Constants.Category[]{
                Constants.Category.COMMUNICATION,
                Constants.Category.SOCIAL
        });
        supers.add(social);

        /** Entertainment **/
        SuperCategory entertainment = new SuperCategory("Entertainment");

        entertainment.addAll(new Constants.Category[] {
                Constants.Category.MUSIC_AND_AUDIO,
                Constants.Category.ENTERTAINMENT,
                Constants.Category.COMICS,
                Constants.Category.BOOKS_AND_REFERENCE,
                Constants.Category.PHOTOGRAPHY,
                Constants.Category.SHOPPING
        });
        supers.add(entertainment);

        /** Tools **/
        SuperCategory tools = new SuperCategory("Tools");

        tools.addAll(new Constants.Category[]{
                Constants.Category.TOOLS,
                Constants.Category.PERSONALIZATION
        });
        supers.add(tools);

        /** WORK **/
        SuperCategory work = new SuperCategory("Work");

        work.addAll(new Constants.Category[] {
                Constants.Category.BUSINESS,
                Constants.Category.EDUCATION,
                Constants.Category.FINANCE,
                Constants.Category.PRODUCTIVITY,
                Constants.Category.TRANSPORTATION,
                Constants.Category.TRAVEL_AND_LOCAL
        });
        supers.add(work);

        /** HEALTH **/
        SuperCategory health = new SuperCategory("Health");

        health.addAll(new Constants.Category[] {
                Constants.Category.HEALTH_AND_FITNESS,
                Constants.Category.SPORTS,
                Constants.Category.MEDICAL
        });
        supers.add(health);

        /** Others **/
        SuperCategory others = new SuperCategory("Others");
        others.addAll(new Constants.Category[] {
                Constants.Category.NEWS_AND_MAGAZINES,
                Constants.Category.WEATHER,
                Constants.Category.LIFESTYLE
        });
        supers.add(others);
    }

    public List<SuperCategory> getSupers() {

        return supers;
    }

    public class SuperCategory {

        private List<Constants.Category> mCategories;
        private String mName;

        public SuperCategory(String name) {

            mName = name;
            mCategories = new ArrayList<>();
        }

        public String getName() {

            return mName;
        }

        public List<Constants.Category> getCategories() {

            return mCategories;
        }

        public void add(Constants.Category category) {
            mCategories.add(category);
        }

        public void addAll(Constants.Category[] collection) {

            for (Constants.Category category : collection) {
                add(category);
            }
        }

        @Override
        public String toString() {

            return mName;
        }
    }
}
