package com.example.android.popularmoviesstage2;

public class Views<V> {

    public V view;
    private String viewTag;

    public void bindView(V view) {
        this.view = view;
        this.viewTag = view.getClass().getSimpleName();
    }
    public void unbindView() { view = null; }

    public void dispose() {
        ManageViews.disposePresenter(viewTag);
    }
}
