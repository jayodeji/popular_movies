package com.jayodeji.android.popularmovies;

import android.content.Intent;
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

    protected MovieGridAdapter mMovieGridAdapter;
    protected Movie[] mMovieList = null;


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
    protected void showMoviePosterGrid() {
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    protected void showLoadingError() {
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
        new FetchMovieListTask(this).execute("/movie/" + path);
    }

}
