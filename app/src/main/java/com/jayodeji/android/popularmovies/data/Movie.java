package com.jayodeji.android.popularmovies.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by joshuaadeyemi on 12/28/16.
 */

public class Movie implements Parcelable {

    public final long internalId;
    public final int movieId;
    public final String posterUrl;
    public final String thumbnailUrl;
    public final String originalTitle;
    public final String movieSynopsis;
    public final String userRating;
    public final String releaseDate;
    public final int runtime;
    public final Trailer[] trailers;
    public final Review[] reviews;

    private Movie(Builder builder) {
        internalId = builder.internalId;
        movieId = builder.newMovieId;
        posterUrl = builder.newPosterUrl;
        thumbnailUrl = builder.newThumbnailUrl;
        originalTitle = builder.newOriginalTitle;
        movieSynopsis = builder.newMovieSynopsis;
        userRating = builder.newUserRating;
        releaseDate = builder.newReleaseDate;
        runtime = builder.newRuntime;
        trailers = builder.newTrailers;
        reviews = builder.newReviews;
    }

    protected Movie(Parcel in) {
        internalId = in.readLong();
        movieId = in.readInt();
        posterUrl = in.readString();
        thumbnailUrl = in.readString();
        originalTitle = in.readString();
        movieSynopsis = in.readString();
        userRating = in.readString();
        releaseDate = in.readString();
        runtime = in.readInt();
        trailers = in.createTypedArray(Trailer.CREATOR);
        reviews = in.createTypedArray(Review.CREATOR);
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
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(internalId);
        dest.writeInt(movieId);
        dest.writeString(posterUrl);
        dest.writeString(thumbnailUrl);
        dest.writeString(originalTitle);
        dest.writeString(movieSynopsis);
        dest.writeString(userRating);
        dest.writeString(releaseDate);
        dest.writeInt(runtime);
        dest.writeTypedArray(trailers, flags);
        dest.writeTypedArray(reviews, flags);
    }

    public static class Builder {
        private long internalId = 0;
        private int newMovieId;
        private String newPosterUrl;
        private String newThumbnailUrl;
        private String newOriginalTitle;
        private String newMovieSynopsis;
        private String newUserRating;
        private String newReleaseDate;
        private int newRuntime;
        private Trailer[] newTrailers;
        private Review[] newReviews;

        public Builder internalId(long internalId) {
            this.internalId = internalId;
            return this;
        }

        public Builder movieId(int movieId) {
            newMovieId = movieId;
            return this;
        }
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

        public Builder runtime(int runtime) {
            newRuntime = runtime;
            return this;
        }

        public Builder trailers(Trailer[] trailers) {
            newTrailers = trailers;
            return this;
        }

        public Builder reviews(Review[] reviews) {
            newReviews = reviews;
            return this;
        }

        public Movie build() {
            return new Movie(this);
        }
    }
}