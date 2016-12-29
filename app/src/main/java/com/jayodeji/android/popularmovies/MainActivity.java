package com.jayodeji.android.popularmovies;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final int NUM_COLUMNS = 2;

    private static final String TAG = MainActivity.class.getSimpleName();

    private RecyclerView mRecyclerView;

    private TextView mErrorMessageDisplay;

    private ProgressBar mLoadingIndicator;

    private MoviePosterAdapter mMoviePosterAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView  = (RecyclerView) findViewById(R.id.rv_movie_posters);
        mErrorMessageDisplay  = (TextView) findViewById(R.id.tv_error_message_display);
        mLoadingIndicator  = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        mMoviePosterAdapter = new MoviePosterAdapter();

        GridLayoutManager layoutManager = new GridLayoutManager(this, NUM_COLUMNS);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mMoviePosterAdapter);


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
        String sortByMostPopular = "";
    }

    public void onClickSortByHighestRated() {
        String sortByHighestRated = "";
    }
}
