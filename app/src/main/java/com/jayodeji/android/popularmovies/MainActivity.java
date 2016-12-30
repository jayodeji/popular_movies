package com.jayodeji.android.popularmovies;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jayodeji.android.popularmovies.utilities.MovieDbJsonUtils;
import com.jayodeji.android.popularmovies.utilities.NetworkUtils;

import java.net.URL;

public class MainActivity extends AppCompatActivity implements MovieGridAdapter.MoviePosterClickListener {

    private static final int NUM_COLUMNS = 2;

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String POPULAR_MOVIE_PATH = "popular";
    private static final String TOP_RATED_MOVIE_PATH = "top_rated";
    private static final String DEFAULT_MOVIE_PATH = POPULAR_MOVIE_PATH;

    private RecyclerView mRecyclerView;

    private TextView mErrorMessageDisplay;

    private ProgressBar mLoadingIndicator;

    private MovieGridAdapter mMovieGridAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView  = (RecyclerView) findViewById(R.id.rv_movie_posters);
        mErrorMessageDisplay  = (TextView) findViewById(R.id.tv_error_message_display);
        mLoadingIndicator  = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        mMovieGridAdapter = new MovieGridAdapter(this);

        GridLayoutManager layoutManager = new GridLayoutManager(this, NUM_COLUMNS);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mMovieGridAdapter);

        loadMovieData(DEFAULT_MOVIE_PATH);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
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
     * ONCLICK MENU EVENTS
     */
    public void onClickSortByMostPopular() {
        loadMovieData(POPULAR_MOVIE_PATH);
    }

    public void onClickSortByHighestRated() {
        loadMovieData(TOP_RATED_MOVIE_PATH);
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
            if (strings.length == 0) {
                return null;
            }
            String moviePath = strings[0];

            URL url = NetworkUtils.buildUrl(moviePath);
            String response = null;
            try {
                response = NetworkUtils.getResponseFromHttpUrl(url);
                Movie[] results = MovieDbJsonUtils.getMovieObjectsFromJson(response);
                return results;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Movie[] movies) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (movies != null) {
                showMoviePosterGrid();
                mMovieGridAdapter.setMovieList(movies);
            } else {
                showLoadingError();
            }
        }
    }
}
