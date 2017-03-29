package com.jayodeji.android.popularmovies;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jayodeji.android.popularmovies.async.AddMovieSyncTask;
import com.jayodeji.android.popularmovies.async.DeleteMovieAsyncTask;
import com.jayodeji.android.popularmovies.data.Movie;
import com.jayodeji.android.popularmovies.data.Review;
import com.jayodeji.android.popularmovies.data.Trailer;
import com.jayodeji.android.popularmovies.async.FetchMovieDetailTaskLoader;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

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

    private Movie mMovie = null;
    private int mMovieId;

    //Views to bind
    @BindView(R.id.movie_title) protected TextView mMovieTitle;
    @BindView(R.id.rating) protected TextView mRating;
    @BindView(R.id.release_date) protected TextView mReleaseDate;
    @BindView(R.id.runtime) protected TextView mRuntime;
    @BindView(R.id.overview) protected TextView mOverview;
    @BindView(R.id.load_error) protected TextView mLoadError;
    @BindView(R.id.trailer_caption) protected TextView mMovieTrailerCaption;
    @BindView(R.id.review_caption) protected TextView mMovieReviewCaption;

    @BindView(R.id.progress_bar) protected ProgressBar mLoadingIndicator;

    @BindView(R.id.star) protected ImageView mFavoriteStar;
    @BindView(R.id.thumbnail) protected ImageView mThumbnail;

    @BindView(R.id.movie_reviews) protected RecyclerView mMovieReviews;
    @BindView(R.id.movie_trailers) protected RecyclerView mMovieTrailers;

    @BindView(R.id.movie_detail) protected FrameLayout mMovieDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        ButterKnife.bind(this);

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
                mMovieTitle.setText(movieTitle);
            }
        }
    }
    private void loadMovieDetail(int movieId) {
        Bundle loaderBundle = new Bundle();
        loaderBundle.putInt(FetchMovieDetailTaskLoader.MOVIE_ID_EXTRA_KEY, movieId);
        getSupportLoaderManager().initLoader(MOVIE_DETAIL_LOADER_ID, loaderBundle, this);
    }


    private void bindDataToViews(Movie movie) {
        mMovieTitle.setText(movie.originalTitle);
        mReleaseDate.setText(movie.releaseDate);

        mRating.setText(movie.userRating);
        mRuntime.setText(movie.runtime + "min");

        Picasso.with(this)
                .load(movie.thumbnailUrl)
                .error(R.drawable.placeholder)
                .into(mThumbnail);

        String thumbnailContentDescription = "Thumbnail for " + movie.originalTitle;
        mThumbnail.setContentDescription(thumbnailContentDescription);

        //mark as favorite or not
        bindMarkAsFavoriteData(movie);

        mOverview.setText(movie.movieSynopsis);

        mMovieTrailers.setAdapter(new TrailerListAdapter(this, movie.trailers));
        mMovieTrailers.setHasFixedSize(true);
        mMovieTrailers.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        );
        if (movie.trailers == null || movie.trailers.length == 0) {
            mMovieTrailerCaption.setVisibility(View.INVISIBLE);
        }

        //add reviews as an adapter
        mMovieReviews.setAdapter(new ReviewListAdapter(this, movie.reviews));
        mMovieReviews.setHasFixedSize(true);
        mMovieReviews.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        );
        if (movie.reviews == null || movie.reviews.length == 0) {
            mMovieReviewCaption.setVisibility(View.INVISIBLE);
        }
        Log.v(TAG, "Num Trailers: " + movie.trailers.length);
        Log.v(TAG, "Num Reviews: " + movie.reviews.length);
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
        mFavoriteStar.setContentDescription(imageDescription);
        mFavoriteStar.setImageDrawable(image);

        //remove onclick listener before re-adding it
        mFavoriteStar.setOnClickListener(null);
        mFavoriteStar.setOnClickListener(this);
    }

    private void showMovieDetail() {
        mLoadError.setVisibility(View.INVISIBLE);
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mMovieDetails.setVisibility(View.VISIBLE);
    }

    private void showLoadingError() {
        mLoadError.setVisibility(View.VISIBLE);
        mMovieDetails.setVisibility(View.INVISIBLE);
        mLoadingIndicator.setVisibility(View.INVISIBLE);
    }

    public void showLoading() {
        mLoadError.setVisibility(View.INVISIBLE);
        mMovieDetails.setVisibility(View.INVISIBLE);
        mLoadingIndicator.setVisibility(View.VISIBLE);
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
            showLoadingError();
        } else {
            setMovie(data);
            bindDataToViews(data);
            showMovieDetail();
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