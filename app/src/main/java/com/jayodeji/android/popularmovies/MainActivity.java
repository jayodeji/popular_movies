package com.jayodeji.android.popularmovies;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private RecyclerView mRecyclerView;

    private TextView mErrorMessageDisplay;

    private ProgressBar mLoadingIndicator;

    private MovieListAdapter mMovieListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView  = (RecyclerView) findViewById(R.id.rv_movie_posters);
        mErrorMessageDisplay  = (TextView) findViewById(R.id.tv_error_message_display);
        mLoadingIndicator  = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        mMovieListAdapter = new MovieListAdapter();

        int spanCount = 1;
        GridLayoutManager layoutManager = new GridLayoutManager(this, spanCount);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mMovieListAdapter);

    }

    private void loadMovieInfo(String sortBy) {
        showMoviePosterGrid();
        new FetchMovieInfoTask().execute(sortBy);
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
        String sortByMostPopular = "";
        loadMovieInfo(sortByMostPopular);
    }

    public void onClickSortByHighestRated() {
        String sortByHighestRated = "";
        loadMovieInfo(sortByHighestRated);
    }

    public class FetchMovieInfoTask extends AsyncTask<String, Void, String[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected String[] doInBackground(String... strings) {
            return new String[0];
        }

        @Override
        protected void onPostExecute(String[] movieInfo) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (movieInfo != null) {
//                do something
            } else {
                showLoadingError();
            }
        }
    }
}
