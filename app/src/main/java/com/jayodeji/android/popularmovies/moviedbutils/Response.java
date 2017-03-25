package com.jayodeji.android.popularmovies.moviedbutils;

import android.util.Log;

import com.jayodeji.android.popularmovies.data.Movie;
import com.jayodeji.android.popularmovies.data.MoviePoster;
import com.jayodeji.android.popularmovies.data.Review;
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

    private static final String YOUTUBE_URL = "https://youtu.be";

    private static final String MDB_RESULTS = "results";

    private static final String MDB_MOVIE_ID = "id";
    private static final String MDB_POSTER_PATH = "poster_path";
    private static final String MDB_TITLE = "title";
    private static final String MDB_OVERVIEW = "overview";
    private static final String MDB_AVERAGE_VOTE = "vote_average";
    private static final String MDB_RELEASE_DATE = "release_date";
    private static final String MDB_RUNTIME = "runtime";
    private static final String MDB_TRAILERS = "trailers";
    private static final String MDB_YOUTUBE = "youtube";
    private static final String MDB_TRAILER_NAME = "name";
    private static final String MDB_TRAILER_SOURCE = "source";
    private static final String MDB_REVIEWS = "reviews";
    private static final String MDB_AUTHOR = "author";
    private static final String MDB_ID = "id";
    private static final String MDB_URL = "url";

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
                    .posterUrl(generatePosterUrl(movieJson.getString(MDB_POSTER_PATH)))
                    .releaseDate(formatReleaseDate(movieJson.getString(MDB_RELEASE_DATE)))
                    .runtime(movieJson.getInt(MDB_RUNTIME))
                    .rating(formatUserRating(movieJson.getString(MDB_AVERAGE_VOTE)))
                    .overview(movieJson.getString(MDB_OVERVIEW))
                    .trailers(getTrailersFromMovieDetail(movieJson.getJSONObject(MDB_TRAILERS)))
                    .reviews(getReviewsFromMovieDetail(movieJson.getJSONObject(MDB_REVIEWS)));

            movie = builder.build();
        }
        return movie;
    }

    private static Trailer[] getTrailersFromMovieDetail(JSONObject trailersJsonObject) throws JSONException {
        Trailer[] trailerList = null;

        if (trailersJsonObject != null) {
            JSONArray youtubeVideos = trailersJsonObject.getJSONArray(MDB_YOUTUBE);
            if (youtubeVideos != null) {
                int numTrailers = youtubeVideos.length();
                trailerList = new Trailer[numTrailers];

                for (int ii=0; ii<numTrailers; ii++) {
                    JSONObject trailerJson = youtubeVideos.getJSONObject(ii);

                    String key = trailerJson.getString(MDB_TRAILER_SOURCE);

                    Trailer.Builder builder = new Trailer.Builder();
                    builder.key(key)
                            .name(trailerJson.getString(MDB_TRAILER_NAME))
                            .url(formatYoutubeUrl(key));
                    trailerList[ii] = builder.build();
                }
            }
        }
        return trailerList;
    }

    private static Review[] getReviewsFromMovieDetail(JSONObject reviewsJsonObject) throws JSONException {
        Review[] reviewList = null;
        if (reviewsJsonObject != null) {
            JSONArray resultList = reviewsJsonObject.getJSONArray(MDB_RESULTS);
            int numReviews = resultList.length();
            reviewList = new Review[numReviews];

            for (int ii=0; ii<numReviews; ii++) {
                JSONObject reviewJson = resultList.getJSONObject(ii);
                Review.Builder builder = new Review.Builder();
                builder.reviewId(reviewJson.getString(MDB_ID))
                        .author(reviewJson.getString(MDB_AUTHOR))
                        .url(reviewJson.getString(MDB_URL));
                reviewList[ii] = builder.build();
            }
        }
        return reviewList;
    }

    private static String formatYoutubeUrl(String videoKey) {
        return YOUTUBE_URL + "/" + videoKey;
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

