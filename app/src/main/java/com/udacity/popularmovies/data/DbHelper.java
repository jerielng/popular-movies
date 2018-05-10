package com.udacity.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_FILE_NAME = "favorites.db";
    public static final int VERSION_NUMBER = 1;
    public static final String TABLE_NAME = "favorites";

    public DbHelper(Context context) {
        super(context, DATABASE_FILE_NAME, null, VERSION_NUMBER);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_FAVORITES_TABLE =
                "CREATE TABLE " + TABLE_NAME
                + " ("
                + FavoritesProvider.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + FavoritesProvider.COLUMN_MOVIE_ID + " INTEGER NOT NULL, "
                + FavoritesProvider.COLUMN_TITLE + " TEXT NOT NULL, "
                + FavoritesProvider.COLUMN_DESCRIPTION + " TEXT, "
                + FavoritesProvider.COLUMN_POSTER + " TEXT, "
                + FavoritesProvider.COLUMN_RATING + " REAL, "
                + FavoritesProvider.COLUMN_RELEASE_DATE + " TEXT);";
        db.execSQL(SQL_CREATE_FAVORITES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
