package com.obviz.review.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import com.obviz.review.database.HistoryContract.HistoryEntry;
import com.obviz.review.database.FavoriteContract.FavoriteEntry;
import com.obviz.review.models.AndroidApp;

import java.util.Date;

/**
 * Created by gaylor on 8/3/2015.
 *
 */
public class DatabaseService {

    private static DatabaseService instance;

    private DatabaseHelper helper;

    private DatabaseService() {}
    
    public static void init(Context context) {

        instance = new DatabaseService();

        instance.initHelper(context);
    }

    public static DatabaseService instance() {

        return instance;
    }

    public void initHelper(Context context) {
        helper = new DatabaseHelper(context);
    }

    /* INSERTION */

    public long insertHistoryEntry(String query) {
        if (helper == null) {
            return 0;
        }

        SQLiteDatabase db = helper.getWritableDatabase();

        // Try to find a same query
        String[] projection = new String[] { HistoryEntry.COLUMN_QUERY };
        String[] args = new String[] { query };

        Cursor cursor = db.query(HistoryEntry.TABLE_NAME, projection, projection[0] + " like ?", args, null, null, null);
        try {

            if (cursor.getCount() > 0) {
                ContentValues values = new ContentValues();
                values.put(HistoryEntry.COLUMN_DATE, new Date().getTime());

                return db.update(HistoryEntry.TABLE_NAME, values, projection[0] + " like ?", args);
            } else {
                ContentValues values = new ContentValues();
                values.put(HistoryEntry.COLUMN_QUERY, query);
                values.put(HistoryEntry.COLUMN_DATE, new Date().getTime());

                return db.insert(HistoryEntry.TABLE_NAME, null, values);
            }
        } finally {

            cursor.close();
        }
    }

    public long insertFavorite(AndroidApp app) {
        if (helper == null) {
            return 0;
        }

        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FavoriteEntry.COLUMN_APP_ID, app.getAppID());
        values.put(FavoriteEntry.COLUMN_APP_NAME, app.getName());

        try {

            return db.insertOrThrow(FavoriteEntry.TABLE_NAME, null, values);
        } catch (SQLiteException e) {

            return -1;
        }
    }

    /* SELECTION */

    public Cursor selectHistory() {
        if (helper == null) {
            return null;
        }

        SQLiteDatabase db = helper.getReadableDatabase();

        String[] projection = new String[] {
                HistoryEntry.COLUMN_QUERY,
                HistoryEntry.COLUMN_DATE
        };

        return db.query(HistoryEntry.TABLE_NAME, projection, null, null, null, null, HistoryEntry.COLUMN_DATE + " DESC");
    }

    public boolean isInFavorite(String appID) {
        if (helper == null) {
            return false;
        }

        SQLiteDatabase db = helper.getReadableDatabase();

        String[] projection = new String[] { FavoriteEntry.COLUMN_APP_ID };
        String selection = FavoriteEntry.COLUMN_APP_ID + " = '" + appID + "'";
        Cursor cursor = db.query(FavoriteEntry.TABLE_NAME, projection, selection, null, null, null, null);

        try {

            return cursor.getCount() > 0;
        } finally {

            cursor.close();
        }
    }

    /* DELETIONS */

    public void dropHistory() {
        if (helper == null) {
            return;
        }

        SQLiteDatabase db = helper.getWritableDatabase();

        db.delete(HistoryEntry.TABLE_NAME, null, null);
    }

    public long removeFavorite(String appID) {
        if (helper == null) {
            return 0;
        }

        SQLiteDatabase db = helper.getWritableDatabase();

        return db.delete(FavoriteEntry.TABLE_NAME, FavoriteEntry.COLUMN_APP_ID + " = '" + appID + "'", null);
    }
}
