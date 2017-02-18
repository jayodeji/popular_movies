package com.jayodeji.android.popularmovies.loaders;

import android.support.v4.content.AsyncTaskLoader;

import com.jayodeji.android.popularmovies.MainActivity;
import com.jayodeji.android.popularmovies.data.MoviePoster;
import com.jayodeji.android.popularmovies.moviedbutils.Request;

/**
 * Created by joshuaadeyemi on 2/15/17.
 */

public class FetchMovieListTaskLoader extends AsyncTaskLoader<MoviePoster[]> {

    public static final String MOVIE_TYPE_KEY = "movie_path_key";

    private static final String TAG = FetchMovieListTaskLoader.class.getSimpleName();

    private MainActivity mActivityContext;

    private MoviePoster[] mMovieList = null;
    String mMovieListType = "";

    public FetchMovieListTaskLoader(MainActivity context, String movieListType) {
        super(context);
        mActivityContext = context;
        mMovieListType = movieListType;

    }

    @Override
    protected void onStartLoading() {
        if (mMovieList != null) {
            deliverResult(mMovieList);
        } else {
            mActivityContext.showLoading();
            forceLoad();
        }
    }

    @Override
    public MoviePoster[] loadInBackground() {
        MoviePoster[] results = null;
        if (mMovieListType.length() > 0) {
            results = Request.getMovieListByType(getContext(), mMovieListType);
        }
        return results;
    }

    @Override
    public void deliverResult(MoviePoster[] data) {
        mMovieList = data;
        super.deliverResult(data);
    }
}