package com.jayodeji.android.popularmovies.utilities;

import com.jayodeji.android.popularmovies.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by joshuaadeyemi on 12/29/16.
 */

public class MovieDbJsonUtils {

    private static final String MDB_RESULTS = "results";
    private static final String MDB_POSTER_PATH = "poster_path";
    private static final String MDB_ORIGINAL_TITLE = "original_title";
    private static final String MDB_OVERVIEW = "overview";
    private static final String MDB_AVERAGE_VOTE = "vote_average";
    private static final String MDB_RELEASE_DATE = "release_date";

    public static Movie[] getMovieObjectsFromJson(String responseJsonStr) throws JSONException {
        Movie[] parsedMovies = null;

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
                    .rating(rating)
                    .releaseDate(releaseDate)
                    .title(title)
                    .posterUrl(generatePosterUrl(posterPath))
                    .thumbnailUrl(generateThumbnailUrl(posterPath));
            parsedMovies[ii] = builder.build();
        }
        return parsedMovies;
    }

    private static String generatePosterUrl(String path) {
        String baseUrl = "http://image.tmdb.org/t/p/";
        String posterSize = "w500";
        return baseUrl + posterSize + path;
    }

    private static String generateThumbnailUrl(String path) {
        String baseUrl = "http://image.tmdb.org/t/p/";
        String posterSize = "w92";
        return baseUrl + posterSize + path;
    }
}