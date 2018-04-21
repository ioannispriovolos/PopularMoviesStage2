package com.example.android.popularmoviesstage2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.ByteArrayOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
// // https://github.com/udacity/ud851-Sunshine/blob/student/S12.04-Solution-ResourceQualifiers/app/src/main/java/com/example/android/sunshine/utilities/NetworkUtils.java
public class NetworkUtils {

    public static final String TAG = NetworkUtils.class.getSimpleName();

    // Movie Database URL
    private static final String DB_URL = "https://image.tmdb.org/t/p";

    public static URL buildImageURL(String path, String size) {
        path = path.replace("/", "");

        Uri uri = Uri.parse(DB_URL).buildUpon().appendPath(size).appendPath(path).build();

        URL url = null;
        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {}

        return url;
    }

    public static byte[] encodeImageData(Bitmap image) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 0, stream);

        return stream.toByteArray();
    }

    public static Bitmap decodeImageData(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }
}