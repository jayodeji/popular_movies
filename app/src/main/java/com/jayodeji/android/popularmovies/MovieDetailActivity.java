package com.jayodeji.android.popularmovies;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.jayodeji.android.popularmovies.async.AddMovieSyncTask;
import com.jayodeji.android.popularmovies.async.DeleteMovieAsyncTask;
import com.jayodeji.android.popularmovies.data.Movie;
import com.jayodeji.android.popularmovies.data.Review;
import com.jayodeji.android.popularmovies.data.Trailer;
import com.jayodeji.android.popularmovies.databinding.ActivityMovieDetailBinding;
import com.jayodeji.android.popularmovies.dbcontract.MovieContract;
import com.jayodeji.android.popularmovies.async.FetchMovieDetailTaskLoader;
import com.squareup.picasso.Picasso;

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
                mMovieDetailBinding.tvMovieTitle.setText(movieTitle);
            }
        }
    }
    private void loadMovieDetail(int movieId) {
        Bundle loaderBundle = new Bundle();
        loaderBundle.putInt(FetchMovieDetailTaskLoader.MOVIE_ID_EXTRA_KEY, movieId);
        getSupportLoaderManager().initLoader(MOVIE_DETAIL_LOADER_ID, loaderBundle, this);
    }

    private void bindDataToViews(Movie movie) {
        mMovieDetailBinding.tvMovieTitle.setText(movie.originalTitle);
        mMovieDetailBinding.tvReleaseDate.setText(movie.releaseDate);
        mMovieDetailBinding.tvMovieRating.setText(movie.userRating);
        mMovieDetailBinding.tvMovieOverview.setText(movie.movieSynopsis);
        mMovieDetailBinding.tvRuntime.setText(movie.runtime + "min");

        Picasso.with(this)
                .load(movie.thumbnailUrl)
                .error(R.drawable.placeholder)
                .into(mMovieDetailBinding.ivMovieThumbnail);

        //add the trailers as an adapter
        TrailerListAdapter trailerListAdapter = new TrailerListAdapter(this, movie.trailers);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mMovieDetailBinding.rvMovieTrailers.setLayoutManager(layoutManager);
        mMovieDetailBinding.rvMovieTrailers.setHasFixedSize(true);
        mMovieDetailBinding.rvMovieTrailers.setAdapter(trailerListAdapter);

        //add reviews as an adapter
        ReviewListAdapter reviewListAdapter = new ReviewListAdapter(this, movie.reviews);
        LinearLayoutManager reviewLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mMovieDetailBinding.rvMovieReviews.setLayoutManager(reviewLayoutManager);
        mMovieDetailBinding.rvMovieReviews.setHasFixedSize(true);
        mMovieDetailBinding.rvMovieReviews.setAdapter(reviewListAdapter);

        //mark as favorite or not
        bindMarkAsFavoriteData(movie);
    }

    public void bindMarkAsFavoriteData(Movie movie) {
        //mark as favorite or remove as favorite
        String markAsFavText = getString(R.string.mark_as_favorite);
        if (movie.internalId > 0) {
            markAsFavText = getString(R.string.remove_as_favorite);
        }
        mMovieDetailBinding.tvMarkFavorite.setText(markAsFavText);
        //remove onclick listener before re-adding it
        mMovieDetailBinding.tvMarkFavorite.setOnClickListener(null);
        mMovieDetailBinding.tvMarkFavorite.setOnClickListener(this);
    }

    private void showMovieDetail() {
        mMovieDetailBinding.tvErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mMovieDetailBinding.pbLoadingIndicator.setVisibility(View.INVISIBLE);
        mMovieDetailBinding.svMovieDetail.setVisibility(View.VISIBLE);
    }

    private void showLoadingError() {
        mMovieDetailBinding.tvErrorMessageDisplay.setVisibility(View.VISIBLE);
        mMovieDetailBinding.svMovieDetail.setVisibility(View.INVISIBLE);
        mMovieDetailBinding.pbLoadingIndicator.setVisibility(View.INVISIBLE);
    }

    public void showLoading() {
        mMovieDetailBinding.tvErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mMovieDetailBinding.svMovieDetail.setVisibility(View.INVISIBLE);
        mMovieDetailBinding.pbLoadingIndicator.setVisibility(View.VISIBLE);
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