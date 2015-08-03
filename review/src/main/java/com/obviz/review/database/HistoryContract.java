package com.obviz.review.database;

import android.provider.BaseColumns;

/**
 * Created by gaylor on 8/3/2015.
 * Database entries for the search history
 */
public class HistoryContract {

    public HistoryContract() {}

    public static abstract class HistoryEntry implements BaseColumns {

        public static final String TABLE_NAME = "history";
        public static final String COLUMN_QUERY = "query";
        public static final String COLUMN_DATE = "date";

        public static final String SQL_CREATE = "create table " + TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY," +
                COLUMN_QUERY + " TEXT," +
                COLUMN_DATE + " NUMBER" +
                ");";

        public static final String SQL_DELETE = "drop table if exists " + TABLE_NAME;
    }
}
