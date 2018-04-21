package com.example.android.popularmoviesstage2;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MovieRequestType {

    public static final int TYPE_POPULAR = 100;
    public static final int TYPE_TOPRATED = 101;
    public static final int TYPE_FAVORITE = 102;

    @SerializedName("results")
    public List<Movie> movies = null;

    public int resultType;

    public MovieRequestType(List<Movie> movies, int resultType) {
        this.movies = movies;
        this.resultType = resultType;
    }
}
