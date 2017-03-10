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

import com.jayodeji.android.popularmovies.dbcontract.MovieContract;
import com.jayodeji.android.popularmovies.dbcontract.MovieDbHelper;
import com.jayodeji.android.popularmovies.providers.FavoriteMovieProvider;
import com.jayodeji.android.popularmovies.utils.FavoriteMovieProviderTestUtils;
import com.jayodeji.android.popularmovies.utils.MovieTestInfo;
import com.jayodeji.android.popularmovies.utils.TestUtilities;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

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
        FavoriteMovieProviderTestUtils.clearAllRecords(mDatabase);
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
        MovieTestInfo[] movieList = FavoriteMovieProviderTestUtils.insertMultipleMoviesIntoDatabase(mDatabase, 4);

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
        MovieTestInfo movieObject = FavoriteMovieProviderTestUtils.insertMultipleMoviesIntoDatabase(mDatabase, 2)[0];
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
        MovieTestInfo movieObject = FavoriteMovieProviderTestUtils.insertMultipleMoviesIntoDatabase(mDatabase, 3)[0];
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

    @Test
    public void testCanGetMovieUsingMovieId() {
        fail("Not implemented yet");
    }

    @Test
    public void testCanInsertMovieUsingContentProvider() {
        ContentValues movie = TestUtilities.createTestMovieContentValues();
        Uri movieUri = mContext.getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, movie);

        String selection = MovieContract.MovieEntry._ID + " = ?";
        String[] selectionArgs = {movieUri.getLastPathSegment()};

        Cursor savedMovie = mDatabase.query(
                MovieContract.MovieEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        String errorMessage = "Movie was not properly inserted: ";
        TestUtilities.validateThenCloseCursor(errorMessage, savedMovie, movie);
    }

    @Test
    public void testCanInsertMultipleReviewsBelongingToAnExistingMovie() {
        fail("Not implemented");
    }

    @Test
    public void testCanInsertMultipleTrailersBelongingToAnExistingMovie() {
        fail("Not implemented");
    }

    @Test
    public void testCanInsertAMovieWithTrailersAndReviewsInOneTransaction() {
        fail("Not implemented");
    }

    @Test
    public void testCanDeleteAMovieWhichDeletesAllReviewsAndTrailers() {
        fail("Not implemented");
    }
}