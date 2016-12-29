package com.jayodeji.android.popularmovies.utilities;

import com.jayodeji.android.popularmovies.MoviePoster;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by joshuaadeyemi on 12/29/16.
 */

public class MovieDbJsonUtils {

    private static final String MDB_RESULTS = "results";
    private static final String MDB_POSTER_PATH = "poster_path";

    public static MoviePoster[] getMovieObjectsFromJson(String responseJsonStr) throws JSONException {
        MoviePoster[] parsedMovies = null;

        JSONObject responseJson = new JSONObject(responseJsonStr);
        JSONArray resultsArray = responseJson.getJSONArray(MDB_RESULTS);

        int numResults = resultsArray.length();
        parsedMovies = new MoviePoster[numResults];

        for (int ii=0; ii<numResults; ii++) {
            JSONObject movieJson = resultsArray.getJSONObject(ii);
            String posterPath = movieJson.getString(MDB_POSTER_PATH);
            String posterUrl = generatePosterUrl(posterPath);
            parsedMovies[ii] = new MoviePoster(posterUrl);
        }
        return parsedMovies;
    }

    private static String generatePosterUrl(String path) {
        String baseUrl = "http://image.tmdb.org/t/p/";
        String posterSize = "w500";
        return baseUrl + posterSize + path;
    }
}
