package com.jayodeji.android.popularmovies.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by joshuaadeyemi on 2/18/17.
 */

public class MoviePoster implements Parcelable {

    public final String posterUrl;
    public final int movieId;
    public final String title;

    private MoviePoster(Builder builder) {
        posterUrl = builder.mPosterUrl;
        movieId = builder.mMovieId;
        title = builder.mTitle;
    }

    protected MoviePoster(Parcel in) {
        posterUrl = in.readString();
        movieId = in.readInt();
        title = in.readString();
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
        dest.writeString(title);
    }

    public static class Builder {
        private String mPosterUrl;
        private int mMovieId;
        private String mTitle;

        public Builder movieId(int movieId) {
            mMovieId = movieId;
            return this;
        }

        public Builder posterUrl(String posterUrl) {
            mPosterUrl = posterUrl;
            return this;
        }

        public Builder title(String title) {
            mTitle = title;
            return this;
        }

        public MoviePoster build() {
            return new MoviePoster(this);
        }
    }
}
