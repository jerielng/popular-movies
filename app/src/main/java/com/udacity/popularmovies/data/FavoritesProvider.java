package com.udacity.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class FavoritesProvider extends ContentProvider {

    private static final String AUTHORITY = "com.udacity.popularmovies";
    private static final String PREFIX = "content://";
    private static final Uri URI_BASE = Uri.parse(PREFIX + AUTHORITY);

    private static final String TABLE_NAME = "favorites";

    private DbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new DbHelper(this.getContext());
        return true;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        return null;
    }
}
