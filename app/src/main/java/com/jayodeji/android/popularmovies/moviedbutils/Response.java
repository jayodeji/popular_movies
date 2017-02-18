package com.jayodeji.android.popularmovies.moviedbutils;

import android.util.Log;

import com.jayodeji.android.popularmovies.data.Movie;
import com.jayodeji.android.popularmovies.data.MoviePoster;
import com.jayodeji.android.popularmovies.data.Trailer;

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

public class Response {

    private static final String TAG = Response.class.getSimpleName();

    private static final String TYPE_TRAILER = "trailer";

    private static final String MDB_RESULTS = "results";

    private static final String MDB_MOVIE_ID = "id";
    private static final String MDB_POSTER_PATH = "poster_path";
    private static final String MDB_TITLE = "title";
    private static final String MDB_OVERVIEW = "overview";
    private static final String MDB_AVERAGE_VOTE = "vote_average";
    private static final String MDB_RELEASE_DATE = "release_date";
    private static final String MDB_RUNTIME = "runtime";
    private static final String MDB_VIDEOS = "videos";
    private static final String MDB_VIDEO_TYPE = "type";
    private static final String MDB_VIDEO_NAME = "name";
    private static final String MDB_VIDEO_KEY = "key";

    private static final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/";
    private static final String POSTER_SIZE = "w500";
    private static final String THUMBNAIL_SIZE = "w92";

    public static MoviePoster[] getMoviePosterObjectsFromJson(String responseJsonStr) throws JSONException {
        MoviePoster[] moviePosterList = null;
        if (responseJsonStr != null) {
            JSONObject responseJson = new JSONObject(responseJsonStr);
            JSONArray resultsArray = responseJson.getJSONArray(MDB_RESULTS);

            int numResults = resultsArray.length();
            moviePosterList = new MoviePoster[numResults];

            for (int ii=0; ii<numResults; ii++) {
                JSONObject movieJson = resultsArray.getJSONObject(ii);

                MoviePoster.Builder builder = new MoviePoster.Builder();
                builder.movieId(movieJson.getInt(MDB_MOVIE_ID))
                        .posterUrl(generatePosterUrl(movieJson.getString(MDB_POSTER_PATH)))
                        .title(movieJson.getString(MDB_TITLE));

                moviePosterList[ii] = builder.build();
            }
        }
        return moviePosterList;
    }

    public static Movie getMovieFromJson(String responseJsonStr) throws JSONException {
        Movie movie = null;
        if (responseJsonStr != null) {
            JSONObject movieJson = new JSONObject(responseJsonStr);

            Movie.Builder builder = new Movie.Builder();
            builder.movieId(movieJson.getInt(MDB_MOVIE_ID))
                    .title(movieJson.getString(MDB_TITLE))
                    .thumbnailUrl(generateThumbnailUrl(movieJson.getString(MDB_POSTER_PATH)))
                    .releaseDate(formatReleaseDate(movieJson.getString(MDB_RELEASE_DATE)))
                    .runtime(movieJson.getInt(MDB_RUNTIME))
                    .rating(formatUserRating(movieJson.getString(MDB_AVERAGE_VOTE)))
                    .overview(movieJson.getString(MDB_OVERVIEW))
                    .trailers(getTrailersFromMovieDetail(movieJson.getJSONObject(MDB_VIDEOS)));

            movie = builder.build();
        }
        return movie;
    }

    private static Trailer[] getTrailersFromMovieDetail(JSONObject videoJsonObject) throws JSONException {
        Trailer[] trailerList = null;
        if (videoJsonObject != null) {
            JSONArray resultsArray = videoJsonObject.getJSONArray(MDB_RESULTS);

            int numResults = resultsArray.length();
            trailerList = new Trailer[numResults];

            for (int ii=0; ii<numResults; ii++) {
                JSONObject trailerJson = resultsArray.getJSONObject(ii);
                //the video has to be a trailer
                if (trailerJson.getString(MDB_VIDEO_TYPE).equals(TYPE_TRAILER)) {
                    Trailer.Builder builder = new Trailer.Builder();
                    builder.key(trailerJson.getString(MDB_VIDEO_KEY))
                            .name(trailerJson.getString(MDB_VIDEO_NAME));

                    trailerList[ii] = builder.build();
                }
            }
        }
        return trailerList;
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

