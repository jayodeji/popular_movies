package com.jayodeji.android.popularmovies.moviedbutils;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.jayodeji.android.popularmovies.data.Movie;
import com.jayodeji.android.popularmovies.R;
import com.jayodeji.android.popularmovies.data.MoviePoster;
import com.jayodeji.android.popularmovies.utilities.NetworkUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by joshuaadeyemi on 2/18/17.
 */

public class Request {

    private static final String TAG = Request.class.getSimpleName();

    private static final String MOVIE_DB_BASE_URL = "https://api.themoviedb.org/3";
    private static final String MOVIE_PATH = "/movie";

    private static final String API_KEY_PARAM = "api_key";
    private static final String APPEND_TO_PARAM = "append_to_response";
    private static final String VIDEO_PARAM_VALUE = "videos";

    /**
     * Append the videos for the movie also, so we do not have to make a separate request for the trailers
     * @param context
     * @return
     */
    public static Movie getMovieDetail(Context context, int movieId) {
        String path = MOVIE_PATH + "/" + movieId;
        Uri.Builder uriBuilder = getBaseUriWithAuthentication(context, path);
        uriBuilder.appendQueryParameter(APPEND_TO_PARAM, VIDEO_PARAM_VALUE);
        URL url = buildUrlFromUri(uriBuilder);

        Movie movie = null;
        String response = null;
        try {
            response = NetworkUtils.getResponseFromHttpUrl(url);
            movie = Response.getMovieFromJson(response);
        } catch (IOException e) {
            Log.v(TAG, "Error getting response from movie detail url: " + url);
            e.printStackTrace();
        } catch (JSONException e) {
            Log.v(TAG, "Error parsing movie detail response: " + response);
            e.printStackTrace();
        }
        return movie;
    }

    /**
     * @param context
     * @param type, type can be either popular | highest rated
     * @return
     */
    public static MoviePoster[] getMovieListByType(Context context, String type) {
        String path = MOVIE_PATH + "/" + type;
        Uri.Builder uriBuilder = getBaseUriWithAuthentication(context, path);
        URL url = buildUrlFromUri(uriBuilder);

        MoviePoster[] moviePosterList = null;
        String response = null;
        try {
            response = NetworkUtils.getResponseFromHttpUrl(url);
            moviePosterList = Response.getMoviePosterObjectsFromJson(response);
        } catch (IOException e) {
            Log.v(TAG, "Error getting response from movie listing url: " + url);
            e.printStackTrace();
        } catch (JSONException e) {
            Log.v(TAG, "Error parsing movie listing response: " + response);
            e.printStackTrace();
        }
        return moviePosterList;
    }

    private static URL buildUrlFromUri(Uri.Builder uri) {
        URL url = null;
        try {
            url = new URL(uri.build().toString());
            Log.v(TAG, "Built Url: " + url);
        } catch (MalformedURLException e) {
            Log.v(TAG, "Error building Uri: " + uri);
            e.printStackTrace();
        }
        return url;
    }

    private static Uri.Builder getBaseUriWithAuthentication(Context context, String path) {
        String apiKey = context.getString(R.string.movie_db_api_key);
        Uri.Builder builtUri = Uri.parse(MOVIE_DB_BASE_URL + path).buildUpon()
                .appendQueryParameter(API_KEY_PARAM, apiKey);
        return builtUri;
    }

}
