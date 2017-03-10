package com.jayodeji.android.popularmovies.dbcontract;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by joshuaadeyemi on 2/26/17.
 */

public class MovieContract {

    public static final String CONTENT_AUTHORITY = "com.jayodeji.android.popularmovies";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_FAVORITES = "favorites";
    public static final String PATH_TRAILERS = "trailers";
    public static final String PATH_REVIEWS = "reviews";


    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_FAVORITES)
                .build();

        public static final String TABLE_NAME = "movie";

        public static final String COLUMN_EXTERNAL_MOVIE_ID = "external_movie_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_POSTER_URL = "poster_url";
        public static final String COLUMN_THUMBNAIL_URL = "thumbnail_url";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_RELEASE_YEAR = "release_year";
        public static final String COLUMN_RUNTIME = "runtime";
        public static final String COLUMN_TIMESTAMP = "timestamp";

        public static String getTableCreationSql() {
            String sql = "CREATE TABLE " + TABLE_NAME + " (" +
                    _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_EXTERNAL_MOVIE_ID + " INTEGER NOT NULL, " +
                    COLUMN_TITLE + " VARCHAR(255) NOT NULL, " +
                    COLUMN_POSTER_URL + " VARCHAR(255) NOT NULL, " +
                    COLUMN_THUMBNAIL_URL + " VARCHAR(255) NOT NULL, " +
                    COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                    COLUMN_RATING + " REAL NOT NULL, " +
                    COLUMN_RELEASE_YEAR + " INTEGER NOT NULL, " +
                    COLUMN_RUNTIME + " INTEGER NOT NULL, " +
                    COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, " +

                    "UNIQUE (" + COLUMN_EXTERNAL_MOVIE_ID + ") ON CONFLICT REPLACE);";

            return sql;
        }

        public static String getTableDeletionSql() {
            return "DROP TABLE IF EXISTS " + TABLE_NAME;
        }

    }

    public static final class TrailerEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_TRAILERS)
                .build();

        public static final String TABLE_NAME = "trailer";

        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_KEY = "key";
        public static final String COLUMN_URL = "url";
        public static final String COLUMN_EXTERNAL_MOVIE_ID = "external_movie_id";
        public static final String COLUMN_TIMESTAMP = "timestamp";

        public static String getTableCreationSql() {
            String sql = "CREATE TABLE " + TABLE_NAME + " (" +
                    _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NAME + " VARCHAR(255) NOT NULL, " +
                    COLUMN_KEY + " VARCHAR(255) NOT NULL, " +
                    COLUMN_URL + " VARCHAR(255) NOT NULL, " +
                    COLUMN_EXTERNAL_MOVIE_ID + " INTEGER NOT NULL, " +
                    COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, " +
                    "FOREIGN KEY( " + COLUMN_EXTERNAL_MOVIE_ID + " ) REFERENCES " +
                    MovieEntry.TABLE_NAME + "(" + MovieEntry.COLUMN_EXTERNAL_MOVIE_ID + "), " +

                    "UNIQUE (" + COLUMN_KEY + ") ON CONFLICT IGNORE);";

            return sql;
        }

        public static String getTableDeletionSql() {
            return "DROP TABLE IF EXISTS " + TABLE_NAME;
        }

    }

    public static final class ReviewEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_REVIEWS)
                .build();

        public static final String TABLE_NAME = "review";

        public static final String COLUMN_REVIEW_ID = "review";
        public static final String COLUMN_AUTHOR = "author";
        public static final String COLUMN_URL = "url";
        public static final String COLUMN_EXTERNAL_MOVIE_ID = "external_movie_id";
        public static final String COLUMN_TIMESTAMP = "timestamp";

        public static String getTableCreationSql() {
            String sql = "CREATE TABLE " + TABLE_NAME + " (" +
                    _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_REVIEW_ID + " VARCHAR(255) NOT NULL, " +
                    COLUMN_AUTHOR + " VARCHAR(255) NOT NULL, " +
                    COLUMN_URL + " VARCHAR(255) NOT NULL, " +
                    COLUMN_EXTERNAL_MOVIE_ID + " INTEGER NOT NULL, " +
                    COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, " +
                    "FOREIGN KEY( " + COLUMN_EXTERNAL_MOVIE_ID + " ) REFERENCES " +
                    MovieEntry.TABLE_NAME + "( " + MovieEntry.COLUMN_EXTERNAL_MOVIE_ID + " ), " +

                    "UNIQUE (" + COLUMN_REVIEW_ID + ") ON CONFLICT IGNORE);";

            return sql;
        }

        public static String getTableDeletionSql() {
            return "DROP TABLE IF EXISTS " + TABLE_NAME;
        }

    }
}