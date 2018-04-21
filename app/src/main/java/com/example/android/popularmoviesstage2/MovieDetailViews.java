package com.example.android.popularmoviesstage2;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableObserver;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Function3;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class MovieDetailViews extends Views<DetailActivity> {

    public static final String TAG = MovieDetailViews.class.getSimpleName();

    private String apiKey;
    private ContentResolver resolver;

    private boolean loading;
    private Boolean favorite;

    // http://www.vogella.com/tutorials/RxJava/article.html
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private Movie movie;
    private ArrayList<Trailer> trailers = new ArrayList<>();
    private ArrayList<Review> reviews = new ArrayList<>();

    private static final String KEY_MOVIE = "movie";
    private static final String KEY_TRAILERS = "trailers";
    private static final String KEY_REVIEWS = "reviews";
    private static final String KEY_FAVORITE = "favorite";

    public MovieDetailViews(String apiKey, ContentResolver resolver) {
        this.apiKey = apiKey;
        this.resolver = resolver;
    }

    @Override
    public void bindView(DetailActivity view) {
        super.bindView(view);

        if(loading) {
            showLoading();
            return;
        }
        loadMovieDetails();
    }

    @Override
    public void dispose() {
        compositeDisposable.clear();
        super.dispose();
    }

    public Bundle saveState() {
        Bundle state = new Bundle();

        state.putParcelable(KEY_MOVIE, movie);
        state.putParcelableArrayList(KEY_TRAILERS, trailers);
        state.putParcelableArrayList(KEY_REVIEWS, reviews);
        state.putBoolean(KEY_FAVORITE, favorite);

        return state;
    }

    public void restoreState(Bundle state) {
        movie = state.getParcelable(KEY_MOVIE);
        trailers = state.getParcelableArrayList(KEY_TRAILERS);
        reviews = state.getParcelableArrayList(KEY_REVIEWS);
        favorite = state.getBoolean(KEY_FAVORITE);
    }

    public void setMovieData(Movie movie) {
        this.movie = movie;
    }

    private void loadMovieDetails() {
        compositeDisposable.clear();

        DisposableObserver<DetailsCombined> observer = new MovieDetailViews.MovieObserver();
        compositeDisposable.add(observer);

        Observable<TrailerRequestType> trailers = getTrailers();
        Observable<ReviewRequestType> reviews = getReviews();
        Observable<Boolean> favorite = getFavorite();

        Observable<DetailsCombined> combined = Observable.zip(trailers, reviews, favorite,
                new Function3<TrailerRequestType, ReviewRequestType, Boolean, DetailsCombined>() {
                    @Override
                    public DetailsCombined apply(TrailerRequestType trailerEnvelope, ReviewRequestType reviewEnvelope, Boolean favorite) throws Exception {
                        return new DetailsCombined(trailerEnvelope.trailers, reviewEnvelope.reviews, favorite);
                    }
                });

        combined.delay(0, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(observer);
    }

    private Observable<ReviewRequestType> getReviews() {
        if(!reviews.isEmpty()) {
            return Observable.just(new ReviewRequestType(reviews));
        } else {
            return ManageService.getService().getReviews(movie.id, apiKey)
                    .onErrorReturn(new Function<Throwable, ReviewRequestType>() {
                        @Override
                        public ReviewRequestType apply(Throwable throwable) throws Exception {

                            // Create an empty envelope with no reviews
                            ReviewRequestType envelope = new ReviewRequestType(new ArrayList<Review>());
                            envelope.totalResults = 0;
                            envelope.totalPages = 0;
                            envelope.reviews = new ArrayList<>();

                            return envelope;
                        }
                    });
        }
    }

    private Observable<TrailerRequestType> getTrailers() {
        if(!trailers.isEmpty()) {
            return Observable.just(new TrailerRequestType(trailers));
        } else {
            return ManageService.getService().getTrailers(movie.id, apiKey)
                    .onErrorReturn(new Function<Throwable, TrailerRequestType>() {
                        @Override
                        public TrailerRequestType apply(Throwable throwable) throws Exception {

                            // Create an empty envelope with no trailers
                            TrailerRequestType envelope = new TrailerRequestType(new ArrayList<Trailer>());
                            envelope.trailers = new ArrayList<>();

                            return envelope;
                        }
                    });
        }
    }

    private Observable<Boolean> getFavorite() {
        if(favorite != null) {
            return Observable.just(favorite);
        } else {

            return Observable.create(new ObservableOnSubscribe<Boolean>() {
                @Override
                public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                    Cursor result = resolver.query(getMovieProviderUri(),
                            new String[]{DatabasePoster.MovieFavoriteEntry.COLUMN_MOVIEID},
                            null, null, null);

                    if(result != null) {
                        // If we got a result then the movie is stored in the favorites
                        if(result.moveToFirst()) {
                            e.onNext(true);
                        } else {
                            e.onNext(false);
                        }
                        result.close();
                    } else {
                        e.onNext(false);
                    }
                    e.onComplete();
                }
            });
        }
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(final Bitmap poster) {

        favorite = true;

        Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter e) throws Exception {

                ContentValues values = new ContentValues();

                values.put(DatabasePoster.MovieFavoriteEntry.COLUMN_MOVIEID, movie.id);
                values.put(DatabasePoster.MovieFavoriteEntry.COLUMN_TITLE, movie.title);
                values.put(DatabasePoster.MovieFavoriteEntry.COLUMN_RELEASEDATE, movie.releaseDate);
                values.put(DatabasePoster.MovieFavoriteEntry.COLUMN_OVERVIEW, movie.overview);
                values.put(DatabasePoster.MovieFavoriteEntry.COLUMN_RATING, movie.voteAverage);
                values.put(DatabasePoster.MovieFavoriteEntry.COLUMN_POSTERPATH, movie.posterPath);

                if(poster != null) {
                    movie.poster = NetworkUtils.encodeImageData(poster);
                    values.put(DatabasePoster.MovieFavoriteEntry.COLUMN_POSTER, movie.poster);
                }

                Uri result = resolver.insert(DatabasePoster.MovieFavoriteEntry.CONTENT_URI, values);

                if(result == null) {
                    e.onError(new Throwable("DB error saving favorite."));
                }

                e.onComplete();
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onComplete() {

            }

            @Override
            public void onError(Throwable e) {}
        });
    }

    public void removeFavorite() {

        favorite = false;

        Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter e) throws Exception {
                int rows = resolver.delete(getMovieProviderUri(), null, null);

                if(rows != 1) {
                    e.onError(new Throwable("Problem removing movie from database."));
                }

                e.onComplete();
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onComplete() {}

            @Override
            public void onError(Throwable e) {}
        });
    }

    private Uri getMovieProviderUri() {
        return DatabasePoster.MovieFavoriteEntry.CONTENT_URI.buildUpon().appendPath(String.valueOf(movie.id)).build();
    }

    private void showLoading() {
        if(view != null) {
            view.showLoading();
        }
    }

    private void showData() {
        if(view != null) {
            view.updateData(movie, trailers, reviews);
            view.hideLoading();
        }
    }

    private class MovieObserver extends DisposableObserver<DetailsCombined> {

        @Override
        protected void onStart() {
            super.onStart();
            loading = true;
            showLoading();
        }

        @Override
        public void onNext(DetailsCombined composite) {
            trailers = composite.trailers;
            reviews = composite.reviews;
            favorite = composite.favorite;
        }

        @Override
        public void onError(Throwable e) {
            loading = false;

            if(view != null) {
                view.showError();
            }
        }

        @Override
        public void onComplete() {
            loading = false;
            showData();
        }
    }
}