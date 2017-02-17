package com.jayodeji.android.popularmovies;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.jayodeji.android.popularmovies.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements
        MovieGridAdapter.MoviePosterClickListener,
        LoaderManager.LoaderCallbacks<Movie[]> {

    private static final String TAG = MainActivity.class.getSimpleName();

    public static final String EXTRA_MOVIE_ARRAY = MainActivity.class.getSimpleName() + ".MOVIE_LIST";

    private static final int MOVIE_LIST_LOADER_ID = 0;

    private static final int NUM_COLUMNS = 2;

    private static final String POPULAR_MOVIE_PATH = "popular";
    private static final String TOP_RATED_MOVIE_PATH = "top_rated";
    private static final String DEFAULT_MOVIE_PATH = POPULAR_MOVIE_PATH;

    protected MovieGridAdapter mMovieGridAdapter;
    protected Movie[] mMovieList = null;

    private ActivityMainBinding mMainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        mMovieGridAdapter = new MovieGridAdapter(this);

        GridLayoutManager layoutManager = new GridLayoutManager(this, NUM_COLUMNS);
        mMainBinding.rvMoviePosters.setLayoutManager(layoutManager);
        mMainBinding.rvMoviePosters.setHasFixedSize(true);
        mMainBinding.rvMoviePosters.setAdapter(mMovieGridAdapter);

        if (savedInstanceState != null) {
            mMovieList = (Movie[]) savedInstanceState.getParcelableArray(EXTRA_MOVIE_ARRAY);
        }

        if (mMovieList != null) {
            mMovieGridAdapter.setMovieList(mMovieList);
        } else {
            Bundle loaderBundle = new Bundle();
            loaderBundle.putString(FetchMovieListTaskLoader.MOVIE_PATH_KEY, DEFAULT_MOVIE_PATH);
            getSupportLoaderManager().initLoader(MOVIE_LIST_LOADER_ID, loaderBundle, this);
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
                reloadMovieData(TOP_RATED_MOVIE_PATH);
                return true;
            case R.id.action_sort_most_popular:
                Log.v(TAG, "Sorting by most popular.");
                reloadMovieData(POPULAR_MOVIE_PATH);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    /**
     * TOGGLE THE ERROR OR MOVIE POSTER VIEWS
     */
    protected void showMoviePosterGrid() {
        mMainBinding.pbLoadingIndicator.setVisibility(View.INVISIBLE);
        mMainBinding.tvErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mMainBinding.rvMoviePosters.setVisibility(View.VISIBLE);
    }

    protected void showLoadingError() {
        mMainBinding.pbLoadingIndicator.setVisibility(View.INVISIBLE);
        mMainBinding.rvMoviePosters.setVisibility(View.INVISIBLE);
        mMainBinding.tvErrorMessageDisplay.setVisibility(View.VISIBLE);

    }

    protected void showLoading() {
        mMainBinding.pbLoadingIndicator.setVisibility(View.VISIBLE);
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

    private void reloadMovieData(String path) {
        Bundle loaderBundle = new Bundle();
        loaderBundle.putString(FetchMovieListTaskLoader.MOVIE_PATH_KEY, path);
        getSupportLoaderManager().restartLoader(MOVIE_LIST_LOADER_ID, loaderBundle, this);
    }

    @Override
    public Loader<Movie[]> onCreateLoader(int id, Bundle args) {
        String moviePath = null;
        if (args != null) {
            moviePath = args.getString(FetchMovieListTaskLoader.MOVIE_PATH_KEY);
        }
        if (moviePath == null) {
            moviePath = DEFAULT_MOVIE_PATH;
        }
        moviePath = "/movie/" + moviePath;
        return new FetchMovieListTaskLoader(this, moviePath);
    }

    @Override
    public void onLoadFinished(Loader<Movie[]> loader, Movie[] data) {
        mMovieGridAdapter.setMovieList(data);
        if (data == null) {
            this.showLoadingError();
        } else {
            this.showMoviePosterGrid();
        }
    }

    @Override
    public void onLoaderReset(Loader<Movie[]> loader) {

    }
}
