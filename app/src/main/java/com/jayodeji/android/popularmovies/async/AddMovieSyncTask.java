package com.jayodeji.android.popularmovies.async;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.jayodeji.android.popularmovies.MovieDetailActivity;
import com.jayodeji.android.popularmovies.data.Movie;
import com.jayodeji.android.popularmovies.data.Review;
import com.jayodeji.android.popularmovies.data.Trailer;
import com.jayodeji.android.popularmovies.dbcontract.MovieContract;

/**
 * Created by joshuaadeyemi on 3/24/17.
 */

public class AddMovieSyncTask extends AsyncTask<Movie, Void, Movie> {

    private static final String TAG = AddMovieSyncTask.class.getSimpleName();

    private MovieDetailActivity mContext;

    public AddMovieSyncTask(MovieDetailActivity context) {
        mContext = context;
    }

    @Override
    protected Movie doInBackground(Movie... params) {
        if (params.length > 0) {
            Movie movie = params[0];
            Uri movieUri = addFavorite(movie);
            if (movieUri != null) {
                Log.v(TAG, "Saved favorite, movie uri is: " + movieUri);
                //create new movie object with internal movie id
                Movie.Builder builder = new Movie.Builder();
                builder.internalId(Long.parseLong(movieUri.getLastPathSegment()))
                        .movieId(movie.movieId)
                        .title(movie.originalTitle)
                        .posterUrl(movie.posterUrl)
                        .thumbnailUrl(movie.thumbnailUrl)
                        .releaseDate(movie.releaseDate)
                        .runtime(movie.runtime)
                        .rating(movie.userRating)
                        .overview(movie.movieSynopsis)
                        .trailers(movie.trailers)
                        .reviews(movie.reviews);
                return builder.build();
            }
            Log.v(TAG, "Could not save movie as favorite with id: " + movie.movieId);
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

    private Uri addFavorite(Movie movie) {
        ContentValues movieContent = new ContentValues();
        ContentValues[] trailers = new ContentValues[movie.trailers.length];
        ContentValues[] reviews = new ContentValues[movie.reviews.length];

        movieContent.put(MovieContract.MovieEntry.COLUMN_EXTERNAL_MOVIE_ID, movie.movieId);
        movieContent.put(MovieContract.MovieEntry.COLUMN_TITLE, movie.originalTitle);
        movieContent.put(MovieContract.MovieEntry.COLUMN_POSTER_URL, movie.posterUrl);
        movieContent.put(MovieContract.MovieEntry.COLUMN_THUMBNAIL_URL, movie.thumbnailUrl);
        movieContent.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, movie.movieSynopsis);
        movieContent.put(MovieContract.MovieEntry.COLUMN_RATING, movie.userRating);
        movieContent.put(MovieContract.MovieEntry.COLUMN_RELEASE_YEAR, movie.releaseDate);
        movieContent.put(MovieContract.MovieEntry.COLUMN_RUNTIME, movie.runtime);

        for (int ii=0; ii<movie.trailers.length; ii++) {
            Trailer trailer = movie.trailers[ii];
            ContentValues trailerContent = new ContentValues();
            trailerContent.put(MovieContract.TrailerEntry.COLUMN_EXTERNAL_MOVIE_ID, movie.movieId);
            trailerContent.put(MovieContract.TrailerEntry.COLUMN_NAME, trailer.name);
            trailerContent.put(MovieContract.TrailerEntry.COLUMN_KEY, trailer.key);
            trailerContent.put(MovieContract.TrailerEntry.COLUMN_URL, trailer.url);
            trailers[ii] = trailerContent;
        }

        for (int ii=0; ii<movie.reviews.length; ii++) {
            Review review = movie.reviews[ii];
            ContentValues reviewContent = new ContentValues();
            reviewContent.put(MovieContract.ReviewEntry.COLUMN_EXTERNAL_MOVIE_ID, movie.movieId);
            reviewContent.put(MovieContract.ReviewEntry.COLUMN_REVIEW_ID, review.reviewId);
            reviewContent.put(MovieContract.ReviewEntry.COLUMN_AUTHOR, review.author);
            reviewContent.put(MovieContract.ReviewEntry.COLUMN_URL, review.url);
            reviews[ii] = reviewContent;
        }

        // Do the saving
        ContentResolver contentResolver = mContext.getContentResolver();
        Uri movieUri = contentResolver.insert(MovieContract.MovieEntry.CONTENT_URI, movieContent);
        if (movieUri != null) {
            contentResolver.bulkInsert(MovieContract.TrailerEntry.CONTENT_URI, trailers);
            contentResolver.bulkInsert(MovieContract.ReviewEntry.CONTENT_URI, reviews);
        }
        return movieUri;
    }
}
