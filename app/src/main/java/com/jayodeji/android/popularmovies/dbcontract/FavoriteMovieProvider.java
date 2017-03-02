package com.jayodeji.android.popularmovies.dbcontract;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by joshuaadeyemi on 2/26/17.
 */

public class FavoriteMovieProvider extends ContentProvider {

    public static final int CODE_MOVIE = 100;
    public static final int CODE_MOVIE_DETAIL = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDbHelper mMovieDbHelper;


    public static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        /** This URI is content://com.jayodeji.android.popularmovies/favorites/ */
        matcher.addURI(authority, MovieContract.PATH_FAVORITES, CODE_MOVIE);

        /** This URI is content://com.jayodeji.android.popularmovies/favorites/{anynumber} */
        matcher.addURI(authority, MovieContract.PATH_FAVORITES + "/#", CODE_MOVIE_DETAIL);

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
        switch (sUriMatcher.match(uri)) {

        }
        return null;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        return super.bulkInsert(uri, values);
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        throw new RuntimeException("'insert' is not implemented here. Use 'bulkInsert' instead.");
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int numRowsDeleted = 0;

        //allows deletion of entire table with number of rows deleted returned
        if (selection == null) {
            selection = "1";
        }

        switch (sUriMatcher.match(uri)) {

            case CODE_MOVIE:
                numRowsDeleted = mMovieDbHelper.getWritableDatabase().delete(
                        MovieContract.MovieEntry.TABLE_NAME,
                        selection,
                        selectionArgs
                );
                break;

            case CODE_MOVIE_DETAIL:
                //find a way to delete single movies
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (numRowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numRowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        throw new RuntimeException("'update' is not implemented here. Use 'bulkInsert' instead.");
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        throw new RuntimeException("We are not implementing 'getType' in this app.");
    }
}
