package com.example.android.popularmoviesstage2;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class DetailActivity extends AppCompatActivity implements MovieDetailAdapter.MovieOnClickHandler {

    public static final String TAG = DetailActivity.class.getSimpleName();

    @BindView(R.id.rv_movie_details) RecyclerView recyclerView;
    @BindView(R.id.pb_movie_details_loading) ProgressBar progressBar;

    private MovieDetailViews presenter;
    private MovieDetailAdapter adapter;

    private boolean activityIsFinished = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies);

        ButterKnife.bind(this);

        presenter = ManageViews.getPresenter(TAG, presenterFactory);

        Movie movie = getIntent().getParcelableExtra(Movie.TAG);

        if(movie != null) {
            presenter.setMovieData(movie);
        }

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
    public void onTrailerClickPlay(Trailer trailer) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse("vnd.youtube:" + trailer.key), "video/*");

        if(intent.resolveActivity(getPackageManager()) == null) {
            intent.setData(Uri.parse("https://youtu.be/" + trailer.key));
        }

        startActivity(Intent.createChooser(intent, getString(R.string.trailer_codec)));
    }

    @Override
    public void onTrailerClickShare(Trailer trailer) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");

        intent.putExtra(Intent.EXTRA_TEXT, "https://youtu.be/" + trailer.key);
        startActivity(Intent.createChooser(intent, getString(R.string.trailer_share)));
    }

    private void configureRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new MovieDetailAdapter(this.presenter, this);
        recyclerView.setAdapter(adapter);

        DividerItemDecoration decoration = new DividerItemDecoration(recyclerView.getContext(), linearLayoutManager.getOrientation());
        recyclerView.addItemDecoration(decoration);

        ((SimpleItemAnimator)recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
    }

    public void showLoading() {
        recyclerView.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideLoading() {
        progressBar.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    public void updateData(Movie movie, ArrayList<Trailer> trailers, ArrayList<Review> reviews) {
        adapter.setMovieData(movie, trailers, reviews);
    }

    public void showError() {
        Toast.makeText(this, getString(R.string.loading_error), Toast.LENGTH_LONG).show();
        hideLoading();
    }

    private ViewCreator<MovieDetailViews> presenterFactory = new ViewCreator<MovieDetailViews>() {
        @NonNull @Override
        public MovieDetailViews createPresenter() {
            return new MovieDetailViews(getResources().getString(R.string.API_KEY),
                    getContentResolver());
        }
    };
}
