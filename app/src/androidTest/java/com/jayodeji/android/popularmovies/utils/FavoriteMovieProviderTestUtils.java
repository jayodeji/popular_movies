package com.jayodeji.android.popularmovies.utils;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.jayodeji.android.popularmovies.TestFavoriteMovieProvider;
import com.jayodeji.android.popularmovies.dbcontract.MovieContract;

/**
 * Created by joshuaadeyemi on 3/10/17.
 */

public class FavoriteMovieProviderTestUtils {

    public static void clearAllRecords(SQLiteDatabase db) {
        db.delete(MovieContract.MovieEntry.TABLE_NAME, null, null);
        db.delete(MovieContract.ReviewEntry.TABLE_NAME, null, null);
        db.delete(MovieContract.TrailerEntry.TABLE_NAME, null, null);
    }


    public static void validateMovieSavedWithTrailersAndReviews(SQLiteDatabase db, MovieTestInfo movieInfo) {
        ContentValues movie = movieInfo.movie;
        ContentValues[] trailers = movieInfo.trailers;
        ContentValues[] reviews = movieInfo.reviews;

        String externalMovieId = movieInfo.movie.getAsString(MovieContract.MovieEntry.COLUMN_EXTERNAL_MOVIE_ID);
        Cursor movieCursor = db.query(
                MovieContract.MovieEntry.TABLE_NAME,
                null,
                MovieContract.MovieEntry.COLUMN_EXTERNAL_MOVIE_ID + " = ?",
                new String[]{externalMovieId},
                null,
                null,
                null
        );
//        String errorMessage = ""
//        TestUtilities.validateThenCloseCursor();
    }


    public static MovieTestInfo[] insertMultipleMoviesIntoDatabase(SQLiteDatabase db, int numRecords) {
        int numReviews = 2;
        int numTrailers = 3;

        MovieTestInfo[] movieList = new MovieTestInfo[numRecords];

        db.beginTransaction();
        try {
            for (int ii=0; ii<numRecords; ii++) {
                ContentValues movie = insertSingleMovie(db);

                int movieId = movie.getAsInteger(MovieContract.MovieEntry.COLUMN_EXTERNAL_MOVIE_ID);
                ContentValues[] trailers = insertMultipleTrailers(db, numTrailers, movieId);
                ContentValues[] reviews = insertMultipleReviews(db, numReviews, movieId);

                movieList[ii] = new MovieTestInfo(movie, trailers, reviews);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        return movieList;
    }

    public static ContentValues insertSingleMovie(SQLiteDatabase db) {
        ContentValues movie = TestUtilities.createTestMovieContentValues();
        int movieId = movie.getAsInteger(MovieContract.MovieEntry.COLUMN_EXTERNAL_MOVIE_ID);

        long _id = TestUtilities.insertRecordIntoDb(
                db,
                MovieContract.MovieEntry.TABLE_NAME,
                movie
        );
        movie.put(MovieContract.MovieEntry._ID, _id);
        return movie;
    }

    public static ContentValues[] insertMultipleTrailers(SQLiteDatabase db, int numRecords, int movieId) {
        ContentValues[] trailerList = new ContentValues[numRecords];
        for (int ii=0; ii<numRecords; ii++) {
            ContentValues trailer = TestUtilities.createTestTrailerContentValues();
            trailer.put(MovieContract.TrailerEntry.COLUMN_EXTERNAL_MOVIE_ID, movieId);
            long _id = TestUtilities.insertRecordIntoDb(
                    db,
                    MovieContract.TrailerEntry.TABLE_NAME,
                    trailer
            );
            trailer.put(MovieContract.TrailerEntry._ID, _id);
            trailerList[ii] = trailer;
        }
        return trailerList;
    }

    public static ContentValues[] insertMultipleReviews(SQLiteDatabase db, int numRecords, int movieId) {
        ContentValues[] reviewList = new ContentValues[numRecords];
        for (int ii=0; ii<numRecords; ii++) {
            ContentValues review = TestUtilities.createTestReviewContentValues();
            review.put(MovieContract.ReviewEntry.COLUMN_EXTERNAL_MOVIE_ID, movieId);
            long _id = TestUtilities.insertRecordIntoDb(
                    db,
                    MovieContract.ReviewEntry.TABLE_NAME,
                    review
            );
            review.put(MovieContract.ReviewEntry._ID, _id);
            reviewList[ii] = review;
        }
        return reviewList;
    }

}
