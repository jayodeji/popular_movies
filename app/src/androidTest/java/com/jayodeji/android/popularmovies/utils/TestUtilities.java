package com.jayodeji.android.popularmovies.utils;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.jayodeji.android.popularmovies.dbcontract.MovieContract;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;

import io.bloco.faker.Faker;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNotSame;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

/**
 * Created by joshuaadeyemi on 2/26/17.
 */

public class TestUtilities {

    private static Faker sFaker = new Faker();

    public static long insertRecordIntoDb(SQLiteDatabase db, String tableName, ContentValues data) {
        long insertedId = db.insert(tableName, null, data);
        validateDataInsertedSuccessfullyIntoDb(insertedId);
        return insertedId;
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

    public static void validateListOfRecords(String error, Cursor recordsListCursor, ContentValues[] expectedValues, String[] columns) {
        //validate that they are the same length first
        String errorMessage = "Elements are not equals: " + error;
        assertEquals(errorMessage, expectedValues.length, recordsListCursor.getCount());

        //what happens if the order of objects is different?
        for (int ii=0; ii<expectedValues.length; ii++) {
            recordsListCursor.moveToPosition(ii);
            ContentValues expected = new ContentValues();
            for (String column: columns) {
                expected.put(column, expectedValues[ii].getAsString(column));
            }
            validateCurrentRecord(error, recordsListCursor, expected);
        }
        recordsListCursor.close();
    }

    /**
     * Always sort this in ascending order
     * @param content
     * @return
     */
    public static ContentValues[] sortContentList(ContentValues[] content, final String sortColumn, String order) {
        final int multiplier;
        if (order == null || order.toLowerCase() == "asc") {
            multiplier = 1;
        } else {
            multiplier = -1;
        }

        Arrays.sort(content, new Comparator<ContentValues>() {
            @Override
            public int compare(ContentValues o1, ContentValues o2) {
                String o1Key = o1.getAsString(sortColumn);
                String o2Key = o2.getAsString(sortColumn);
                //multiplying by the multiplier reverses if order is to be reversed
                return o1Key.compareTo(o2Key) * multiplier;
            }
        });
        return content;
    }

    public static ContentValues createTestMovieContentValues() {
        ContentValues testMovieValues = new ContentValues();
        testMovieValues.put(MovieContract.MovieEntry.COLUMN_EXTERNAL_MOVIE_ID, Integer.parseInt(sFaker.number.number(6)));
        testMovieValues.put(MovieContract.MovieEntry.COLUMN_TITLE, "Test Movie " + sFaker.number.number(6));
        testMovieValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, sFaker.lorem.paragraph(3));

        testMovieValues.put(MovieContract.MovieEntry.COLUMN_POSTER_URL, "http://post.er/url.jpg");
        testMovieValues.put(MovieContract.MovieEntry.COLUMN_THUMBNAIL_URL, "http://thumbna.il/url.jpg");
        testMovieValues.put(MovieContract.MovieEntry.COLUMN_RATING, 7.4);
        testMovieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_YEAR, 2015);
        testMovieValues.put(MovieContract.MovieEntry.COLUMN_RUNTIME, 120);
        return testMovieValues;
    }

    public static ContentValues createTestTrailerContentValues() {
        String youtubeKey = sFaker.number.number(10).toString();
        ContentValues testTrailerValues = new ContentValues();
        testTrailerValues.put(MovieContract.TrailerEntry.COLUMN_NAME, "first trailer");
        testTrailerValues.put(MovieContract.TrailerEntry.COLUMN_KEY, youtubeKey);
        testTrailerValues.put(MovieContract.TrailerEntry.COLUMN_URL, "http://youtu.be/"+ youtubeKey);
        return testTrailerValues;
    }

    public static ContentValues createTestReviewContentValues() {
        String reviewKey = sFaker.number.number(10).toString();
        ContentValues testReviewValues = new ContentValues();
        testReviewValues.put(MovieContract.ReviewEntry.COLUMN_REVIEW_ID, reviewKey);
        testReviewValues.put(MovieContract.ReviewEntry.COLUMN_AUTHOR, "reno");
        testReviewValues.put(MovieContract.ReviewEntry.COLUMN_URL, "http://revi.ew/"+reviewKey);
        return testReviewValues;
    }

    public static String getStaticStringField(Class clazz, String variableName) {
        String value = null;
        try {
            Field stringField = clazz.getDeclaredField(variableName);
            stringField.setAccessible(true);
            value = (String) stringField.get(null);
        } catch (NoSuchFieldException e) {
            fail("Cannot get static member variable with error: " + e.getMessage());

        } catch (IllegalAccessException e) {
            fail("Cannot get static member variable with error: " + e.getMessage());
        }
        return value;
    }
}