package com.example.android.popularmoviesstage2;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieClickHandler {

    public static final String TAG = MainActivity.class.getSimpleName();

    // http://jakewharton.github.io/butterknife/
    @BindView(R.id.rv_movie_grid) RecyclerView recyclerView;
    @BindView(R.id.pb_movies_loading) ProgressBar progressBar;

    private MovieParser presenter;
    private MovieAdapter adapter;

    private boolean activityIsFinished = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid);

        ButterKnife.bind(this);

        presenter = ManageViews.getPresenter(TAG, presenterFactory);

        if(savedInstanceState != null) {
            Bundle presenterState = savedInstanceState.getBundle(TAG);
            presenter.restoreState(presenterState);
        }

        configureRecyclerView();
    }

    @Override
    protected void onStart() {
        super.onStart();

        activityIsFinished = true;

        presenter.bindView(this);
    }

    @Override
    protected void onStop() {
        super.onStop();

        presenter.unbindView();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        activityIsFinished = false;
        outState.putBundle(TAG, presenter.saveState());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(activityIsFinished) {
            presenter.dispose();
        }

    }

    @Override
    public void onClickMovie(Movie movie) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(Movie.TAG, movie);

        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id) {
            case R.id.favorite:
                presenter.updateMovieData(MovieRequestType.TYPE_FAVORITE);
                return true;

            case R.id.popular:
                presenter.updateMovieData(MovieRequestType.TYPE_POPULAR);
                return true;

            case R.id.toprated:
                presenter.updateMovieData(MovieRequestType.TYPE_TOPRATED);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void configureRecyclerView() {
        int columns = getResources().getInteger(R.integer.movie_columns);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, columns);
        recyclerView.setLayoutManager(gridLayoutManager);

        // Make posters have the same size
        recyclerView.setHasFixedSize(true);

        adapter = new MovieAdapter(this);
        recyclerView.setAdapter(adapter);
    }

    public void showLoading() {
        recyclerView.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideLoading() {
        progressBar.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    public void updateData(ArrayList<Movie> movieData) {
        adapter.setMovieData(movieData);
    }

    public void showEmptyFavoritesWarning() {
        Toast.makeText(this, getString(R.string.favorites_empty), Toast.LENGTH_LONG).show();
    }

    public void showError() {
        Toast.makeText(this, getString(R.string.loading_error), Toast.LENGTH_LONG).show();
        hideLoading();
    }

    private ViewCreator<MovieParser> presenterFactory =
            new ViewCreator<MovieParser>() {
                @NonNull @Override
                public MovieParser createPresenter() {
                    return new MovieParser(getResources().getString(R.string.API_KEY), getContentResolver());
                }
            };

}