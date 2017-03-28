package com.jayodeji.android.popularmovies;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jayodeji.android.popularmovies.data.Trailer;

/**
 * Created by joshuaadeyemi on 2/18/17.
 */

public class TrailerListAdapter extends RecyclerView.Adapter<TrailerListAdapter.TrailerViewHolder> {

    private static final String TAG = TrailerListAdapter.class.getSimpleName();

    private Trailer[] mTrailerList = null;

    private final TrailerClickListener mOnTrailerClickedListener;

    public TrailerListAdapter(TrailerClickListener trailerClickListener, Trailer[] trailerList) {
        mOnTrailerClickedListener = trailerClickListener;
        mTrailerList = trailerList;
    }

    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        boolean attachImmediatelyToParent = false;
        View view = inflater.inflate(R.layout.movie_trailer_item, parent, attachImmediatelyToParent);
        return new TrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerViewHolder holder, int position) {
        if (mTrailerList != null && (mTrailerList.length > position)) {
            String trailerTitle = "Trailer " + (position + 1);
            holder.mMovieTrailerTitle.setText(trailerTitle);
        }
    }

    @Override
    public int getItemCount() {
        if (mTrailerList == null) {
            return 0;
        }
        return mTrailerList.length;
    }

    public class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mMovieTrailerTitle;
        private ImageView mPlayIcon;

        public TrailerViewHolder(View trailerView) {
            super(trailerView);
            mMovieTrailerTitle = (TextView) trailerView.findViewById(R.id.trailer_title);
            mPlayIcon = (ImageView) trailerView.findViewById(R.id.play_icon);
//            mPlayIcon.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            Trailer trailer = mTrailerList[clickedPosition];
            mOnTrailerClickedListener.onTrailerClick(trailer);
        }
    }

    public interface TrailerClickListener {
        public void onTrailerClick(Trailer clickedTrailer);
    }
}
