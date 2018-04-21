package com.example.android.popularmoviesstage2;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
// https://developer.android.com/reference/android/database/sqlite/SQLiteOpenHelper.html
// https://github.com/udacity/Sunshine-Version-2/blob/sunshine_master/app/src/main/java/com/example/android/sunshine/app/data/WeatherDbHelper.java
// https://stackoverflow.com/questions/7899720/what-is-the-use-of-basecolumns-in-android
public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "movies.db";
    private static final int DATABASE_VERSION = 2;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_FAVORITE_TABLE =

                "CREATE TABLE " + DatabasePoster.MovieFavoriteEntry.TABLE_NAME + " (" +

                        DatabasePoster.MovieFavoriteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        DatabasePoster.MovieFavoriteEntry.COLUMN_MOVIEID + " INTEGER NOT NULL, " +
                        DatabasePoster.MovieFavoriteEntry.COLUMN_TITLE + " TEXT, " +
                        DatabasePoster.MovieFavoriteEntry.COLUMN_OVERVIEW + " TEXT, " +
                        DatabasePoster.MovieFavoriteEntry.COLUMN_RELEASEDATE + " TEXT, " +
                        DatabasePoster.MovieFavoriteEntry.COLUMN_POSTERPATH + " TEXT, " +
                        DatabasePoster.MovieFavoriteEntry.COLUMN_RATING + " REAL, " +
                        DatabasePoster.MovieFavoriteEntry.COLUMN_POSTER + " BLOB, " +

                        " UNIQUE (" + DatabasePoster.MovieFavoriteEntry.COLUMN_MOVIEID +") ON CONFLICT REPLACE);";

        db.execSQL(SQL_CREATE_FAVORITE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DatabasePoster.MovieFavoriteEntry.TABLE_NAME);
        onCreate(db);
    }
}
