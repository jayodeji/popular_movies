package com.jayodeji.android.popularmovies.providers;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import com.jayodeji.android.popularmovies.dbcontract.MovieContract;
import com.jayodeji.android.popularmovies.dbcontract.MovieDbHelper;

import java.util.List;

/**
 * Created by joshuaadeyemi on 2/26/17.
 */

public class FavoriteMovieProvider extends ContentProvider {

    private static final String TAG = FavoriteMovieProvider.class.getSimpleName();

    public static final int CODE_MOVIE = 100;
    public static final int CODE_MOVIE_TRAILERS = 102;
    public static final int CODE_MOVIE_REVIEWS = 104;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDbHelper mMovieDbHelper;


    public static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        /** This URI is content://com.jayodeji.android.popularmovies/favorites/ */
        matcher.addURI(authority, MovieContract.PATH_FAVORITES, CODE_MOVIE);

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
                cursor = makeSelectionQuery(MovieContract.MovieEntry.TABLE_NAME, projection, null, null, sortOrder);
                break;
            case CODE_MOVIE_TRAILERS:
                movieId = uri.getQueryParameter(MovieContract.TrailerEntry.COLUMN_EXTERNAL_MOVIE_ID);
                if (movieId != null) {
                    selectionArgs = new String[]{movieId};
                    selection = MovieContract.TrailerEntry.COLUMN_EXTERNAL_MOVIE_ID + " = ? ";
                }
                cursor = makeSelectionQuery(MovieContract.TrailerEntry.TABLE_NAME, projection, selection, selectionArgs, sortOrder);
                break;
            case CODE_MOVIE_REVIEWS:
                movieId = uri.getQueryParameter(MovieContract.ReviewEntry.COLUMN_EXTERNAL_MOVIE_ID);
                if (movieId != null) {
                    selectionArgs = new String[]{movieId};
                    selection = MovieContract.ReviewEntry.COLUMN_EXTERNAL_MOVIE_ID + " = ? ";
                }
                cursor = makeSelectionQuery(MovieContract.ReviewEntry.TABLE_NAME, projection, selection, selectionArgs, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    private Cursor makeSelectionQuery(String tableName, String[] columns, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = mMovieDbHelper.getReadableDatabase();
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

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        throw new RuntimeException("'insert' is not implemented here. Use 'bulkInsert' instead.");
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        throw new RuntimeException("'delete' is not implemented yet.");
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        throw new RuntimeException("'update' is not implemented here. Delete and insert instead.");
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        throw new RuntimeException("We are not implementing 'getType' in this app.");
    }
}
