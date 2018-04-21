package com.example.android.popularmoviesstage2;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ReviewRequestType {
    @SerializedName("id")
    public Integer id;

    @SerializedName("results")
    public List<Review> reviews = null;

    @SerializedName("total_results")
    public Integer totalResults;

    @SerializedName("total_pages")
    public Integer totalPages;

    public ReviewRequestType(List<Review> reviews) {
        this.reviews = reviews;
    }
}
