package com.jayodeji.android.popularmovies;

/**
 * Created by joshuaadeyemi on 12/28/16.
 */

public class MoviePoster {

    String posterUrl;

    public MoviePoster(String url) {
        this.posterUrl = url;
    }

    @Override
    public String toString() {
        return this.posterUrl;
    }
}
