package com.obviz.review.models;

import android.database.Cursor;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by gaylor on 8/3/2015.
 * Searching history entry
 */
public class HistoryItem {

    private static final SimpleDateFormat formatter = new SimpleDateFormat("d MMM yyyy, hh:mm", Locale.getAvailableLocales()[0]);

    private String query;
    private java.util.Date date;

    /**
     * Constructor for database query
     * @param cursor Assume that the cursor is already moved
     */
    public HistoryItem(Cursor cursor) {
        query = cursor.getString(0);
        date = new java.util.Date(cursor.getLong(1));
    }

    public String getQuery() {
        return query;
    }

    public String getDate() {
        return formatter.format(date);
    }
}
