package com.jayodeji.android.popularmovies.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by joshuaadeyemi on 2/18/17.
 */

public class Trailer implements Parcelable {

    public final String name;
    public final String key;

    private Trailer(Builder builder) {
        name = builder.mName;
        key = builder.mKey;
    }

    protected Trailer(Parcel in) {
        name = in.readString();
        key = in.readString();
    }

    public static final Creator<Trailer> CREATOR = new Creator<Trailer>() {
        @Override
        public Trailer createFromParcel(Parcel in) {
            return new Trailer(in);
        }

        @Override
        public Trailer[] newArray(int size) {
            return new Trailer[size];
        }
    };

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(key);
    }

    public static class Builder {
        private String mName;
        private String mKey;

        public Builder name(String name) {
            mName = name;
            return this;
        }

        public Builder key(String key) {
            mKey = key;
            return this;
        }

        public Trailer build() {
            return new Trailer(this);
        }
    }
}