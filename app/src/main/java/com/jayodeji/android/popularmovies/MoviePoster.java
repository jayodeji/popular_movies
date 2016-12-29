package com.jayodeji.android.popularmovies;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by joshuaadeyemi on 12/28/16.
 */

public class MoviePoster {

    String posterUrl;
    String thumbnailUrl;
    String originalTitle;
    String movieSynopsis;
    String userRating;
    String releaseDate;

    public MoviePoster(String url) {
        this.posterUrl = url;
    }

    public void setMovieJsonObject(JSONObject movieObject) throws JSONException {
        releaseDate = movieObject.getString("release_data");
        userRating = movieObject.getString("vote_average");
        movieSynopsis = movieObject.getString("overview");
        originalTitle = movieObject.getString("original_title");
    }

    @Override
    public String toString() {
        return this.posterUrl;
    }
}

//{
//        "poster_path": "/qjiskwlV1qQzRCjpV0cL9pEMF9a.jpg",
//        "adult": false,
//        "overview": "A rogue band of resistance fighters unite for a mission to steal the Death Star plans and bring a new hope to the galaxy.",
//        "release_date": "2016-12-14",
//        "genre_ids": [28, 12, 14, 878, 53, 10752],
//        "id": 330459,
//        "original_title": "Rogue One: A Star Wars Story",
//        "original_language": "en",
//        "title": "Rogue One: A Star Wars Story",
//        "backdrop_path": "/tZjVVIYXACV4IIIhXeIM59ytqwS.jpg",
//        "popularity": 181.361561,
//        "vote_count": 945,
//        "video": false,
//        "vote_average": 7.4
//        }