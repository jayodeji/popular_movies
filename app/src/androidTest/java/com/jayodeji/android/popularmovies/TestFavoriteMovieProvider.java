package com.jayodeji.android.popularmovies;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.os.ParcelableCompat;

import com.jayodeji.android.popularmovies.dbcontract.MovieContract;
import com.jayodeji.android.popularmovies.dbcontract.MovieDbHelper;
import com.jayodeji.android.popularmovies.providers.FavoriteMovieProvider;
import com.jayodeji.android.popularmovies.utils.TestUtilities;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.Comparator;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;

/**
 * Created by joshuaadeyemi on 3/4/17.
 */

@RunWith(AndroidJUnit4.class)
public class TestFavoriteMovieProvider {

    private final Context mContext = InstrumentationRegistry.getTargetContext();
    private SQLiteDatabase mDatabase;
    private SQLiteOpenHelper mDbHelper;

    @Before
    public void before() {
        mDbHelper = (SQLiteOpenHelper) new MovieDbHelper(mContext);
        mDatabase = (SQLiteDatabase) mDbHelper.getWritableDatabase();
        clearAllRecords();
    }

    @After
    public void after() {
        mDbHelper.close();
    }

    @Test
    public void testProviderRegistry() {
        String packageName = mContext.getPackageName();
        String favoriteMovieProviderClassName = FavoriteMovieProvider.class.getName();
        ComponentName componentName = new ComponentName(packageName, favoriteMovieProviderClassName);

        try {
            PackageManager pm = mContext.getPackageManager();

            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);
            String actualAuthority = providerInfo.authority;
            String expectedAuthority = MovieContract.CONTENT_AUTHORITY;

            String errorMessage = "FavoriteMovieProvider registered with authority: "
                    + actualAuthority + "instead of expected authority: " + expectedAuthority;

            assertEquals(errorMessage, actualAuthority, expectedAuthority);
        } catch (PackageManager.NameNotFoundException e) {
            String errorMessage = "FavoriteMovieProvider not registered at " + packageName;
            fail(errorMessage);
        }

    }

    @Test
    public void testFavoriteMovieListQueryReturnsListOfFavoriteMovies() {
        MovieTestInfo[] movieList = insertMultipleMoviesIntoDatabase(4);

        String[] columns = {
                MovieContract.MovieEntry.COLUMN_POSTER_URL,
                MovieContract.MovieEntry.COLUMN_TITLE
        };
        String sortOrder = MovieContract.MovieEntry.COLUMN_TIMESTAMP + " DESC";

        Cursor movieListCursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                columns,
                null,
                null,
                sortOrder
        );

        String errorMessage = "List of movies returned is not what was expected";

        ContentValues[] movies = new ContentValues[movieList.length];
        for (int ii=0; ii<movieList.length; ii++) {
            movies[ii] = movieList[ii].movie;
        }

        TestUtilities.validateListOfRecords(errorMessage, movieListCursor, movies, columns);
    }

    @Test
    public void testMovieTrailersQueryReturnsListOfTrailersBelongingToAMovie() {
        MovieTestInfo movieObject = insertMultipleMoviesIntoDatabase(2)[0];
        String[] columns = {
                MovieContract.TrailerEntry.COLUMN_EXTERNAL_MOVIE_ID,
                MovieContract.TrailerEntry.COLUMN_KEY,
                MovieContract.TrailerEntry.COLUMN_NAME,
                MovieContract.TrailerEntry.COLUMN_URL
        };
        String sortOrder = MovieContract.TrailerEntry.COLUMN_KEY + " ASC";

        String movieId = movieObject.movie.getAsString(MovieContract.MovieEntry.COLUMN_EXTERNAL_MOVIE_ID);

        Cursor trailersCursor = mContext.getContentResolver().query(
                MovieContract.TrailerEntry.CONTENT_URI,
                columns,
                MovieContract.TrailerEntry.COLUMN_EXTERNAL_MOVIE_ID + " = ? ",
                new String[]{movieId},
                sortOrder
        );

        ContentValues[] trailers = movieObject.trailers;
        trailers = TestUtilities.sortContentList(trailers, MovieContract.TrailerEntry.COLUMN_KEY, "asc");

        String errorMessage = "List of trailers returned is not what was expected";
        TestUtilities.validateListOfRecords(errorMessage, trailersCursor, trailers, columns);
    }

    @Test
    public void testMovieReviewsQueryReturnsListOfReviewsBelongingToAMovie() {
        MovieTestInfo movieObject = insertMultipleMoviesIntoDatabase(3)[0];
        String[] columns = {
                MovieContract.ReviewEntry.COLUMN_EXTERNAL_MOVIE_ID,
                MovieContract.ReviewEntry.COLUMN_AUTHOR,
                MovieContract.ReviewEntry.COLUMN_REVIEW_ID,
                MovieContract.ReviewEntry.COLUMN_URL
        };
        String sortOrder = MovieContract.ReviewEntry.COLUMN_REVIEW_ID + " ASC";

        String movieId = movieObject.movie.getAsString(MovieContract.MovieEntry.COLUMN_EXTERNAL_MOVIE_ID);

        Cursor reviewsCursor = mContext.getContentResolver().query(
                MovieContract.ReviewEntry.CONTENT_URI,
                columns,
                MovieContract.ReviewEntry.COLUMN_EXTERNAL_MOVIE_ID + " = ? ",
                new String[]{movieId},
                sortOrder
        );

        ContentValues[] reviews = movieObject.reviews;
        reviews = TestUtilities.sortContentList(reviews, MovieContract.ReviewEntry.COLUMN_REVIEW_ID, "asc");

        String errorMessage = "List of reviews returned is not what was expected";
        TestUtilities.validateListOfRecords(errorMessage, reviewsCursor, reviews, columns);
    }

    private void clearAllRecords() {
        mDatabase.delete(MovieContract.MovieEntry.TABLE_NAME, null, null);
        mDatabase.delete(MovieContract.ReviewEntry.TABLE_NAME, null, null);
        mDatabase.delete(MovieContract.TrailerEntry.TABLE_NAME, null, null);
    }


    private MovieTestInfo[] insertMultipleMoviesIntoDatabase(int numRecords) {
        int numReviews = 2;
        int numTrailers = 3;

        MovieTestInfo[] movieList = new MovieTestInfo[numRecords];

        mDatabase.beginTransaction();
        try {
            for (int ii=0; ii<numRecords; ii++) {
                ContentValues movie = TestUtilities.createTestMovieContentValues();
                int movieId = movie.getAsInteger(MovieContract.MovieEntry.COLUMN_EXTERNAL_MOVIE_ID);

                long _id = TestUtilities.insertRecordIntoDb(
                        mDatabase,
                        MovieContract.MovieEntry.TABLE_NAME,
                        movie
                );
                movie.put(MovieContract.MovieEntry._ID, _id);

                ContentValues[] trailers = insertMultipleTrailers(numTrailers, movieId);
                ContentValues[] reviews = insertMultipleReviews(numReviews, movieId);

                MovieTestInfo movieTestInfo = new MovieTestInfo(movie, trailers, reviews);
                movieList[ii] = movieTestInfo;
            }
            mDatabase.setTransactionSuccessful();
        } finally {
            mDatabase.endTransaction();
        }

        return movieList;
    }

    private ContentValues[] insertMultipleTrailers(int numRecords, int movieId) {
        ContentValues[] trailerList = new ContentValues[numRecords];
        for (int ii=0; ii<numRecords; ii++) {
            ContentValues trailer = TestUtilities.createTestTrailerContentValues();
            trailer.put(MovieContract.TrailerEntry.COLUMN_EXTERNAL_MOVIE_ID, movieId);
            long _id = TestUtilities.insertRecordIntoDb(
                    mDatabase,
                    MovieContract.TrailerEntry.TABLE_NAME,
                    trailer
            );
            trailer.put(MovieContract.TrailerEntry._ID, _id);
            trailerList[ii] = trailer;
        }
        return trailerList;
    }

    private ContentValues[] insertMultipleReviews(int numRecords, int movieId) {
        ContentValues[] reviewList = new ContentValues[numRecords];
        for (int ii=0; ii<numRecords; ii++) {
            ContentValues review = TestUtilities.createTestReviewContentValues();
            review.put(MovieContract.ReviewEntry.COLUMN_EXTERNAL_MOVIE_ID, movieId);
            long _id = TestUtilities.insertRecordIntoDb(
                    mDatabase,
                    MovieContract.ReviewEntry.TABLE_NAME,
                    review
            );
            review.put(MovieContract.ReviewEntry._ID, _id);
            reviewList[ii] = review;
        }
        return reviewList;
    }

    private class MovieTestInfo {

        public final ContentValues movie;
        public final ContentValues[] trailers;
        public final ContentValues[] reviews;

        public MovieTestInfo(ContentValues movie, ContentValues[] trailers, ContentValues[] reviews) {
            this.movie = movie;
            this.trailers = trailers;
            this.reviews = reviews;
        }
    }
}