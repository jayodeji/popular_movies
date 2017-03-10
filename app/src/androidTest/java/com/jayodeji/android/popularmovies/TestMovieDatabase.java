package com.jayodeji.android.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.jayodeji.android.popularmovies.dbcontract.MovieContract;
import com.jayodeji.android.popularmovies.dbcontract.MovieDbHelper;
import com.jayodeji.android.popularmovies.utils.TestUtilities;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashSet;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Created by joshuaadeyemi on 2/26/17.
 */

@RunWith(AndroidJUnit4.class)
public class TestMovieDatabase {

    private final Context context = InstrumentationRegistry.getTargetContext();
    private SQLiteDatabase database;
    private SQLiteOpenHelper dbHelper;

    @Before
    public void before() {
        context.deleteDatabase(MovieDbHelper.DATABASE_NAME);
        dbHelper = (SQLiteOpenHelper) new MovieDbHelper(context);
        database = (SQLiteDatabase) dbHelper.getWritableDatabase();
    }

    @After
    public void after() {
        dbHelper.close();
    }

    @Test
    public void testDatabaseCreatedWithAppropriateTables() {
        final HashSet<String> tableNameHashSet = new HashSet<>();
        tableNameHashSet.add(MovieContract.MovieEntry.TABLE_NAME);
        tableNameHashSet.add(MovieContract.TrailerEntry.TABLE_NAME);
        tableNameHashSet.add(MovieContract.ReviewEntry.TABLE_NAME);

        //verify that the database is open
        assertEquals("The database should be open, but is not", true, database.isOpen());

        Cursor tableNameCursor = database.rawQuery("SELECT name from sqlite_master WHERE type='table'", null);

        assertTrue("The database was not created properly", tableNameCursor.moveToFirst());

        do {
            String tableName = tableNameCursor.getString(0);
            tableNameHashSet.remove(tableName);
        } while (tableNameCursor.moveToNext());

        assertTrue("Database was created without the expected tables", tableNameHashSet.isEmpty());

        tableNameCursor.close();
    }

    @Test
    public void testMovieCanBeInsertedIntoMovieTable() {
        ContentValues movieContentValues = TestUtilities.createTestMovieContentValues();

        TestUtilities.insertRecordIntoDb(database, MovieContract.MovieEntry.TABLE_NAME, movieContentValues);

        int movieId = (int) movieContentValues.get(MovieContract.MovieEntry.COLUMN_EXTERNAL_MOVIE_ID);
        Cursor newDataCursor = fetchMovieFromDbByMovieId(movieId);

        TestUtilities.validateThenCloseCursor("Error inserting movie data", newDataCursor, movieContentValues);
    }

    @Test
    public void testDuplicateExternalMovieIdInsertBehaviorShouldReplace() {
        ContentValues movieContentValues = TestUtilities.createTestMovieContentValues();
        movieContentValues.put(MovieContract.MovieEntry.COLUMN_TITLE, "Test Movie I");

        TestUtilities.insertRecordIntoDb(database, MovieContract.MovieEntry.TABLE_NAME, movieContentValues);

        int movieId = (int) movieContentValues.get(MovieContract.MovieEntry.COLUMN_EXTERNAL_MOVIE_ID);
        Cursor movieDbData = fetchMovieFromDbByMovieId(movieId);

        TestUtilities.validateThenCloseCursor("Error inserting initial movie data", movieDbData, movieContentValues);

        movieContentValues.put(MovieContract.MovieEntry.COLUMN_TITLE, "Test Movie II");

        TestUtilities.insertRecordIntoDb(database, MovieContract.MovieEntry.TABLE_NAME, movieContentValues);

        Cursor newMovieDbData = fetchMovieFromDbByMovieId(movieId);

        TestUtilities.validateThenCloseCursor("Error inserting duplicate movie data", newMovieDbData, movieContentValues);
    }

    @Test
    public void testTrailerDataCannotBeInsertedIntoTrailerTableIfExternalMovieIdNotGiven() {
        ContentValues trailerContentValues = TestUtilities.createTestTrailerContentValues();
        long insertedId = database.insert(MovieContract.TrailerEntry.TABLE_NAME, null, trailerContentValues);
        assertEquals("The insertion should not have been successful", insertedId, -1);
    }

    @Test
    public void testTrailerDataCanBeInsertedIntoTrailerTable() {
        ContentValues movieContentValues = TestUtilities.createTestMovieContentValues();

        int externalMovieId = movieContentValues.getAsInteger(MovieContract.MovieEntry.COLUMN_EXTERNAL_MOVIE_ID);

        ContentValues trailerContentValues = TestUtilities.createTestTrailerContentValues();
        trailerContentValues.put(MovieContract.TrailerEntry.COLUMN_EXTERNAL_MOVIE_ID, externalMovieId);

        TestUtilities.insertRecordIntoDb(database, MovieContract.TrailerEntry.TABLE_NAME, trailerContentValues);

        String trailerKey = trailerContentValues.getAsString(MovieContract.TrailerEntry.COLUMN_KEY);
        Cursor trailerCursor = fetchTrailerFromDbByKey(trailerKey);

        TestUtilities.validateThenCloseCursor("Error inserting trailer data", trailerCursor, trailerContentValues);
    }

