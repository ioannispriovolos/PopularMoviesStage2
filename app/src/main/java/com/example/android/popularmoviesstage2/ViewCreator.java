package com.example.android.popularmoviesstage2;

import android.support.annotation.NonNull;

// https://stackoverflow.com/questions/15748144/android-java-create-an-interface-which-extends-an-existing-class
public interface ViewCreator<V extends Views> {
    @NonNull V createPresenter();
}


