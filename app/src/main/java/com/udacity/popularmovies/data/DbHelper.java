package com.udacity.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_FILE_NAME = "favorites.db";
    private static final int VERSION_NUMBER = 1;

    public DbHelper(Context context) {
        super(context, DATABASE_FILE_NAME, null, VERSION_NUMBER);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_FAVORITES_TABLE =
                "";
        db.execSQL(SQL_CREATE_FAVORITES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