    @Test
    public void testDuplicateTrailerKeyInsertBehaviorShouldIgnore() {
        ContentValues movieContentValues = TestUtilities.createTestMovieContentValues();

        int externalMovieId = movieContentValues.getAsInteger(MovieContract.MovieEntry.COLUMN_EXTERNAL_MOVIE_ID);

        ContentValues trailerContentValues = TestUtilities.createTestTrailerContentValues();
        trailerContentValues.put(MovieContract.TrailerEntry.COLUMN_EXTERNAL_MOVIE_ID, externalMovieId);

        TestUtilities.insertRecordIntoDb(database, MovieContract.TrailerEntry.TABLE_NAME, trailerContentValues);

        String trailerKey = trailerContentValues.getAsString(MovieContract.TrailerEntry.COLUMN_KEY);
        Cursor trailerCursor = fetchTrailerFromDbByKey(trailerKey);

        TestUtilities.validateThenCloseCursor("Error inserting initial trailer data", trailerCursor, trailerContentValues);

        ContentValues newTrailerContentValues = TestUtilities.createTestTrailerContentValues();
        newTrailerContentValues.put(MovieContract.TrailerEntry.COLUMN_KEY, trailerKey);
        newTrailerContentValues.put(MovieContract.TrailerEntry.COLUMN_NAME, "random trailer");

        TestUtilities.insertRecordIntoDb(database, MovieContract.TrailerEntry.TABLE_NAME, newTrailerContentValues);

        Cursor newTrailerCursor = fetchTrailerFromDbByKey(trailerKey);

        TestUtilities.validateThenCloseCursor("Error inserting duplicate trailer data", newTrailerCursor, trailerContentValues);
    }

    @Test
    public void testReviewDataCannotBeInsertedIntoReviewTableIfExternalMovieIdNotGiven() {
        ContentValues reviewContentValues = TestUtilities.createTestReviewContentValues();
        long insertedId = database.insert(MovieContract.ReviewEntry.TABLE_NAME, null, reviewContentValues);
        assertEquals("The insertion should not have been successful", insertedId, -1);
    }

    @Test
    public void testReviewDataCanBeInsertedIntoReviewTable() {
        ContentValues movieContentValues = TestUtilities.createTestMovieContentValues();

        int externalMovieId = movieContentValues.getAsInteger(MovieContract.MovieEntry.COLUMN_EXTERNAL_MOVIE_ID);

        ContentValues reviewContentValues = TestUtilities.createTestReviewContentValues();
        reviewContentValues.put(MovieContract.ReviewEntry.COLUMN_EXTERNAL_MOVIE_ID, externalMovieId);

        TestUtilities.insertRecordIntoDb(database, MovieContract.ReviewEntry.TABLE_NAME, reviewContentValues);

        String reviewId = reviewContentValues.getAsString(MovieContract.ReviewEntry.COLUMN_REVIEW_ID);
        Cursor reviewCursor = fetchReviewFromDbByReviewId(reviewId);

        TestUtilities.validateThenCloseCursor("Error inserting review data", reviewCursor, reviewContentValues);
    }

    @Test
    public void testDuplicateReviewKeyInsertBehaviorShouldIgnore() {
        ContentValues movieContentValues = TestUtilities.createTestMovieContentValues();

        int externalMovieId = movieContentValues.getAsInteger(MovieContract.MovieEntry.COLUMN_EXTERNAL_MOVIE_ID);

        ContentValues reviewContentValues = TestUtilities.createTestReviewContentValues();
        reviewContentValues.put(MovieContract.ReviewEntry.COLUMN_EXTERNAL_MOVIE_ID, externalMovieId);

        TestUtilities.insertRecordIntoDb(database, MovieContract.ReviewEntry.TABLE_NAME, reviewContentValues);

        String reviewId = reviewContentValues.getAsString(MovieContract.ReviewEntry.COLUMN_REVIEW_ID);
        Cursor reviewCursor = fetchReviewFromDbByReviewId(reviewId);

        TestUtilities.validateThenCloseCursor("Error inserting initial review data", reviewCursor, reviewContentValues);


        ContentValues newReviewContentValues = TestUtilities.createTestReviewContentValues();
        newReviewContentValues.put(MovieContract.ReviewEntry.COLUMN_REVIEW_ID, reviewId);
        newReviewContentValues.put(MovieContract.TrailerEntry.COLUMN_NAME, "random user review");

        TestUtilities.insertRecordIntoDb(database, MovieContract.ReviewEntry.TABLE_NAME, newReviewContentValues);

        Cursor newReviewCursor = fetchReviewFromDbByReviewId(reviewId);

        TestUtilities.validateThenCloseCursor("Error inserting duplicate review data", newReviewCursor, reviewContentValues);
    }


    private Cursor fetchTrailerFromDbByKey(String key) {
        String whereQuery = MovieContract.TrailerEntry.COLUMN_KEY + " = ? ";
        String[] selectionArgs = {key};
        return database.query(MovieContract.TrailerEntry.TABLE_NAME, null, whereQuery, selectionArgs, null, null, null);
    }

    private Cursor fetchReviewFromDbByReviewId(String id) {
        String whereQuery = MovieContract.ReviewEntry.COLUMN_REVIEW_ID + " = ? ";
        String[] selectionArgs = {id};
        return database.query(MovieContract.ReviewEntry.TABLE_NAME, null, whereQuery, selectionArgs, null, null, null);
    }

    private Cursor fetchMovieFromDbByMovieId(int id) {
        String whereQuery = MovieContract.MovieEntry.COLUMN_EXTERNAL_MOVIE_ID + " = ? ";
        String[] selectionArgs = {String.valueOf(id)};
        return database.query(MovieContract.MovieEntry.TABLE_NAME, null, whereQuery, selectionArgs, null, null, null);
    }

}
