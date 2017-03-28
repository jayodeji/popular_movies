package com.jayodeji.android.popularmovies;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.jayodeji.android.popularmovies.async.AddMovieSyncTask;
import com.jayodeji.android.popularmovies.async.DeleteMovieAsyncTask;
import com.jayodeji.android.popularmovies.data.Movie;
import com.jayodeji.android.popularmovies.data.Review;
import com.jayodeji.android.popularmovies.data.Trailer;
import com.jayodeji.android.popularmovies.databinding.ActivityMovieDetailBinding;
import com.jayodeji.android.popularmovies.async.FetchMovieDetailTaskLoader;
import com.squareup.picasso.Picasso;

//TODO Try to make scrollview go up
//TODO Add ability to share movie detail information
//TODO Different layout based on orientation and screen size
public class MovieDetailActivity extends AppCompatActivity implements
        TrailerListAdapter.TrailerClickListener,
        ReviewListAdapter.ReviewClickListener,
        LoaderManager.LoaderCallbacks<Movie>,
        View.OnClickListener {

    private static final String TAG = MovieDetailActivity.class.getSimpleName();

    public static final String EXTRA_MOVIE = MovieDetailActivity.class.getSimpleName() + ".MOVIE";
    public static final String EXTRA_MOVIE_ID = MovieDetailActivity.class.getSimpleName() + ".MOVIE_ID";
    public static final String EXTRA_MOVIE_TITLE = MovieDetailActivity.class.getSimpleName() + ".MOVIE_TITLE";

    private static final int MOVIE_DETAIL_LOADER_ID = 1;

    private ActivityMovieDetailBinding mMovieDetailBinding;

    private Movie mMovie = null;
    private int mMovieId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMovieDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_movie_detail);

        if (savedInstanceState != null) {
            mMovie = (Movie) savedInstanceState.getParcelable(EXTRA_MOVIE);
        }

        if (mMovie != null) {
            bindDataToViews(mMovie);
        } else {
            mMovieId = getIntent().getIntExtra(EXTRA_MOVIE_ID, 0);
            bindMovieTitleFromIntent();
            loadMovieDetail(mMovieId);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mMovie != null) {
            outState.putParcelable(EXTRA_MOVIE, mMovie);
        }
    }

    private void bindMovieTitleFromIntent() {
        Intent intentThatStartedActivity = getIntent();
        if (intentThatStartedActivity.hasExtra(EXTRA_MOVIE_TITLE)) {
            String movieTitle = intentThatStartedActivity.getStringExtra(EXTRA_MOVIE_TITLE);
            if (!TextUtils.isEmpty(movieTitle)) {
                mMovieDetailBinding.movieTitle.setText(movieTitle);
            }
        }
    }
    private void loadMovieDetail(int movieId) {
        Bundle loaderBundle = new Bundle();
        loaderBundle.putInt(FetchMovieDetailTaskLoader.MOVIE_ID_EXTRA_KEY, movieId);
        getSupportLoaderManager().initLoader(MOVIE_DETAIL_LOADER_ID, loaderBundle, this);
    }

    private void bindDataToViews(Movie movie) {
        mMovieDetailBinding.movieTitle.setText(movie.originalTitle);
        mMovieDetailBinding.metaInfo.releaseDate.setText(movie.releaseDate);

        mMovieDetailBinding.metaInfo.rating.setText(movie.userRating);
        mMovieDetailBinding.metaInfo.runtime.setText(movie.runtime + "min");

        Picasso.with(this)
                .load(movie.thumbnailUrl)
                .error(R.drawable.placeholder)
                .into(mMovieDetailBinding.metaInfo.thumbnail);

        String thumbnailContentDescription = "Thumbnail for " + movie.originalTitle;
        mMovieDetailBinding.metaInfo.thumbnail.setContentDescription(thumbnailContentDescription);

        //mark as favorite or not
        bindMarkAsFavoriteData(movie);

        mMovieDetailBinding.extraInfo.overview.setText(movie.movieSynopsis);

        //add the trailers as an adapter
        mMovieDetailBinding.extraInfo.movieTrailers.setAdapter(new TrailerListAdapter(this, movie.trailers));
        mMovieDetailBinding.extraInfo.movieTrailers.setHasFixedSize(true);
        mMovieDetailBinding.extraInfo.movieTrailers.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        );
        if (movie.trailers == null || movie.trailers.length == 0) {
            mMovieDetailBinding.extraInfo.trailerCaption.setVisibility(View.INVISIBLE);
        }

        //add reviews as an adapter
        mMovieDetailBinding.extraInfo.movieReviews.setAdapter(new ReviewListAdapter(this, movie.reviews));
        mMovieDetailBinding.extraInfo.movieReviews.setHasFixedSize(true);
        mMovieDetailBinding.extraInfo.movieReviews.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        );
        Log.v(TAG, "Review length: " + movie.reviews.length);
        if (movie.reviews == null || movie.reviews.length == 0) {
            mMovieDetailBinding.extraInfo.reviewCaption.setVisibility(View.INVISIBLE);
        }
    }

    public void bindMarkAsFavoriteData(Movie movie) {
        String imageDescription;
        int color;
        if (movie.internalId > 0) {
            imageDescription = getString(R.string.remove_as_favorite);
            color = ContextCompat.getColor(this, R.color.colorStarSelectedTint);
        } else {
            imageDescription = getString(R.string.mark_as_favorite);
            color = ContextCompat.getColor(this, R.color.colorStarUnselectedTint);
        }

        Drawable image = ContextCompat.getDrawable(this, R.drawable.ic_star);
        DrawableCompat.setTint(image, color);
        mMovieDetailBinding.metaInfo.star.setContentDescription(imageDescription);
        mMovieDetailBinding.metaInfo.star.setImageDrawable(image);

        //remove onclick listener before re-adding it
        mMovieDetailBinding.metaInfo.star.setOnClickListener(null);
        mMovieDetailBinding.metaInfo.star.setOnClickListener(this);
    }

    private void showMovieDetail() {
        mMovieDetailBinding.errorMessageDisplay.setVisibility(View.INVISIBLE);
        mMovieDetailBinding.loadingIndicator.setVisibility(View.INVISIBLE);
        mMovieDetailBinding.movieDetail.setVisibility(View.VISIBLE);
    }

    private void showLoadingError() {
        mMovieDetailBinding.errorMessageDisplay.setVisibility(View.VISIBLE);
        mMovieDetailBinding.movieDetail.setVisibility(View.INVISIBLE);
        mMovieDetailBinding.loadingIndicator.setVisibility(View.INVISIBLE);
    }

    public void showLoading() {
        mMovieDetailBinding.errorMessageDisplay.setVisibility(View.INVISIBLE);
        mMovieDetailBinding.movieDetail.setVisibility(View.INVISIBLE);
        mMovieDetailBinding.loadingIndicator.setVisibility(View.VISIBLE);
    }

    @Override
    public Loader<Movie> onCreateLoader(int id, Bundle args) {
        int movieId = 0;
        if (args != null) {
            movieId = args.getInt(FetchMovieDetailTaskLoader.MOVIE_ID_EXTRA_KEY, 0);
        }
        return new FetchMovieDetailTaskLoader(this, movieId);
    }

    @Override
    public void onLoadFinished(Loader<Movie> loader, Movie data) {
        if (data == null) {
            this.showLoadingError();
        } else {
            setMovie(data);
            bindDataToViews(data);
            this.showMovieDetail();
        }
    }

    public void setMovie(Movie movie) {
        mMovie = movie;
    }

    @Override
    public void onLoaderReset(Loader<Movie> loader) {

    }

    /**
     * Use intents to play a youtube video
     * @param clickedTrailer
     */
    @Override
    public void onTrailerClick(Trailer clickedTrailer) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri youtubeUri = Uri.parse(clickedTrailer.url);
        intent.setData(youtubeUri);

        if (intent.resolveActivity(getPackageManager()) != null) {
            Log.v(TAG, "Called " + youtubeUri.toString());
            startActivity(intent);
        } else {
            Log.d(TAG, "Couldn't call " + youtubeUri.toString() + ", no receiving apps installed");
        }
    }

    @Override
    public void onReviewClicked(Review clickedReview) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri reviewUri = Uri.parse(clickedReview.url);
        intent.setData(reviewUri);

        if (intent.resolveActivity(getPackageManager()) != null) {
            Log.v(TAG, "Called " + reviewUri.toString());
            startActivity(intent);
        } else {
            Log.d(TAG, "Couldn't call " + reviewUri.toString() + ", no receicing apps installed");
        }
    }

    /**
     * If mark as favorite or unmark as favorite is clicked
     * @param v
     */
    @Override
    public void onClick(View v) {
        //If the movie id exists, then delete otherwise save
        if (mMovie.internalId > 0) {
            new DeleteMovieAsyncTask(this).execute(mMovie);
        } else {
            new AddMovieSyncTask(this).execute(mMovie);
        }
    }
}