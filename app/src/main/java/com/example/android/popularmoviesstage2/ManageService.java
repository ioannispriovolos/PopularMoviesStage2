package com.example.android.popularmoviesstage2;

import retrofit2.Retrofit;

public class ManageService {

    private static ManageService manageService;
    private Service service;

    private static final String MOVIE_BASE_URL = "https://api.themoviedb.org/3/movie/";

    // https://stackoverflow.com/questions/26500036/using-retrofit-in-android
    private ManageService() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(MOVIE_BASE_URL).addCallAdapterFactory(ManageConnections.getRxJava2CallAdapterFactory()).addConverterFactory(ManageConnections.getGsonConverter()).client(ManageConnections.getClient()).build();
        service = retrofit.create(Service.class);
    }

    public static Service getService() {
        if(manageService == null) {
            manageService = new ManageService();
        }
        return manageService.service;
    }
}
