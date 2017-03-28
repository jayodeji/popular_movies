package com.jayodeji.android.popularmovies;

import android.annotation.TargetApi;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jayodeji.android.popularmovies.data.MoviePoster;
import com.squareup.picasso.Picasso;

/**
 * Created by joshuaadeyemi on 12/28/16.
 */

public class MovieGridAdapter extends RecyclerView.Adapter<MovieGridAdapter.MoviePosterViewHolder> {

    private static final String TAG = MovieGridAdapter.class.getSimpleName();

    private MoviePoster[] mMovieList = null;

    private final MoviePosterClickListener mOnMoviePosterClickListener;

    public MovieGridAdapter(MoviePosterClickListener listener) {
        mOnMoviePosterClickListener = listener;
    }

    @Override
    public MoviePosterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        boolean attachImmediatelyToParent = false;
        View view = inflater.inflate(R.layout.poster_item, parent, attachImmediatelyToParent);
        return new MoviePosterViewHolder(view);
    }

    @TargetApi(16)
    @Override
    public void onBindViewHolder(MoviePosterViewHolder holder, int position) {
        if (mMovieList != null && (mMovieList.length > position)) {
            MoviePoster movie = mMovieList[position];

            Picasso.with(holder.mMoviePosterImageView.getContext())
                    .load(movie.posterUrl)
                    .error(R.drawable.placeholder)
                    .into(holder.mMoviePosterImageView);

            String description = "Poster for movie: " + movie.title;
            holder.mMoviePosterImageView.setContentDescription(description);
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

    public void setMovieList(MoviePoster[] list) {
        mMovieList = list;
        notifyDataSetChanged();
    }

    class MoviePosterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView mMoviePosterImageView;

        public MoviePosterViewHolder(View posterView) {
            super(posterView);
            mMoviePosterImageView = (ImageView) posterView.findViewById(R.id.poster);
            posterView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            MoviePoster clickedMovie = mMovieList[clickedPosition];
            mOnMoviePosterClickListener.onMoviePosterClick(clickedMovie);
        }
    }

    public interface MoviePosterClickListener {
        void onMoviePosterClick(MoviePoster clickedMovie);
    }

}
