package com.jayodeji.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by joshuaadeyemi on 12/28/16.
 */

public class MovieGridAdapter extends RecyclerView.Adapter<MovieGridAdapter.MoviePosterViewHolder> {

    private static final String TAG = MovieGridAdapter.class.getSimpleName();

    private Movie[] mMovieList = null;

    private final MoviePosterClickListener mOnMoviePosterClickListener;

    public MovieGridAdapter(MoviePosterClickListener listener) {
        mOnMoviePosterClickListener = listener;
    }

    @Override
    public MoviePosterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        boolean attachImmediatelyToParent = false;
        View view = inflater.inflate(R.layout.movie_thumbnail_item, parent, attachImmediatelyToParent);
        return new MoviePosterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MoviePosterViewHolder holder, int position) {
        if (mMovieList != null && (mMovieList.length > position)) {
            Movie movie = mMovieList[position];

            Context context = holder.mMoviePosterImageView.getContext();
            Picasso.with(context).load(movie.posterUrl).into(holder.mMoviePosterImageView);

            Log.v(TAG, "Bound url to view: " + movie.posterUrl);
        } else {
            Log.v(TAG, "Cannot get movie with position: " + position);
        }
    }


    @Override
    public int getItemCount() {
        if (mMovieList == null) {
            return 0;
        }
        return mMovieList.length;
    }

    public void setMovieList(Movie[] list) {
        mMovieList = list;
        notifyDataSetChanged();
    }

    class MoviePosterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView mMoviePosterImageView;

        public MoviePosterViewHolder(View posterView) {
            super(posterView);
            mMoviePosterImageView = (ImageView) posterView.findViewById(R.id.iv_movie_poster);
            posterView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            Movie clickedMovie = mMovieList[clickedPosition];
            mOnMoviePosterClickListener.onMoviePosterClick(clickedMovie);
        }
    }

    public interface MoviePosterClickListener {
        public void onMoviePosterClick(Movie clickedMovie);
    }

}
