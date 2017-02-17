package com.jayodeji.android.popularmovies;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.jayodeji.android.popularmovies.databinding.ActivityMovieDetailBinding;
import com.squareup.picasso.Picasso;

public class MovieDetailActivity extends AppCompatActivity {

    public static final String EXTRA_MOVIE = MovieDetailActivity.class.getSimpleName() + ".MOVIE";

    private ActivityMovieDetailBinding mMovieDetailBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMovieDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_movie_detail);

        Intent intentThatStartedActivity = getIntent();
        if (intentThatStartedActivity.hasExtra(EXTRA_MOVIE)) {
            Movie movie = intentThatStartedActivity.getParcelableExtra(EXTRA_MOVIE);
            bindDataToViews(movie);
        }
    }

    protected void bindDataToViews(Movie movie) {
        mMovieDetailBinding.tvMovieTitle.setText(movie.originalTitle);
        mMovieDetailBinding.tvReleaseDate.setText(movie.releaseDate);
        mMovieDetailBinding.tvMovieRating.setText(movie.userRating);
        mMovieDetailBinding.tvMovieOverview.setText(movie.movieSynopsis);
        Picasso.with(this)
                .load(movie.thumbnailUrl)
                .error(R.drawable.placeholder)
                .into(mMovieDetailBinding.ivMovieThumbnail);
    }
}
