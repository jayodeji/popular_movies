package com.jayodeji.android.popularmovies;

import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jayodeji.android.popularmovies.utilities.MovieDbJsonUtils;
import com.jayodeji.android.popularmovies.utilities.NetworkUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MovieGridAdapter.MoviePosterClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    public static final String EXTRA_MOVIE_ARRAY = MainActivity.class.getSimpleName() + ".MOVIE_LIST";

    private static final int NUM_COLUMNS = 2;

    private static final String POPULAR_MOVIE_PATH = "popular";
    private static final String TOP_RATED_MOVIE_PATH = "top_rated";
    private static final String DEFAULT_MOVIE_PATH = POPULAR_MOVIE_PATH;

    @BindView(R.id.rv_movie_posters) protected RecyclerView mRecyclerView;
    @BindView(R.id.tv_error_message_display) protected TextView mErrorMessageDisplay;
    @BindView(R.id.pb_loading_indicator) protected ProgressBar mLoadingIndicator;

    private MovieGridAdapter mMovieGridAdapter;
    private Movie[] mMovieList = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        mMovieGridAdapter = new MovieGridAdapter(this);

        GridLayoutManager layoutManager = new GridLayoutManager(this, NUM_COLUMNS);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mMovieGridAdapter);

        if (savedInstanceState != null) {
            mMovieList = (Movie[]) savedInstanceState.getParcelableArray(EXTRA_MOVIE_ARRAY);
        }

        if (mMovieList != null) {
            mMovieGridAdapter.setMovieList(mMovieList);
        } else {
            loadMovieData(DEFAULT_MOVIE_PATH);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mMovieList != null) {
            outState.putParcelableArray(EXTRA_MOVIE_ARRAY, mMovieList);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.action_sort_highest_rated:
                Log.v(TAG, "Sorting by highest rated.");
                loadMovieData(TOP_RATED_MOVIE_PATH);
                return true;
            case R.id.action_sort_most_popular:
                Log.v(TAG, "Sorting by most popular.");
                loadMovieData(POPULAR_MOVIE_PATH);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    /**
     * TOGGLE THE ERROR OR MOVIE POSTER VIEWS
     */
    private void showMoviePosterGrid() {
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showLoadingError() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    /**
     * Listen to click events on the recycler view
     * @param clickedMovie
     */
    public void onMoviePosterClick(Movie clickedMovie) {
        Intent intent = new Intent(this, MovieDetailActivity.class);
        intent.putExtra(MovieDetailActivity.EXTRA_MOVIE, clickedMovie);
        startActivity(intent);
    }

    private void loadMovieData(String path) {
        new FetchMovieListTask().execute("/movie/" + path);
    }

    public class FetchMovieListTask extends AsyncTask<String, Void, Movie[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected Movie[] doInBackground(String... strings) {
            Movie[] results = null;

            if (strings.length > 0) {
                String moviePath = strings[0];

                Resources resources = getResources();
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
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (movies != null) {
                //save the movie list so we can recreate activity onCreate without another call
                mMovieList = movies;
                mMovieGridAdapter.setMovieList(movies);
                showMoviePosterGrid();
            } else {

                showLoadingError();
            }
        }
    }
}
