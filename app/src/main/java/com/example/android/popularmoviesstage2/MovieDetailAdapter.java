package com.example.android.popularmoviesstage2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Locale;

public class MovieDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final String TAG = MovieDetailAdapter.class.getSimpleName();

    private Movie movie;
    private ArrayList<Trailer> trailers;
    private ArrayList<Review> reviews;

    private MovieDetailViews presenter;
    private MovieOnClickHandler clickHandler;

    private static final int VIEW_HEADER = 100;
    private static final int VIEW_TRAILER = 101;
    private static final int VIEW_REVIEW = 102;

    public MovieDetailAdapter(MovieDetailViews presenter, MovieOnClickHandler clickHandler) {
        this.presenter = presenter;
        this.clickHandler = clickHandler;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view;
        switch(viewType) {
            case VIEW_HEADER:
                view = inflater.inflate(R.layout.activity_detail, parent, false);
                return new MovieDetailHeaderViewHolder(view);

            case VIEW_TRAILER:
                view = inflater.inflate(R.layout.trailer, parent, false);
                return new MovieTrailerViewHolder(view);

            case VIEW_REVIEW:
                view = inflater.inflate(R.layout.review, parent, false);
                return new MovieReviewViewHolder(view);

            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder untypedHolder, int position) {
        if(untypedHolder instanceof MovieDetailHeaderViewHolder) {
            bindHeaderViewHolder((MovieDetailHeaderViewHolder) untypedHolder);
        }
        else if(untypedHolder instanceof MovieTrailerViewHolder) {
            int trailerPosition = getTrailerPositionFromAdapterPosition(position);
            bindTrailerViewHolder((MovieTrailerViewHolder) untypedHolder, trailerPosition);
        }
        else if(untypedHolder instanceof MovieReviewViewHolder) {
            int reviewPosition = getReviewPositionFromAdapterPosition(position);
            bindReviewViewHolder((MovieReviewViewHolder) untypedHolder, reviewPosition);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0) {
            return VIEW_HEADER;
        }
        else if(position > 0 && position <= trailers.size()) {
            return VIEW_TRAILER;
        }
        return VIEW_REVIEW;
    }

    @Override
    public int getItemCount() {
        if(movie == null) {
            return 0;
        }
        return 1 + trailers.size() + reviews.size();
    }

    public void setMovieData(Movie movie, ArrayList<Trailer> trailers, ArrayList<Review> reviews) {
        this.movie = movie;
        this.trailers = trailers;
        this.reviews = reviews;

        notifyDataSetChanged();
    }

    private int getTrailerPositionFromAdapterPosition(int adapterPosition) {
        return adapterPosition - 1;
    }

    private int getReviewPositionFromAdapterPosition(int adapterPosition) {
        return adapterPosition - trailers.size() - 1;
    }

    private void loadPosterImage(ImageView poster) {
        if(movie.poster != null) {
            Bitmap bitmap = NetworkUtils.decodeImageData(movie.poster);
            poster.setImageBitmap(bitmap);
        }
        else {
            String imageUrl = NetworkUtils.buildImageURL(movie.posterPath, poster.getContext().getString(R.string.api_image_size)).toString();

            Picasso.with(poster.getContext()).load(imageUrl).placeholder(R.drawable.poster_holder).into(poster);
        }
    }

    private void bindHeaderViewHolder(MovieDetailHeaderViewHolder holder) {
        Context context = holder.itemView.getContext();

        // Movie title & Year released
        holder.title.setText(String.format(context.getString(R.string.title_details), movie.title, Integer.valueOf(movie.releaseDate.split("-")[0])));

        // Rating
        holder.rating.setText(String.format(Locale.ENGLISH, "%.1f", movie.voteAverage));

        // Overview
        holder.overview.setText(movie.overview);

        // Favorite button Add/ Remove
        if(presenter.isFavorite()) {
            holder.favorite.setText(context.getString(R.string.remove_favorite));
            holder.heart.setImageResource(R.drawable.favorite);
        }
        else {
            holder.favorite.setText(context.getString(R.string.add_favorite));
            holder.heart.setImageResource(R.drawable.bounds_favorite);
        }
        loadPosterImage(holder.poster);
    }

    private void bindTrailerViewHolder(MovieTrailerViewHolder holder, int trailerPosition) {
        Context context = holder.itemView.getContext();

        try {
            Trailer trailer = trailers.get(trailerPosition);

            holder.title.setText(trailer.name);

            // Video size
            String size = context.getString(R.string.trailer_HD);
            if (trailer.size < 720) {
                size = context.getString(R.string.trailer_SD);
            }

            holder.size.setText(String.format(context.getString(R.string.trailer_size), size, trailer.size));
        } catch (IndexOutOfBoundsException e) {}
    }

    private void bindReviewViewHolder(MovieReviewViewHolder holder, int reviewPosition) {
        Context context = holder.itemView.getContext();

        try {
            Review review = reviews.get(reviewPosition);

            // Author review
            holder.title.setText(String.format(context.getString(R.string.title_review), review.author));

            // Review
            holder.review.setText(review.content);

        } catch (IndexOutOfBoundsException e) {}
    }

    private class MovieDetailHeaderViewHolder extends RecyclerView.ViewHolder {

        final TextView title;
        final TextView rating;
        final TextView favorite;
        final TextView overview;

        final ImageView poster;
        final ImageView heart;

        final LinearLayout favoriteButton;

        MovieDetailHeaderViewHolder(View view) {
            super(view);

            title = (TextView) view.findViewById(R.id.tv_movie_title);
            favorite = (TextView) view.findViewById(R.id.tv_favorite);
            rating = (TextView) view.findViewById(R.id.tv_rating);
            overview = (TextView) view.findViewById(R.id.tv_overview);

            poster = (ImageView) view.findViewById(R.id.iv_movie_poster);
            heart = (ImageView) view.findViewById(R.id.iv_favorite_heart);

            favoriteButton = (LinearLayout) view.findViewById(R.id.favorite_button);

            favoriteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(presenter.isFavorite()) {
                        presenter.removeFavorite();
                    }
                    else {

                        Drawable drawable = poster.getDrawable();

                        if(drawable instanceof BitmapDrawable) {
                            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                            presenter.setFavorite(bitmap);
                        }
                        else {
                            presenter.setFavorite(null);
                        }
                    }
                    notifyItemChanged(getAdapterPosition());
                }
            });
        }
    }

    private class MovieTrailerViewHolder extends RecyclerView.ViewHolder {

        final TextView title;
        final TextView size;
        final ImageButton share;

        MovieTrailerViewHolder(View view) {
            super(view);

            title = (TextView) view.findViewById(R.id.tv_trailer_title);
            size = (TextView) view.findViewById(R.id.tv_trailer_size);
            share = (ImageButton) view.findViewById(R.id.ib_share);


            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getTrailerPositionFromAdapterPosition(getAdapterPosition());
                    clickHandler.onTrailerClickPlay(trailers.get(position));
                }
            });

            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getTrailerPositionFromAdapterPosition(getAdapterPosition());
                    clickHandler.onTrailerClickShare(trailers.get(position));
                }
            });
        }
    }

    private class MovieReviewViewHolder extends RecyclerView.ViewHolder {

        final TextView title;
        final TextView review;

        MovieReviewViewHolder(View view) {
            super(view);

            title = (TextView) view.findViewById(R.id.tv_review_title);
            review = (TextView) view.findViewById(R.id.tv_review);
        }
    }

    public interface MovieOnClickHandler {
        void onTrailerClickPlay(Trailer trailer);
        void onTrailerClickShare(Trailer trailer);
    }
}