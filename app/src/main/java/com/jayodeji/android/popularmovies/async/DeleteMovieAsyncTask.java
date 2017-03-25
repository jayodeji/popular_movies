package com.jayodeji.android.popularmovies.async;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.jayodeji.android.popularmovies.MovieDetailActivity;
import com.jayodeji.android.popularmovies.data.Movie;
import com.jayodeji.android.popularmovies.dbcontract.MovieContract;

/**
 * Created by joshuaadeyemi on 3/24/17.
 */

public class DeleteMovieAsyncTask extends AsyncTask<Movie, Void, Movie> {

    private static final String TAG = DeleteMovieAsyncTask.class.getSimpleName();

    private MovieDetailActivity mContext;

    public DeleteMovieAsyncTask(MovieDetailActivity context) {
        mContext = context;
    }

    @Override
    protected Movie doInBackground(Movie... params) {
        if (params.length > 0) {
            Movie movie = params[0];
            ContentResolver resolver = mContext.getContentResolver();
            Uri uri = ContentUris.withAppendedId(MovieContract.MovieEntry.CONTENT_URI, movie.movieId);
            int deleted = resolver.delete(uri, null, null);
            if (deleted > 0) {
                Log.v(TAG, "Deleted favorite movie: " + movie.movieId);

                //create new movie object without internal movie id
                Movie.Builder builder = new Movie.Builder();
                builder.movieId(movie.movieId)
                        .title(movie.originalTitle)
                        .thumbnailUrl(movie.thumbnailUrl)
                        .posterUrl(movie.posterUrl)
                        .releaseDate(movie.releaseDate)
                        .runtime(movie.runtime)
                        .rating(movie.userRating)
                        .overview(movie.movieSynopsis)
                        .trailers(movie.trailers)
                        .reviews(movie.reviews);
                return builder.build();
            }

            Log.v(TAG, "Could not delete favorite movie: " + movie.movieId);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Movie movie) {
        if (movie != null) {
            mContext.setMovie(movie);
            mContext.bindMarkAsFavoriteData(movie);
        }
    }
}