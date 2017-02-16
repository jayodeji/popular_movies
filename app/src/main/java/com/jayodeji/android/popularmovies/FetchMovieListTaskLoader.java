package com.jayodeji.android.popularmovies;

import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;
import android.view.View;

import com.jayodeji.android.popularmovies.utilities.MovieDbJsonUtils;
import com.jayodeji.android.popularmovies.utilities.NetworkUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;

/**
 * Created by joshuaadeyemi on 2/15/17.
 */

public class FetchMovieListTaskLoader extends AsyncTaskLoader<Movie[]> {

    public static final String MOVIE_PATH_KEY = "movie_path_key";

    private static final String TAG = FetchMovieListTaskLoader.class.getSimpleName();

    private MainActivity mActivityContext;

    private Movie[] mMovieList = null;
    String mMoviePath = "";

    public FetchMovieListTaskLoader(MainActivity context, String moviePath) {
        super(context);
        mActivityContext = context;
        mMoviePath = moviePath;

    }

    @Override
    protected void onStartLoading() {
        if (mMovieList != null) {
            deliverResult(mMovieList);
        } else {
            mActivityContext.mLoadingIndicator.setVisibility(View.VISIBLE);
            forceLoad();
        }
    }

    @Override
    public Movie[] loadInBackground() {
        Movie[] results = null;
        if (mMoviePath.length() > 0) {
            String apiKey = getContext().getString(R.string.movie_db_api_key);
            URL url = NetworkUtils.buildUrl(apiKey, mMoviePath);
            String response = null;
            try {
                response = NetworkUtils.getResponseFromHttpUrl(url);
                results = MovieDbJsonUtils.getMovieObjectsFromJson(response);
            } catch (IOException e) {
                Log.v(TAG, "Error getting response from url: " + url);
                e.printStackTrace();
            } catch (JSONException e) {
                Log.v(TAG, "Error parsing response: " + response);
                e.printStackTrace();
            }
        }
        return results;
    }

    @Override
    public void deliverResult(Movie[] data) {
        mMovieList = data;
        super.deliverResult(data);
    }
}