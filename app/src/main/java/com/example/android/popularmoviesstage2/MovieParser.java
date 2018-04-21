package com.example.android.popularmoviesstage2;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;


public class MovieParser extends Views<MainActivity> {

    public static final String TAG = MovieParser.class.getSimpleName();

    final private String apiKey;
    private ContentResolver resolver;

    private FavoriteContentObserver observer;

    private boolean isLoading = false;

    private int selectedRequestType = MovieRequestType.TYPE_POPULAR;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private ArrayList<Movie> favorites = new ArrayList<>();
    private ArrayList<Movie> popular = new ArrayList<>();
    private ArrayList<Movie> toprated = new ArrayList<>();

    private static final String KEY_REQUEST_TYPE = "requestType";
    private static final String KEY_MOVIES_POPULAR = "popular";
    private static final String KEY_MOVIES_TOPRATED = "toprated";

    public MovieParser(String apiKey, ContentResolver resolver) {
        this.apiKey = apiKey;
        this.resolver = resolver;

        observer = new FavoriteContentObserver(new Handler());
        resolver.registerContentObserver(DatabasePoster.MovieFavoriteEntry.CONTENT_URI, true, observer);
    }

    @Override
    public void bindView(MainActivity view) {
        super.bindView(view);

        if(isLoading) {
            showLoading();
            return;
        }

        loadMovieData();
    }

    public Bundle saveState() {
        Bundle state = new Bundle();

        state.putInt(KEY_REQUEST_TYPE, selectedRequestType);

        state.putParcelableArrayList(KEY_MOVIES_POPULAR, popular);
        state.putParcelableArrayList(KEY_MOVIES_TOPRATED, toprated);

        return state;
    }

    public void restoreState(Bundle state) {
        this.selectedRequestType = state.getInt(KEY_REQUEST_TYPE, MovieRequestType.TYPE_POPULAR);

        this.toprated = state.getParcelableArrayList(KEY_MOVIES_TOPRATED);
        this.popular = state.getParcelableArrayList(KEY_MOVIES_POPULAR);
    }

    @Override
    public void dispose() {
        compositeDisposable.clear();
        resolver.unregisterContentObserver(observer);

        super.dispose();
    }

    public void updateMovieData(int requestType) {

        if(isLoading && requestType == selectedRequestType) {
            return;
        }
        selectedRequestType = requestType;

        loadMovieData();
    }

