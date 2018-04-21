package com.example.android.popularmoviesstage2;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieGridAdapterViewHolder> {

    private ArrayList<Movie> movies;

    private MovieClickHandler clickHandler;

    public MovieAdapter(MovieClickHandler clickHandler) {
        this.clickHandler = clickHandler;
    }

    @Override
    public MovieGridAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.items_grid, parent, false);

        return new MovieGridAdapterViewHolder(view);
    }


    private void loadPosterImage(ImageView poster, Movie movie) {
        if(movie.poster != null) {
            Bitmap bitmap = NetworkUtils.decodeImageData(movie.poster);
            poster.setImageBitmap(bitmap);

        } else {
            String imageUrl = NetworkUtils.buildImageURL(movie.posterPath, poster.getContext().getString(R.string.api_image_size)).toString();

            Picasso.with(poster.getContext()).load(imageUrl).placeholder(R.drawable.poster_holder).into(poster);
        }
    }

    @Override
    public void onBindViewHolder(MovieGridAdapterViewHolder holder, int position) {
        Movie movie = movies.get(position);
        loadPosterImage(holder.poster, movie);
    }

    @Override
    public int getItemCount() {
        if(movies == null) {
            return 0;
        }
        return movies.size();
    }

    public void setMovieData(ArrayList<Movie> data) {
        movies = data;
        notifyDataSetChanged();
    }

    // https://stackoverflow.com/questions/29479647/setonclicklistener-vs-onclicklistener-vs-view-onclicklistener
    public class MovieGridAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView poster;

        public MovieGridAdapterViewHolder(View view) {
            super(view);
            poster = (ImageView) view.findViewById(R.id.iv_movie_poster);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Movie clickedMovie = movies.get(getAdapterPosition());
            clickHandler.onClickMovie(clickedMovie);
        }
    }

    public interface MovieClickHandler {
        void onClickMovie(Movie movie);
    }
}