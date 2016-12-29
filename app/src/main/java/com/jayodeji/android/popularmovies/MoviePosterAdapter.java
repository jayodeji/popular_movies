package com.jayodeji.android.popularmovies;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by joshuaadeyemi on 12/28/16.
 */

public class MoviePosterAdapter extends RecyclerView.Adapter<MoviePosterAdapter.MoviePosterViewHolder> {

    private static final String TAG = MoviePosterAdapter.class.getSimpleName();

    private MoviePoster[] mMoviePosterList = new MoviePoster[] {
            new MoviePoster("https://upload.wikimedia.org/wikipedia/en/5/53/Captain_America_Civil_War_poster.jpg"),
            new MoviePoster("https://upload.wikimedia.org/wikipedia/en/5/53/Captain_America_Civil_War_poster.jpg"),
            new MoviePoster("https://upload.wikimedia.org/wikipedia/en/5/53/Captain_America_Civil_War_poster.jpg"),
            new MoviePoster("https://upload.wikimedia.org/wikipedia/en/5/53/Captain_America_Civil_War_poster.jpg"),
            new MoviePoster("https://upload.wikimedia.org/wikipedia/en/5/53/Captain_America_Civil_War_poster.jpg"),
            new MoviePoster("https://upload.wikimedia.org/wikipedia/en/5/53/Captain_America_Civil_War_poster.jpg"),
            new MoviePoster("https://upload.wikimedia.org/wikipedia/en/5/53/Captain_America_Civil_War_poster.jpg"),
            new MoviePoster("https://upload.wikimedia.org/wikipedia/en/5/53/Captain_America_Civil_War_poster.jpg"),
            new MoviePoster("https://upload.wikimedia.org/wikipedia/en/5/53/Captain_America_Civil_War_poster.jpg"),
            new MoviePoster("https://upload.wikimedia.org/wikipedia/en/5/53/Captain_America_Civil_War_poster.jpg"),
    };

    @Override
    public MoviePosterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        boolean attachImmediatelyToParent = false;
        View view = inflater.inflate(R.layout.movie_thumbnail_item, parent, attachImmediatelyToParent);
        return new MoviePosterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MoviePosterViewHolder holder, int position) {
        if (mMoviePosterList != null && (mMoviePosterList.length > position)) {
            MoviePoster moviePoster = mMoviePosterList[position];
            String movieUrl = moviePoster.toString();
            holder.mMoviePosterTextView.setText(movieUrl);
            Log.d(TAG, "Bound url to view: " + movieUrl);
        } else {
            Log.d(TAG, "Cannot get movie with position: " + position);
        }
    }

    @Override
    public int getItemCount() {
        if (mMoviePosterList == null) {
            return 0;
        }
        return mMoviePosterList.length;
    }

    public void setmMoviePosterList(MoviePoster[] list) {
        mMoviePosterList = list;
        notifyDataSetChanged();
    }

    class MoviePosterViewHolder extends RecyclerView.ViewHolder {

        private TextView mMoviePosterTextView;

        public MoviePosterViewHolder(View posterView) {
            super(posterView);
            mMoviePosterTextView = (TextView) posterView.findViewById(R.id.iv_movie_thumbnail);
        }


    }

}
