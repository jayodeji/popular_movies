package com.jayodeji.android.popularmovies;

import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.jayodeji.android.popularmovies.utilities.MovieDbJsonUtils;
import com.jayodeji.android.popularmovies.utilities.NetworkUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;

/**
 * Created by joshuaadeyemi on 12/30/16.
 */
public class FetchMovieListTask extends AsyncTask<String, Void, Movie[]> {

    private static final String TAG = FetchMovieListTask.class.getSimpleName();

    private MainActivity activityContext;

    public FetchMovieListTask(MainActivity context) {
        activityContext = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        activityContext.mLoadingIndicator.setVisibility(View.VISIBLE);
    }

    @Override
    protected Movie[] doInBackground(String... strings) {
        Movie[] results = null;

        if (strings.length > 0) {
            String moviePath = strings[0];

            Resources resources = activityContext.getResources();
            String apiKey = resources.getString(R.string.movie_db_api_key);
            URL url = NetworkUtils.buildUrl(apiKey, moviePath);
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
    protected void onPostExecute(Movie[] movies) {
        activityContext.mLoadingIndicator.setVisibility(View.INVISIBLE);
        if (movies != null) {
            //save the movie list so we can recreate activity onCreate without another call
            activityContext.mMovieList = movies;
            activityContext.mMovieGridAdapter.setMovieList(movies);
            activityContext.showMoviePosterGrid();
        } else {
            activityContext.showLoadingError();
        }
    }
}
