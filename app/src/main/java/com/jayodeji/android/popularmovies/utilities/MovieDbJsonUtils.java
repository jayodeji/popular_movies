package com.jayodeji.android.popularmovies.utilities;

import android.util.Log;

import com.jayodeji.android.popularmovies.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by joshuaadeyemi on 12/29/16.
 */

public class MovieDbJsonUtils {

    private static final String TAG = MovieDbJsonUtils.class.getSimpleName();

    private static final String MDB_RESULTS = "results";
    private static final String MDB_POSTER_PATH = "poster_path";
    private static final String MDB_ORIGINAL_TITLE = "original_title";
    private static final String MDB_OVERVIEW = "overview";
    private static final String MDB_AVERAGE_VOTE = "vote_average";
    private static final String MDB_RELEASE_DATE = "release_date";

    private static final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/";
    private static final String POSTER_SIZE = "w500";
    private static final String THUMBNAIL_SIZE = "w92";

    public static Movie[] getMovieObjectsFromJson(String responseJsonStr) throws JSONException {
        Movie[] parsedMovies = null;
        if (responseJsonStr != null) {
            JSONObject responseJson = new JSONObject(responseJsonStr);
            JSONArray resultsArray = responseJson.getJSONArray(MDB_RESULTS);

            int numResults = resultsArray.length();
            parsedMovies = new Movie[numResults];

            for (int ii=0; ii<numResults; ii++) {
                JSONObject movieJson = resultsArray.getJSONObject(ii);

                String posterPath = movieJson.getString(MDB_POSTER_PATH);
                String title = movieJson.getString(MDB_ORIGINAL_TITLE);
                String overview = movieJson.getString(MDB_OVERVIEW);
                String releaseDate = movieJson.getString(MDB_RELEASE_DATE);
                String rating = movieJson.getString(MDB_AVERAGE_VOTE);

                Movie.Builder builder = new Movie.Builder();
                builder.overview(overview)
                        .rating(formatUserRating(rating))
                        .releaseDate(formatReleaseDate(releaseDate))
                        .title(title)
                        .posterUrl(generatePosterUrl(posterPath))
                        .thumbnailUrl(generateThumbnailUrl(posterPath));
                parsedMovies[ii] = builder.build();
            }
        }
        return parsedMovies;
    }

    private static String formatUserRating(String rating) {
        return rating + "/10";
    }

    private static String formatReleaseDate(String releaseDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;

        try {
            date = dateFormat.parse(releaseDate);
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(date);

            int year = calendar.get(Calendar.YEAR);
            return String.valueOf(year);
        } catch (ParseException e) {
            String message = "Error parsing date string: " + releaseDate;
            Log.e(TAG, message);
            return "";
        }

    }

    private static String generatePosterUrl(String path) {
        return POSTER_BASE_URL + POSTER_SIZE + path;
    }

    private static String generateThumbnailUrl(String path) {
        return POSTER_BASE_URL + THUMBNAIL_SIZE + path;
    }
}

