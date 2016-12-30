package com.jayodeji.android.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class MovieDetailActivity extends AppCompatActivity {

    public static final String EXTRA_MOVIE = MovieDetailActivity.class.getSimpleName() + ".MOVIE";

    private TextView mMovieTitle;
    private TextView mMovieReleaseDate;
    private TextView mMovieRating;
    private TextView mMovieOverview;

    private ImageView mMovieThumbnail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        mMovieTitle = (TextView) findViewById(R.id.tv_movie_title);
        mMovieReleaseDate = (TextView) findViewById(R.id.tv_release_date);
        mMovieRating = (TextView) findViewById(R.id.tv_movie_rating);
        mMovieOverview = (TextView) findViewById(R.id.tv_movie_overview);

        mMovieThumbnail = (ImageView) findViewById(R.id.iv_movie_thumbnail);

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

        Picasso.with(this).load(movie.thumbnailUrl).into(mMovieThumbnail);
    }
}
