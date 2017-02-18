package com.jayodeji.android.popularmovies.loaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.jayodeji.android.popularmovies.MovieDetailActivity;
import com.jayodeji.android.popularmovies.data.Movie;
import com.jayodeji.android.popularmovies.moviedbutils.Request;

/**
 * Created by joshuaadeyemi on 2/18/17.
 */

public class FetchMovieDetailTaskLoader extends AsyncTaskLoader<Movie> {

    public static final String TAG = FetchMovieDetailTaskLoader.class.getSimpleName();

    public static final String MOVIE_ID_EXTRA_KEY = "movie_id_key";

    private MovieDetailActivity mActivityContext;
    private Movie mMovie = null;
    private int mMovieId;

    public FetchMovieDetailTaskLoader(Context context, int movieId) {
        super(context);
        mMovieId = movieId;
        mActivityContext = (MovieDetailActivity) context;
    }

    /**
     * If movie exists and this is the same movie as the movie that is being requested
     */
    @Override
    protected void onStartLoading() {
        if (mMovie != null && (mMovie.movieId == mMovieId)) {
            deliverResult(mMovie);
        } else {
            mActivityContext.showLoading();
            forceLoad();
        }
    }

    @Override
    public Movie loadInBackground() {
        Movie movie = null;
        if (mMovieId > 0) {
            movie = Request.getMovieDetail(getContext(), mMovieId);
        }
        return movie;
    }

    @Override
    public void deliverResult(Movie data) {
        mMovie = data;
        super.deliverResult(data);
    }
}
