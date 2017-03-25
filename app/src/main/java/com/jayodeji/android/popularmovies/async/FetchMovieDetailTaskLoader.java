package com.jayodeji.android.popularmovies.async;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.AsyncTaskLoader;

import com.jayodeji.android.popularmovies.MovieDetailActivity;
import com.jayodeji.android.popularmovies.data.Movie;
import com.jayodeji.android.popularmovies.data.Review;
import com.jayodeji.android.popularmovies.data.Trailer;
import com.jayodeji.android.popularmovies.dbcontract.MovieContract;
import com.jayodeji.android.popularmovies.moviedbutils.Request;

/**
 * Created by joshuaadeyemi on 2/18/17.
 */

public class FetchMovieDetailTaskLoader extends AsyncTaskLoader<Movie> {

    public static final String TAG = FetchMovieDetailTaskLoader.class.getSimpleName();

    public static final String MOVIE_ID_EXTRA_KEY = "movie_id_key";

    public static final String[] MOVIE_DETAIL_PROJECTION = {
            MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_THUMBNAIL_URL,
            MovieContract.MovieEntry.COLUMN_POSTER_URL,
            MovieContract.MovieEntry.COLUMN_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_RATING,
            MovieContract.MovieEntry.COLUMN_RELEASE_YEAR,
            MovieContract.MovieEntry.COLUMN_RUNTIME
    };

    public static final int INDEX_INTERNAL_MOVIE_ID = 0;
    public static final int INDEX_TITLE = 1;
    public static final int INDEX_THUMBNAIL_URL = 2;
    public static final int INDEX_POSTER_URL = 3;
    public static final int INDEX_OVERVIEW = 4;
    public static final int INDEX_RATING = 5;
    public static final int INDEX_RELEASE_YEAR = 6;
    public static final int INDEX_RUNTIME = 7;

    public static final String[] MOVIE_TRAILER_PROJECTION = {
            MovieContract.TrailerEntry.COLUMN_NAME,
            MovieContract.TrailerEntry.COLUMN_KEY,
            MovieContract.TrailerEntry.COLUMN_URL
    };

    public static final int INDEX_COLUMN_TRAILER_NAME = 0;
    public static final int INDEX_COLUMN_TRAILER_KEY = 1;
    public static final int INDEX_COLUMN_TRAILER_URL = 2;

    public static final String[] MOVIE_REVIEW_PROJECTION = {
            MovieContract.ReviewEntry.COLUMN_REVIEW_ID,
            MovieContract.ReviewEntry.COLUMN_AUTHOR,
            MovieContract.ReviewEntry.COLUMN_URL
    };

    public static final int INDEX_COLUMN_REVIEW_ID = 0;
    public static final int INDEX_COLUMN_REVIEW_AUTHOR = 1;
    public static final int INDEX_COLUMN_REVIEW_URL = 2;

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

    /**
     * When loading movie detail, try to load the movie from
     * the database. If the movie exists, use it, otherwise
     * load the movie from the network
     * @return
     */
    @Override
    public Movie loadInBackground() {
        Movie movie = null;
        if (mMovieId > 0) {
            movie = fetchMovieFromDatabase();
            if (movie == null) {
                movie = Request.getMovieDetail(getContext(), mMovieId);
            }
        }
        return movie;
    }

    @Override
    public void deliverResult(Movie data) {
        mMovie = data;
        super.deliverResult(data);
    }

    private Movie fetchMovieFromDatabase() {
        ContentResolver contentResolver = getContext().getContentResolver();
        Uri uri = ContentUris.withAppendedId(MovieContract.MovieEntry.CONTENT_URI, mMovieId);
        Cursor movieCursor = contentResolver.query(uri, MOVIE_DETAIL_PROJECTION, null, null, null);
        if (movieCursor != null && movieCursor.moveToFirst()) {
            Movie.Builder builder = new Movie.Builder();
            builder.movieId(mMovieId)
                    .internalId(movieCursor.getLong(INDEX_INTERNAL_MOVIE_ID))
                    .title(movieCursor.getString(INDEX_TITLE))
                    .thumbnailUrl(movieCursor.getString(INDEX_THUMBNAIL_URL))
                    .posterUrl(movieCursor.getString(INDEX_POSTER_URL))
                    .releaseDate(movieCursor.getString(INDEX_RELEASE_YEAR))
                    .runtime(movieCursor.getInt(INDEX_RUNTIME))
                    .rating(movieCursor.getString(INDEX_RATING))
                    .overview(movieCursor.getString(INDEX_OVERVIEW))
                    .trailers(getTrailersFromContentProvider())
                    .reviews(getReviewsFromContentProvider());
            return builder.build();
        }
        return null;
    }

    private Trailer[] getTrailersFromContentProvider() {
        Cursor cursor = getContext().getContentResolver().query(
                MovieContract.TrailerEntry.CONTENT_URI,
                MOVIE_TRAILER_PROJECTION,
                MovieContract.TrailerEntry.COLUMN_EXTERNAL_MOVIE_ID + " = ?",
                new String[]{String.valueOf(mMovieId)},
                MovieContract.TrailerEntry.COLUMN_KEY + " ASC"
        );
        Trailer[] trailers = new Trailer[cursor.getCount()];
        int index = 0;
        while (cursor.moveToNext()) {
            Trailer.Builder builder = new Trailer.Builder();
            builder.key(cursor.getString(INDEX_COLUMN_TRAILER_KEY))
                    .name(cursor.getString(INDEX_COLUMN_TRAILER_NAME))
                    .url(cursor.getString(INDEX_COLUMN_TRAILER_URL));
            trailers[index] = builder.build();
            index++;
        }
        return trailers;
    }

    private Review[] getReviewsFromContentProvider() {
        Cursor cursor = getContext().getContentResolver().query(
                MovieContract.ReviewEntry.CONTENT_URI,
                MOVIE_REVIEW_PROJECTION,
                MovieContract.TrailerEntry.COLUMN_EXTERNAL_MOVIE_ID + " = ?",
                new String[]{String.valueOf(mMovieId)},
                MovieContract.ReviewEntry.COLUMN_REVIEW_ID + " ASC"
        );
        Review[] reviews = new Review[cursor.getCount()];
        int index = 0;
        while (cursor.moveToNext()) {
            Review.Builder builder = new Review.Builder();
            builder.author(cursor.getString(INDEX_COLUMN_REVIEW_AUTHOR))
                    .reviewId(cursor.getString(INDEX_COLUMN_REVIEW_ID))
                    .url(cursor.getString(INDEX_COLUMN_REVIEW_URL));
            reviews[index] = builder.build();
            index++;
        }
        return reviews;
    }

}