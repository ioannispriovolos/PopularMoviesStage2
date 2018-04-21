package com.example.android.popularmoviesstage2;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

// https://developer.android.com/guide/topics/providers/content-provider-creating.html
public class Provider extends ContentProvider {

    private DatabaseHelper dbHelper;

    public static final int FAVORITES = 100;
    public static final int FAVORITES_ID = 101;

    private static UriMatcher matcher;

    static {
        matcher = new UriMatcher(UriMatcher.NO_MATCH);

        matcher.addURI(DatabasePoster.CONTENT_AUTHORITY, DatabasePoster.PATH_FAVORITES, FAVORITES);

        matcher.addURI(DatabasePoster.CONTENT_AUTHORITY, DatabasePoster.PATH_FAVORITES + "/#", FAVORITES_ID);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Nullable @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        Cursor cursor;

        switch(matcher.match(uri)) {

            case FAVORITES:

                cursor = dbHelper.getReadableDatabase().query(DatabasePoster.MovieFavoriteEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case FAVORITES_ID:

                selectionArgs = new String[]{uri.getLastPathSegment()};

                cursor = dbHelper.getReadableDatabase().query(DatabasePoster.MovieFavoriteEntry.TABLE_NAME, projection, DatabasePoster.MovieFavoriteEntry.COLUMN_MOVIEID + " = ? ", selectionArgs, null, null, sortOrder);
                break;

            default:
                throw new UnsupportedOperationException("URI error query: " + uri);

        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {

        switch(matcher.match(uri)) {

            case FAVORITES:

                long rowId = dbHelper.getWritableDatabase().insert(DatabasePoster.MovieFavoriteEntry.TABLE_NAME,
                        null,
                        values);

                if(rowId != -1) {
                    Uri insertedUri = DatabasePoster.MovieFavoriteEntry.CONTENT_URI.buildUpon().appendPath(values.get(DatabasePoster.MovieFavoriteEntry.COLUMN_MOVIEID).toString()).build();

                    getContext().getContentResolver().notifyChange(insertedUri, null);

                    return insertedUri;
                }
                break;

            default:
                throw new IllegalArgumentException("URI error insert: " + uri);
        }

        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {

        int numRowsDeleted;

        switch(matcher.match(uri)) {

            case FAVORITES_ID:

                selectionArgs = new String[]{uri.getLastPathSegment()};

                numRowsDeleted = dbHelper.getWritableDatabase().delete(DatabasePoster.MovieFavoriteEntry.TABLE_NAME, DatabasePoster.MovieFavoriteEntry.COLUMN_MOVIEID + " = ? ", selectionArgs);
                break;

            default:
                throw new UnsupportedOperationException("URI error delete: " + uri);
        }

        if (numRowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return numRowsDeleted;
    }

    @Nullable @Override
    public String getType(@NonNull Uri uri) {
        throw new RuntimeException("getType error");
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        throw new RuntimeException("update error");
    }
}