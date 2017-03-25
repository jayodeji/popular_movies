package com.jayodeji.android.popularmovies.providers;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.jayodeji.android.popularmovies.dbcontract.MovieContract;
import com.jayodeji.android.popularmovies.dbcontract.MovieDbHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joshuaadeyemi on 2/26/17.
 */

public class FavoriteMovieProvider extends ContentProvider {

    private static final String TAG = FavoriteMovieProvider.class.getSimpleName();

    public static final int CODE_MOVIE = 100;
    public static final int CODE_MOVIE_DETAIL = 101;
    public static final int CODE_MOVIE_TRAILERS = 102;
    public static final int CODE_MOVIE_REVIEWS = 104;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDbHelper mMovieDbHelper;


    public static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        /** This URI is content://com.jayodeji.android.popularmovies/favorites/ */
        matcher.addURI(authority, MovieContract.PATH_FAVORITES, CODE_MOVIE);

        /** This URI is content://com.jayodeji.android.popularmovies/favorties/# */
        String detailPath = MovieContract.PATH_FAVORITES + "/#";
        matcher.addURI(authority, detailPath, CODE_MOVIE_DETAIL);

        /** This URI is content://com.jayodeji.android.popularmovies/trailers/ */
        matcher.addURI(authority, MovieContract.PATH_TRAILERS, CODE_MOVIE_TRAILERS);

        /** This URI is content://com.jayodeji.android.popularmovies/reviews/ */
        matcher.addURI(authority, MovieContract.PATH_REVIEWS, CODE_MOVIE_REVIEWS);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mMovieDbHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor;
        String movieId;
        switch (sUriMatcher.match(uri)) {
            case CODE_MOVIE:
                projection = new String[]{
                        MovieContract.MovieEntry._ID,
                        MovieContract.MovieEntry.COLUMN_POSTER_URL,
                        MovieContract.MovieEntry.COLUMN_TITLE,
                        MovieContract.MovieEntry.COLUMN_EXTERNAL_MOVIE_ID
                };
                if (sortOrder == null) {
                    sortOrder = MovieContract.MovieEntry._ID + " DESC";
                }

                cursor = makeSelectionQuery(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        null,
                        null,
                        sortOrder
                );
                break;
            case CODE_MOVIE_DETAIL:
                cursor = makeSelectionQuery(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        MovieContract.MovieEntry.COLUMN_EXTERNAL_MOVIE_ID + " = ? ",
                        new String[]{uri.getLastPathSegment()},
                        null
                );
                break;
            case CODE_MOVIE_TRAILERS:
                movieId = uri.getQueryParameter(MovieContract.TrailerEntry.COLUMN_EXTERNAL_MOVIE_ID);
                if (movieId != null) {
                    selectionArgs = new String[]{movieId};
                    selection = MovieContract.TrailerEntry.COLUMN_EXTERNAL_MOVIE_ID + " = ? ";
                }
                cursor = makeSelectionQuery(
                        MovieContract.TrailerEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        sortOrder
                );
                break;
            case CODE_MOVIE_REVIEWS:
                movieId = uri.getQueryParameter(MovieContract.ReviewEntry.COLUMN_EXTERNAL_MOVIE_ID);
                if (movieId != null) {
                    selectionArgs = new String[]{movieId};
                    selection = MovieContract.ReviewEntry.COLUMN_EXTERNAL_MOVIE_ID + " = ? ";
                }
                cursor = makeSelectionQuery(
                        MovieContract.ReviewEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        sortOrder
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    private Cursor makeSelectionQuery(String tableName, String[] columns, String selection, String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase db = mMovieDbHelper.getReadableDatabase();
        return db.query(
                tableName,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        final SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();
        Uri returnUri;

        switch (sUriMatcher.match(uri)) {
            case CODE_MOVIE:
                long insertId = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);
                if (insertId > 0) {
                    returnUri = ContentUris.withAppendedId(MovieContract.MovieEntry.CONTENT_URI, insertId);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        int rowsInserted = 0;
        switch (sUriMatcher.match(uri)) {
            case CODE_MOVIE_TRAILERS:
                rowsInserted = insertMultipleRecords(MovieContract.TrailerEntry.TABLE_NAME, values);
                break;
            case CODE_MOVIE_REVIEWS:
                rowsInserted = insertMultipleRecords(MovieContract.ReviewEntry.TABLE_NAME, values);
                break;
            default:
                return super.bulkInsert(uri, values);
        }
        if (rowsInserted > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsInserted;
    }

    private int insertMultipleRecords(String tableName, ContentValues[] records) {
        final SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();
        int rowsInserted = 0;
        db.beginTransaction();
        try {
            for (ContentValues value : records) {
                long _id = db.insert(tableName, null, value);
                if (_id != -1) {
                    rowsInserted++;
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        return rowsInserted;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();
        int movieDeleted = 0;
        switch (sUriMatcher.match(uri)) {
            case CODE_MOVIE_DETAIL:
                String movieId = uri.getLastPathSegment();
                db.beginTransaction();
                try {
                    //delete movie
                    movieDeleted = db.delete(
                            MovieContract.MovieEntry.TABLE_NAME,
                            MovieContract.MovieEntry.COLUMN_EXTERNAL_MOVIE_ID + " = ? ",
                            new String[]{movieId}
                    );
                    //delete trailers
                    db.delete(
                            MovieContract.TrailerEntry.TABLE_NAME,
                            MovieContract.TrailerEntry.COLUMN_EXTERNAL_MOVIE_ID + " = ?",
                            new String[]{movieId}
                    );
                    //delete reviews
                    db.delete(
                            MovieContract.ReviewEntry.TABLE_NAME,
                            MovieContract.ReviewEntry.COLUMN_EXTERNAL_MOVIE_ID + " = ?",
                            new String[]{movieId}
                    );
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (movieDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return movieDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        throw new RuntimeException("'update' is not implemented here.");
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }
}
