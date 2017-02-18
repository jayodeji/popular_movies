package com.jayodeji.android.popularmovies.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by joshuaadeyemi on 2/18/17.
 */

public class MoviePoster implements Parcelable {

    public final String posterUrl;
    public final int movieId;

    private MoviePoster(Builder builder) {
        this.posterUrl = builder.mPosterUrl;
        this.movieId = builder.mMovieId;
    }

    protected MoviePoster(Parcel in) {
        this.posterUrl = in.readString();
        this.movieId = in.readInt();
    }

    public static final Creator<MoviePoster> CREATOR = new Creator<MoviePoster>() {
        @Override
        public MoviePoster createFromParcel(Parcel in) {
            return new MoviePoster(in);
        }

        @Override
        public MoviePoster[] newArray(int size) {
            return new MoviePoster[size];
        }
    };

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(posterUrl);
        dest.writeInt(movieId);
    }

    public static class Builder {
        private String mPosterUrl;
        private int mMovieId;

        public Builder movieId(int movieId) {
            this.mMovieId = movieId;
            return this;
        }

        public Builder posterUrl(String posterUrl) {
            this.mPosterUrl = posterUrl;
            return this;
        }

        public MoviePoster build() {
            return new MoviePoster(this);
        }
    }
}
