package com.example.android.popularmoviesstage2;

import android.support.v4.util.SimpleArrayMap;

public class ManageViews {

    public static final String TAG = ManageViews.class.getSimpleName();

    private static ManageViews manageViews;

    private SimpleArrayMap<String, Views> simpleArrayMap;

    private ManageViews() {}

    public static void initializeManageViews() {
        if(manageViews == null) {
            manageViews = new ManageViews();
            // https://stackoverflow.com/questions/8328164/build-a-simple-array-in-android
            manageViews.simpleArrayMap = new SimpleArrayMap<>();
        }
    }

    // http://www.codejava.net/java-core/the-java-language/suppresswarnings-annotation-examples
    @SuppressWarnings("unchecked")
    public static <V extends Views> V getPresenter(String key, ViewCreator<V> factory) {
        V presenter = null;
        try {
            presenter = (V) manageViews.simpleArrayMap.get(key);
        } catch (ClassCastException e) {}

        if(presenter == null) {
            presenter = factory.createPresenter();
            manageViews.simpleArrayMap.put(key, presenter);
        }
        return presenter;
    }

    public static void disposePresenter(String key) {
        manageViews.simpleArrayMap.remove(key);
    }
}
