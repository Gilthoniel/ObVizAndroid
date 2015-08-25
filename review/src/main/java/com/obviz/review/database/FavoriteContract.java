package com.obviz.review.database;

import android.provider.BaseColumns;

/**
 * Created by gaylor on 08/24/2015.
 * Helper class to create a Database Table for the favorite list
 */
public class FavoriteContract {

    private FavoriteContract() {}

    public static abstract class FavoriteEntry implements BaseColumns {

        public static final String TABLE_NAME = "favorite";
        public static final String COLUMN_APP_ID = "app_id";
        public static final String COLUMN_APP_NAME = "app_name";
        public static final String COLUMN_APP_LOGO = "app_logo";

        public static final String SQL_CREATE = "create table " + TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY," +
                COLUMN_APP_ID + " TEXT UNIQUE," +
                COLUMN_APP_LOGO + " TEXT," +
                COLUMN_APP_NAME + " TEXT" +
                ");";

        public static final String SQL_DELETE = "drop table if exists " + TABLE_NAME;
    }
}
