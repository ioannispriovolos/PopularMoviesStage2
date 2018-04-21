package com.example.android.popularmoviesstage2;

import java.util.ArrayList;
import java.util.List;

public class DetailsCombined {

    public ArrayList<Trailer> trailers;
    public ArrayList<Review> reviews;

    public boolean favorite;

    public DetailsCombined (List<Trailer> trailers, List<Review> reviews, boolean favorite) {
        this.trailers = (ArrayList<Trailer>) trailers;
        this.reviews = (ArrayList<Review>) reviews;
        this.favorite = favorite;
    }
}
