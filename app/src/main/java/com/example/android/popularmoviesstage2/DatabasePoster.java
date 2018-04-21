package com.example.android.popularmoviesstage2;

import android.net.Uri;
import android.provider.BaseColumns;

public class DatabasePoster {

    public static final String CONTENT_AUTHORITY = "com.example.android.popularmoviesstage2";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_FAVORITES = "favorites";

    public static final class MovieFavoriteEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITES).build();

        public static final String TABLE_NAME = "favorites";

        public static final String COLUMN_MOVIEID = "movie_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RELEASEDATE = "release_date";
        public static final String COLUMN_RATING = "vote_average";
        public static final String COLUMN_POSTER = "encoded_poster";
        public static final String COLUMN_POSTERPATH = "poster_path";
    }
}
