package com.jayodeji.android.popularmovies.utils;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.jayodeji.android.popularmovies.dbcontract.MovieContract;

import java.util.Map;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNotSame;
import static junit.framework.Assert.assertTrue;

/**
 * Created by joshuaadeyemi on 2/26/17.
 */

public class TestUtilities {


    public static void insertRecordIntoDb(SQLiteDatabase db, String tableName, ContentValues data) {
        long insertedId = db.insert(tableName, null, data);
        validateDataInsertedSuccessfullyIntoDb(insertedId);
    }

    public static void  validateDataInsertedSuccessfullyIntoDb(long insertedId) {
        assertNotSame("Unable to insert into database", -1, insertedId);
    }

    public static void validateThenCloseCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertNotNull("Given cursor is null and it should not be", valueCursor);
        assertTrue("Empty cursor is given. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    public static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int index = valueCursor.getColumnIndex(columnName);

            String columnNotFoundError = "Column '" + columnName + "' not found. " + error;
            assertFalse(columnNotFoundError, index == -1);

            String expectedValue = entry.getValue().toString();
            String actualValue = valueCursor.getString(index);

            String valuesDontMatchError = "Actual Value '" + actualValue
                    + "' did not match the expected value '" + expectedValue
                    + "'." + error;
            assertEquals(valuesDontMatchError, expectedValue, actualValue);
        }
    }

    public static ContentValues createTestMovieContentValues() {
        String overViewString = "A rogue band of resistance fighters unite for a mission to steal " +
                "the Death Star plans and bring a new hope to the galaxy.";

        ContentValues testMovieValues = new ContentValues();
        testMovieValues.put(MovieContract.MovieEntry.COLUMN_EXTERNAL_MOVIE_ID, 127380);
        testMovieValues.put(MovieContract.MovieEntry.COLUMN_TITLE, "Test Movie III");
        testMovieValues.put(MovieContract.MovieEntry.COLUMN_POSTER_URL, "http://post.er/url.jpg");
        testMovieValues.put(MovieContract.MovieEntry.COLUMN_THUMBNAIL_URL, "http://thumbna.il/url.jpg");
        testMovieValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, overViewString);
        testMovieValues.put(MovieContract.MovieEntry.COLUMN_RATING, 7.4);
        testMovieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_YEAR, 2015);
        testMovieValues.put(MovieContract.MovieEntry.COLUMN_RUNTIME, 120);
        return testMovieValues;
    }

    public static ContentValues createTestTrailerContentValues() {
        ContentValues testTrailerValues = new ContentValues();
        testTrailerValues.put(MovieContract.TrailerEntry.COLUMN_NAME, "first trailer");
        testTrailerValues.put(MovieContract.TrailerEntry.COLUMN_KEY, "youtube1");
        testTrailerValues.put(MovieContract.TrailerEntry.COLUMN_URL, "http://youtu.be/youtube1");
        return testTrailerValues;
    }

    public static ContentValues createTestReviewContentValues() {
        ContentValues testReviewValues = new ContentValues();
        testReviewValues.put(MovieContract.ReviewEntry.COLUMN_REVIEW_ID, "review1");
        testReviewValues.put(MovieContract.ReviewEntry.COLUMN_AUTHOR, "reno");
        testReviewValues.put(MovieContract.ReviewEntry.COLUMN_URL, "http://revi.ew/review1");
        return testReviewValues;
    }
}