package com.jayodeji.android.popularmovies.utils;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import com.jayodeji.android.popularmovies.TestFavoriteMovieProvider;
import com.jayodeji.android.popularmovies.dbcontract.MovieContract;

import java.lang.reflect.Field;
import java.util.Set;

import static junit.framework.Assert.fail;

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

        int externalMovieId = movieInfo.movie.getAsInteger(MovieContract.MovieEntry.COLUMN_EXTERNAL_MOVIE_ID);

        validateEntriesMatchContentValues(
                db,
                MovieContract.MovieEntry.class,
                new ContentValues[]{movieInfo.movie},
                externalMovieId
        );

        validateEntriesMatchContentValues(
                db,
                MovieContract.TrailerEntry.class,
                movieInfo.trailers,
                externalMovieId
        );

        validateEntriesMatchContentValues(
                db,
                MovieContract.ReviewEntry.class,
                movieInfo.reviews,
                externalMovieId
        );
    }

    public static void validateEntriesMatchContentValues(
            SQLiteDatabase db,
            Class clazz,
            ContentValues[] expected,
            int extMovieId) {

        Cursor cursor = queryForEntries(db, clazz, extMovieId);

        String sortkey = TestUtilities.getStaticStringField(clazz, "DEFAULT_SORT_KEY");
        expected = TestUtilities.sortContentList(expected, sortkey, null);

        Set<String> keySet = expected[0].keySet();
        String[] columns = keySet.toArray(new String[keySet.size()]);
        String errorMessage = "Entry(ies) either do not match or do not exist";
        TestUtilities.validateListOfRecords(errorMessage, cursor, expected, columns);
    }

    public static Cursor queryForEntries(SQLiteDatabase db, Class clazz, int externalMovieId) {
        String tableName = TestUtilities.getStaticStringField(clazz, "TABLE_NAME");
        String where = TestUtilities.getStaticStringField(clazz, "COLUMN_EXTERNAL_MOVIE_ID") + " = ?";
        String[] whereVals = {String.valueOf(externalMovieId)};
        String sort = TestUtilities.getStaticStringField(clazz, "DEFAULT_SORT_KEY") + " ASC";
        return db.query(tableName, null, where, whereVals, null, null, sort);
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
