package com.example.android.popularmoviesstage2;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import okhttp3.OkHttpClient;
import retrofit2.converter.gson.GsonConverterFactory;

public class ManageConnections {

    private static ManageConnections manageConnections;

    private static OkHttpClient client;
    private static GsonConverterFactory gsonConverter;
    private static RxJava2CallAdapterFactory rxJava2CallAdapterFactory;

    private ManageConnections() {
        client = new OkHttpClient();
        gsonConverter = GsonConverterFactory.create();
        rxJava2CallAdapterFactory = RxJava2CallAdapterFactory.create();
    }

    public static void initializeManageConnections() {
        if(manageConnections == null) {
            manageConnections = new ManageConnections();
        }
    }

    public static OkHttpClient getClient() {
        if(client == null) {
            initializeManageConnections();
        }
        return client;
    }

    public static GsonConverterFactory getGsonConverter() {
        if(gsonConverter == null) {
            initializeManageConnections();
        }
        return gsonConverter;
    }

    public static RxJava2CallAdapterFactory getRxJava2CallAdapterFactory() {
        if(rxJava2CallAdapterFactory == null) {
            initializeManageConnections();
        }
        return rxJava2CallAdapterFactory;
    }
}