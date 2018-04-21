package com.example.android.popularmoviesstage2;

import android.app.Application;

import com.facebook.stetho.Stetho;


// https://stackoverflow.com/questions/43197379/application-singleton-use-in-android
public class PopularMovies extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ManageConnections.initializeManageConnections();
        ManageViews.initializeManageViews();

        if(BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this);
        }
    }
}