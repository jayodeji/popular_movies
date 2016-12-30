package com.jayodeji.android.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by joshuaadeyemi on 12/28/16.
 */

public class Movie implements Parcelable {

    public final String posterUrl;
    public final String thumbnailUrl;
    public final String originalTitle;
    public final String movieSynopsis;
    public final String userRating;
    public final String releaseDate;

    private Movie(Builder builder) {
        posterUrl = builder.newPosterUrl;
        thumbnailUrl = builder.newThumbnailUrl;
        originalTitle = builder.newOriginalTitle;
        movieSynopsis = builder.newMovieSynopsis;
        userRating = builder.newUserRating;
        releaseDate = builder.newReleaseDate;
    }

    protected Movie(Parcel in) {
        posterUrl = in.readString();
        thumbnailUrl = in.readString();
        originalTitle = in.readString();
        movieSynopsis = in.readString();
        userRating = in.readString();
        releaseDate = in.readString();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(posterUrl);
        parcel.writeString(thumbnailUrl);
        parcel.writeString(originalTitle);
        parcel.writeString(movieSynopsis);
        parcel.writeString(userRating);
        parcel.writeString(releaseDate);
    }

    public static class Builder {
        private String newPosterUrl;
        private String newThumbnailUrl;
        private String newOriginalTitle;
        private String newMovieSynopsis;
        private String newUserRating;
        private String newReleaseDate;

        public Builder posterUrl(String posterUrl) {
            newPosterUrl = posterUrl;
            return this;
        }

        public Builder thumbnailUrl(String thumbnailUrl) {
            newThumbnailUrl = thumbnailUrl;
            return this;
        }

        public Builder title(String originalTitle) {
            newOriginalTitle = originalTitle;
            return this;
        }

        public Builder overview(String synopsis) {
            newMovieSynopsis = synopsis;
            return this;
        }

        public Builder rating(String userRating) {
            newUserRating = userRating;
            return this;
        }

        public Builder releaseDate(String releaseDate) {
            newReleaseDate = releaseDate;
            return this;
        }

        public Movie build() {
            return new Movie(this);
        }
    }
}