    private void loadMovieData() {
        compositeDisposable.clear();

        DisposableObserver<MovieRequestType> observer = new MovieObserver();
        compositeDisposable.add(observer);

        getDataObservable().delay(0, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(observer);
    }

    private Observable<MovieRequestType> getDataObservable() {
        Observable<MovieRequestType> observable;

        switch(selectedRequestType) {
            case MovieRequestType.TYPE_POPULAR:
                observable = getPopularMovies();
                break;

            case MovieRequestType.TYPE_TOPRATED:
                observable = getTopRatedMovies();
                break;

            case MovieRequestType.TYPE_FAVORITE:
                observable = getFavoriteMovies();
                break;

            default:
                observable = getPopularMovies();
        }

        return observable;
    }

    private Observable<MovieRequestType> getPopularMovies() {
        if(!popular.isEmpty()) {
            return Observable.just(new MovieRequestType(popular, MovieRequestType.TYPE_POPULAR));
        } else {
            return ManageService.getService().getPopularMovies(apiKey).map(new Function<MovieRequestType, MovieRequestType>() {
                @Override
                public MovieRequestType apply(MovieRequestType movieEnvelope) throws Exception {
                    movieEnvelope.resultType = MovieRequestType.TYPE_POPULAR;
                    return movieEnvelope;
                }
            });
        }
    }

    private Observable<MovieRequestType> getTopRatedMovies() {
        if(!toprated.isEmpty()) {
            return Observable.just(new MovieRequestType(toprated, MovieRequestType.TYPE_TOPRATED));
        } else {
            return ManageService.getService().getTopRatedMovies(apiKey).map(new Function<MovieRequestType, MovieRequestType>() {
                @Override
                public MovieRequestType apply(MovieRequestType movieEnvelope) throws Exception {
                    movieEnvelope.resultType = MovieRequestType.TYPE_TOPRATED;
                    return movieEnvelope;
                }
            });
        }
    }

    private Observable<MovieRequestType> getFavoriteMovies() {
        if(!favorites.isEmpty()) {
            return Observable.just(new MovieRequestType(favorites, MovieRequestType.TYPE_FAVORITE));
        } else {
            return Observable.create(new ObservableOnSubscribe<MovieRequestType>() {
                @Override
                public void subscribe(ObservableEmitter<MovieRequestType> e) throws Exception {
                    List<Movie> movies = new ArrayList<>();

                    Cursor results = resolver.query(DatabasePoster.MovieFavoriteEntry.CONTENT_URI, null, null, null, null);

                    // Favorites movies stored in the database
                    if (results != null) {
                        while (results.moveToNext()) {
                            Movie movie = new Movie();

                            int idIndex = results.getColumnIndex(DatabasePoster.MovieFavoriteEntry.COLUMN_MOVIEID);
                            int posterPathIndex = results.getColumnIndex(DatabasePoster.MovieFavoriteEntry.COLUMN_POSTERPATH);
                            int overviewIndex = results.getColumnIndex(DatabasePoster.MovieFavoriteEntry.COLUMN_OVERVIEW);
                            int releaseDateIndex = results.getColumnIndex(DatabasePoster.MovieFavoriteEntry.COLUMN_RELEASEDATE);
                            int titleIndex = results.getColumnIndex(DatabasePoster.MovieFavoriteEntry.COLUMN_TITLE);
                            int voteAverageIndex = results.getColumnIndex(DatabasePoster.MovieFavoriteEntry.COLUMN_RATING);
                            int posterIndex = results.getColumnIndex(DatabasePoster.MovieFavoriteEntry.COLUMN_POSTER);

                            movie.id = results.getInt(idIndex);
                            movie.posterPath = results.getString(posterPathIndex);
                            movie.overview = results.getString(overviewIndex);
                            movie.releaseDate = results.getString(releaseDateIndex);
                            movie.title = results.getString(titleIndex);
                            movie.voteAverage = results.getDouble(voteAverageIndex);
                            movie.poster = results.getBlob(posterIndex);

                            movies.add(movie);
                        }
                        results.close();
                    }
                    e.onNext(new MovieRequestType(movies, MovieRequestType.TYPE_FAVORITE));
                    e.onComplete();
                }
            });
        }
    }

    private void showLoading() {
        if(view != null) {
            view.showLoading();
        }
    }

    private void showData(ArrayList<Movie> movies) {
        if(view != null) {
            view.updateData(movies);
            view.hideLoading();
        }
    }

    private class MovieObserver extends DisposableObserver<MovieRequestType> {

        @Override
        protected void onStart() {
            super.onStart();
            isLoading = true;
            showLoading();
        }

        @Override
        public void onNext(MovieRequestType envelope) {
            isLoading = false;

            ArrayList<Movie> movies = (ArrayList<Movie>) envelope.movies;

            switch (envelope.resultType) {
                case MovieRequestType.TYPE_POPULAR:
                    popular = movies;
                    break;

                case MovieRequestType.TYPE_TOPRATED:
                    toprated = movies;
                    break;

                case MovieRequestType.TYPE_FAVORITE:
                    favorites = movies;

                    if(favorites.isEmpty()) {
                        view.showEmptyFavoritesWarning();
                    }

                    break;
            }
            showData(movies);
        }

        @Override
        public void onError(Throwable e) {
            isLoading = false;
            showData(new ArrayList<Movie>());

            if(view != null) {
                view.showError();
            }
        }

        @Override
        public void onComplete() {}
    }

    private class FavoriteContentObserver extends ContentObserver {

        public FavoriteContentObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            favorites = new ArrayList<>();
        }
    }
}