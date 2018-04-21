package com.example.android.popularmoviesstage2;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface Service {

    // https://stackoverflow.com/questions/24100372/retrofit-and-get-using-parameters
    @GET("popular")
    Observable<MovieRequestType> getPopularMovies(@Query("api_key") String apiKey);

    @GET("top_rated")
    Observable<MovieRequestType> getTopRatedMovies(@Query("api_key") String apiKey);

    @GET("{id}/reviews")
    Observable<ReviewRequestType> getReviews(@Path("id") int id, @Query("api_key") String apiKey);

    @GET("{id}/videos")
    Observable<TrailerRequestType> getTrailers(@Path("id") int id, @Query("api_key") String apiKey);
}