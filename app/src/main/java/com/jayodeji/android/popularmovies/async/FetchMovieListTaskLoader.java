package com.jayodeji.android.popularmovies.async;

import android.database.Cursor;
import android.support.v4.content.AsyncTaskLoader;
import android.text.TextUtils;

import com.jayodeji.android.popularmovies.MainActivity;
import com.jayodeji.android.popularmovies.data.MoviePoster;
import com.jayodeji.android.popularmovies.dbcontract.MovieContract;
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
        if (!TextUtils.isEmpty(mMovieListType)) {
            if (mMovieListType.equals(MainActivity.FAVORITES_MOVIE_TYPE)) {
                results = fetchMoviePosterFromContentProvider();
            } else {
                results = Request.getMovieListByType(getContext(), mMovieListType);
            }
        }
        return results;
    }

    @Override
    public void deliverResult(MoviePoster[] data) {
        mMovieList = data;
        super.deliverResult(data);
    }

    private MoviePoster[] fetchMoviePosterFromContentProvider() {
        Cursor cursor = getContext().getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        MoviePoster[] moviePosters = new MoviePoster[cursor.getCount()];
        int index = 0;
        while (cursor.moveToNext()) {
            int externalMovieIdIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_EXTERNAL_MOVIE_ID);
            int posterUrlIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_URL);
            int titleIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE);

            MoviePoster.Builder builder = new MoviePoster.Builder();
            builder.movieId(cursor.getInt(externalMovieIdIndex))
                    .posterUrl(cursor.getString(posterUrlIndex))
                    .title(cursor.getString(titleIndex));
            moviePosters[index] = builder.build();
            index++;
        }
        return moviePosters;
    }
}