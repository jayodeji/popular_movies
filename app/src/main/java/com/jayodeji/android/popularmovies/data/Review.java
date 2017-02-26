package com.jayodeji.android.popularmovies.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by joshuaadeyemi on 2/26/17.
 */

public class Review implements Parcelable {

    public final String reviewId;
    public final String author;
    public final String url;

    private Review(Builder builder) {
        reviewId = builder.newReviewId;
        author = builder.newAuthor;
        url = builder.newUrl;
    }

    protected Review(Parcel in) {
        reviewId = in.readString();
        author = in.readString();
        url = in.readString();
    }

    public static final Creator<Review> CREATOR = new Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel in) {
            return new Review(in);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(reviewId);
        dest.writeString(author);
        dest.writeString(url);
    }

    public static class Builder {

        private String newReviewId = "id";
        private String newAuthor = "author";
        private String newUrl = "url";

        public Builder reviewId(String reviewId) {
            newReviewId = reviewId;
            return this;
        }

        public Builder author(String author) {
            newAuthor = author;
            return this;
        }

        public Builder url(String url) {
            newUrl = url;
            return this;
        }

        public Review build() {
            return new Review(this);
        }
    }
}
