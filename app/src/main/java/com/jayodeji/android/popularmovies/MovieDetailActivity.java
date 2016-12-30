package com.jayodeji.android.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieDetailActivity extends AppCompatActivity {

    public static final String EXTRA_MOVIE = MovieDetailActivity.class.getSimpleName() + ".MOVIE";

    @BindView(R.id.tv_movie_title) protected TextView mMovieTitle;
    @BindView(R.id.tv_release_date) protected TextView mMovieReleaseDate;
    @BindView(R.id.tv_movie_rating) protected TextView mMovieRating;
    @BindView(R.id.tv_movie_overview) protected TextView mMovieOverview;

    @BindView(R.id.iv_movie_thumbnail) protected ImageView mMovieThumbnail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        ButterKnife.bind(this);

        Intent intentThatStartedActivity = getIntent();
        if (intentThatStartedActivity.hasExtra(EXTRA_MOVIE)) {
            Movie movie = intentThatStartedActivity.getParcelableExtra(EXTRA_MOVIE);
            bindDataToViews(movie);
        }

    }

    protected void bindDataToViews(Movie movie) {
        mMovieTitle.setText(movie.originalTitle);
        mMovieReleaseDate.setText(movie.releaseDate);
        mMovieRating.setText(movie.userRating);
        mMovieOverview.setText(movie.movieSynopsis);

        Picasso.with(this)
                .load(movie.thumbnailUrl)
                .error(R.drawable.placeholder)
                .into(mMovieThumbnail);
    }
}
