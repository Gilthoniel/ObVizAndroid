package com.obviz.review.database;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;

import android.net.Uri;
import android.os.Bundle;
import com.obviz.review.database.HistoryContract.HistoryEntry;

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
        if (cursor.getCount() > 0) {
            cursor.close();

            ContentValues values = new ContentValues();
            values.put(HistoryEntry.COLUMN_DATE, new Date().getTime());

            return db.update(HistoryEntry.TABLE_NAME, values, projection[0] + " like ?", args);
        } else {
            cursor.close();

            ContentValues values = new ContentValues();
            values.put(HistoryEntry.COLUMN_QUERY, query);
            values.put(HistoryEntry.COLUMN_DATE, new Date().getTime());

            return db.insert(HistoryEntry.TABLE_NAME, null, values);
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

    /* DELETIONS */

    public void dropHistory() {
        if (helper == null) {
            return;
        }

        SQLiteDatabase db = helper.getWritableDatabase();

        db.delete(HistoryEntry.TABLE_NAME, null, null);
    }
}
