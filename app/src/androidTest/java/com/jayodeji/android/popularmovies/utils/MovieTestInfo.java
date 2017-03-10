package com.jayodeji.android.popularmovies.utils;

import android.content.ContentValues;

/**
 * Created by joshuaadeyemi on 3/10/17.
 */

public class MovieTestInfo {

    public final ContentValues movie;
    public final ContentValues[] trailers;
    public final ContentValues[] reviews;

    public MovieTestInfo(ContentValues movie, ContentValues[] trailers, ContentValues[] reviews) {
        this.movie = movie;
        this.trailers = trailers;
        this.reviews = reviews;
    }
}
