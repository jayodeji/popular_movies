package com.jayodeji.android.popularmovies;

import android.content.ComponentName;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentUris;
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
import static junit.framework.Assert.assertTrue;
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
    public void testQueryForFavoriteMoviesWithoutIdReturnsListOfFavoriteMovies() {
        MovieTestInfo[] movieList = FavoriteMovieProviderTestUtils.insertMultipleMoviesIntoDatabase(mDatabase, 4);

        Cursor movieListCursor = mContext.getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI, null, null,null, null);

        String errorMessage = "List of movies returned is not what was expected";

        //extract out just the movies from the list of MovieTestInfo
        ContentValues[] movies = new ContentValues[movieList.length];
        for (int ii=0; ii<movieList.length; ii++) {
            movies[ii] = movieList[ii].movie;
        }

        String[] columns = {
                MovieContract.MovieEntry._ID,
                MovieContract.MovieEntry.COLUMN_POSTER_URL,
                MovieContract.MovieEntry.COLUMN_TITLE
        };

        TestUtilities.sortContentList(movies, MovieContract.MovieEntry._ID, "desc");
        TestUtilities.validateListOfRecords(errorMessage, movieListCursor, movies, columns);
    }


    @Test
    public void testQueryForFavoriteMoviesWithIdReturnsJustOneMovie() {
        MovieTestInfo[] movieList = FavoriteMovieProviderTestUtils.insertMultipleMoviesIntoDatabase(mDatabase, 2);
        ContentValues movie = movieList[0].movie;
        long movieId = movie.getAsLong(MovieContract.MovieEntry._ID);

        Uri uri = ContentUris.withAppendedId(MovieContract.MovieEntry.CONTENT_URI, movieId);
        Cursor movieCursor = mContext.getContentResolver().query(uri, null, null, null, null);

        String errorMessage = "Cannot query for single movie.";
        TestUtilities.validateThenCloseCursor(errorMessage, movieCursor, movie);
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
    public void testCanInsertMovieUsingContentProvider() {
        ContentValues movie = TestUtilities.createTestMovieContentValues();

        ContentResolver contentResolver = mContext.getContentResolver();
        Uri movieUri = contentResolver.insert(MovieContract.MovieEntry.CONTENT_URI, movie);

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
    public void testAttemptingToInsertTrailersForNonexistentMovieFails() {
        ContentValues[] trailers = {TestUtilities.createTestMovieContentValues()};
        int numInserted = mContext.getContentResolver().bulkInsert(
                MovieContract.TrailerEntry.CONTENT_URI,
                trailers
        );
        assertEquals("No records should have been inserted", 0, numInserted);
    }


    @Test
    public void testCanInsertMultipleTrailersBelongingToAnExistingMovie() {
        ContentValues movie = FavoriteMovieProviderTestUtils.insertSingleMovie(mDatabase);
        int externalMovieId = movie.getAsInteger(MovieContract.MovieEntry.COLUMN_EXTERNAL_MOVIE_ID);

        ContentValues[] trailers = new ContentValues[3];
        for (int ii=0; ii<3; ii++) {
            ContentValues trailer = TestUtilities.createTestTrailerContentValues();
            trailer.put(MovieContract.TrailerEntry.COLUMN_EXTERNAL_MOVIE_ID, externalMovieId);
            trailers[ii] = trailer;
        }

        int numInserted = mContext.getContentResolver().bulkInsert(MovieContract.TrailerEntry.CONTENT_URI, trailers);
        assertEquals("Number of inserted records not what was expected", trailers.length, numInserted);

        FavoriteMovieProviderTestUtils.validateEntriesMatchContentValues(
                mDatabase,
                MovieContract.TrailerEntry.class,
                trailers,
                externalMovieId
        );

    }

    @Test
    public void testAttemptingToInsertReviewsForNonexistentMovieFails() {
        ContentValues[] reviews = {TestUtilities.createTestReviewContentValues()};
        int numInserted = mContext.getContentResolver().bulkInsert(
                MovieContract.ReviewEntry.CONTENT_URI,
                reviews
        );
        assertEquals("No records should have been inserted", 0, numInserted);
    }

    @Test
    public void testCanInsertMultipleReviewsBelongingToAnExistingMovie() {
        ContentValues movie = FavoriteMovieProviderTestUtils.insertSingleMovie(mDatabase);
        int externalMovieId = movie.getAsInteger(MovieContract.MovieEntry.COLUMN_EXTERNAL_MOVIE_ID);

        ContentValues[] reviews = new ContentValues[3];
        for (int ii=0; ii<3; ii++) {
            ContentValues review = TestUtilities.createTestReviewContentValues();
            review.put(MovieContract.ReviewEntry.COLUMN_EXTERNAL_MOVIE_ID, externalMovieId);
            reviews[ii] = review;
        }

        int numInserted = mContext.getContentResolver().bulkInsert(
                MovieContract.ReviewEntry.CONTENT_URI,
                reviews
        );
        assertEquals("Number of inserted records not what was expected", reviews.length, numInserted);

        FavoriteMovieProviderTestUtils.validateEntriesMatchContentValues(
                mDatabase,
                MovieContract.ReviewEntry.class,
                reviews,
                externalMovieId
        );
    }

    @Test
    public void testCanDeleteAMovieWhichDeletesAllReviewsAndTrailers() {
        MovieTestInfo[] movieList = FavoriteMovieProviderTestUtils.insertMultipleMoviesIntoDatabase(mDatabase, 2);
        MovieTestInfo movieToTest = movieList[0];
        long _id = movieToTest.movie.getAsLong(MovieContract.MovieEntry._ID);
        int extMovieId = movieToTest.movie.getAsInteger(MovieContract.MovieEntry.COLUMN_EXTERNAL_MOVIE_ID);

        Uri uri = ContentUris.withAppendedId(MovieContract.MovieEntry.CONTENT_URI, extMovieId);
        mContext.getContentResolver().delete(uri, null, null);

        //test that movie and trailers belonging to movie are null
        Cursor movieCursor = FavoriteMovieProviderTestUtils.queryForEntries(mDatabase, MovieContract.MovieEntry.class, extMovieId);
        assertTrue("Movie should have been deleted", movieCursor.getCount() == 0);
        movieCursor.close();

        Cursor trailerCursor = FavoriteMovieProviderTestUtils.queryForEntries(mDatabase, MovieContract.TrailerEntry.class, extMovieId);
        assertTrue("Trailers should have been deleted", trailerCursor.getCount() == 0);
        trailerCursor.close();

        Cursor reviewCursor = FavoriteMovieProviderTestUtils.queryForEntries(mDatabase, MovieContract.ReviewEntry.class, extMovieId);
        assertTrue("Reviews should have been deleted", reviewCursor.getCount() == 0);
        reviewCursor.close();

        //test that no other movies are deleted
        for (int ii=1; ii<movieList.length; ii++) {
            FavoriteMovieProviderTestUtils.validateMovieSavedWithTrailersAndReviews(mDatabase, movieList[ii]);
        }
    }
}