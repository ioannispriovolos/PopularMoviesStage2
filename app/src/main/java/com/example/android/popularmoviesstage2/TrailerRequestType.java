package com.example.android.popularmoviesstage2;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TrailerRequestType {
    @SerializedName("id")
    public Integer id;

    @SerializedName("results")
    public List<Trailer> trailers = null;

    public TrailerRequestType(List<Trailer> trailers) {
        this.trailers = trailers;
    }
}
