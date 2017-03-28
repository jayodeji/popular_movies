package com.jayodeji.android.popularmovies;

import android.content.Intent;
import android.graphics.Rect;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jayodeji.android.popularmovies.data.MoviePoster;
import com.jayodeji.android.popularmovies.async.FetchMovieListTaskLoader;

//TODO too much spacing/margins between rows when device is in portrait mode
public class MainActivity extends AppCompatActivity implements
        MovieGridAdapter.MoviePosterClickListener,
        LoaderManager.LoaderCallbacks<MoviePoster[]> {

    private static final String TAG = MainActivity.class.getSimpleName();

    public static final String EXTRA_MOVIE_ARRAY = MainActivity.class.getSimpleName() + ".MOVIE_LIST";

    private static final int MOVIE_LIST_LOADER_ID = 0;

    private static final int ITEM_SPACING = 2;

    public static final String POPULAR_MOVIE_TYPE = "popular";
    public static final String TOP_RATED_MOVIE_TYPE = "top_rated";
    public static final String FAVORITES_MOVIE_TYPE = "favorites";
    public static final String DEFAULT_MOVIE_TYPE = POPULAR_MOVIE_TYPE;

    protected MovieGridAdapter mMovieGridAdapter;
    protected MoviePoster[] mMovieList = null;

    private RecyclerView mMoviePosterListView;
    private ProgressBar mLoadingIndicator;
    private TextView mLoadingErrorMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMoviePosterListView = (RecyclerView) findViewById(R.id.movie_poster_list);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.loading_indicator);
        mLoadingErrorMessage = (TextView) findViewById(R.id.error_message_display);

        mMovieGridAdapter = new MovieGridAdapter(this);
        mMoviePosterListView.setHasFixedSize(true);
        mMoviePosterListView.setAdapter(mMovieGridAdapter);
        mMoviePosterListView.addItemDecoration(new SpacesItemDecoration(ITEM_SPACING));

        if (savedInstanceState != null) {
            mMovieList = (MoviePoster[]) savedInstanceState.getParcelableArray(EXTRA_MOVIE_ARRAY);
        }

        if (mMovieList != null) {
            mMovieGridAdapter.setMovieList(mMovieList);
        } else {
            Bundle loaderBundle = new Bundle();
            loaderBundle.putString(FetchMovieListTaskLoader.MOVIE_TYPE_KEY, DEFAULT_MOVIE_TYPE);
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
                reloadMovieData(TOP_RATED_MOVIE_TYPE);
                return true;
            case R.id.action_sort_most_popular:
                Log.v(TAG, "Sorting by most popular.");
                reloadMovieData(POPULAR_MOVIE_TYPE);
                return true;
            case R.id.action_favorites:
                Log.v(TAG, "Sorting by favorites.");
                reloadMovieData(FAVORITES_MOVIE_TYPE);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    /**
     * TOGGLE THE ERROR OR MOVIE POSTER VIEWS
     */
    public void showMoviePosterGrid() {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mLoadingErrorMessage.setVisibility(View.INVISIBLE);
        mMoviePosterListView.setVisibility(View.VISIBLE);
    }

    public void showLoadingError() {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mMoviePosterListView.setVisibility(View.INVISIBLE);
        mLoadingErrorMessage.setVisibility(View.VISIBLE);
    }

    public void showLoading() {
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }

    /**
     * Listen to click events on the recycler view
     * When a Poster is selected, make a request to the movie db to get
     * movie details
     * @param clickedMovie
     */
    public void onMoviePosterClick(MoviePoster clickedMovie) {
        Intent intent = new Intent(this, MovieDetailActivity.class);
        intent.putExtra(MovieDetailActivity.EXTRA_MOVIE_ID, clickedMovie.movieId);
        intent.putExtra(MovieDetailActivity.EXTRA_MOVIE_TITLE, clickedMovie.title);
        startActivity(intent);
    }

    private void reloadMovieData(String path) {
        Bundle loaderBundle = new Bundle();
        loaderBundle.putString(FetchMovieListTaskLoader.MOVIE_TYPE_KEY, path);
        getSupportLoaderManager().restartLoader(MOVIE_LIST_LOADER_ID, loaderBundle, this);
    }

    @Override
    public Loader<MoviePoster[]> onCreateLoader(int id, Bundle args) {
        String movieListType = null;
        if (args != null) {
            movieListType = args.getString(FetchMovieListTaskLoader.MOVIE_TYPE_KEY);
        }
        if (movieListType == null) {
            movieListType = DEFAULT_MOVIE_TYPE;
        }
        return new FetchMovieListTaskLoader(this, movieListType);
    }

    @Override
    public void onLoadFinished(Loader<MoviePoster[]> loader, MoviePoster[] data) {
        mMovieGridAdapter.setMovieList(data);
        if (data == null) {
            this.showLoadingError();
        } else {
            this.showMoviePosterGrid();
        }
    }

    @Override
    public void onLoaderReset(Loader<MoviePoster[]> loader) {

    }

    /*

    Decorator which adds spacing around the tiles in a Grid layout RecyclerView. Apply to a RecyclerView with:
    SpacesItemDecoration decoration = new SpacesItemDecoration(16);
    mRecyclerView.addItemDecoration(decoration);

    Feel free to add any value you wish for SpacesItemDecoration. That value determines the amount of spacing.
    Source: http://blog.grafixartist.com/pinterest-masonry-layout-staggered-grid/
    */

    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private final int mSpace;
        public SpacesItemDecoration(int space) {
            this.mSpace = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.left = mSpace;
            outRect.right = mSpace;
            outRect.bottom = mSpace;
            // Add top margin only for the first item to avoid double space between items
            if (parent.getChildAdapterPosition(view) == 0)
                outRect.top = mSpace;
        }
    }
}
