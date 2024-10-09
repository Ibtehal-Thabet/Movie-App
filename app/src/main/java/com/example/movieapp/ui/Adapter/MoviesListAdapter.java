package com.example.movieapp.ui.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movieapp.R;
import com.example.movieapp.data.auth.TokenManager;
import com.example.movieapp.data.database.DatabaseHelper;
import com.example.movieapp.domain.movie.MovieItem;
import com.example.movieapp.domain.movie.MovieResponse;
import com.example.movieapp.ui.Activities.MovieDetailsActivity;
import com.squareup.picasso.Picasso;

public class MoviesListAdapter extends RecyclerView.Adapter<MoviesListAdapter.ViewHolder> {
    MovieResponse items;
    Context context;
    private DatabaseHelper databaseHelper;
    TokenManager tokenManager;

    public MoviesListAdapter(MovieResponse items) {
        this.items = items;
    }

    @NonNull
    @Override
    public MoviesListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie, parent, false);
        context = parent.getContext();
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull MoviesListAdapter.ViewHolder holder, int position) {
        MovieItem movieItem = items.getMovies().get(position);
        holder.titleTxt.setText(movieItem.getTitle());
        holder.rateTxt.setText(String.valueOf(movieItem.getVoteAverage()));
        Picasso.get()
                .load("https://image.tmdb.org/t/p/original" + movieItem.getPosterPath())
                .placeholder(R.drawable.no_image_placeholder)
                .into(holder.movieImg);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), MovieDetailsActivity.class);
            intent.putExtra("movieId", movieItem.getId());
            holder.itemView.getContext().startActivity(intent);
        });

        tokenManager = new TokenManager(context);
        databaseHelper = new DatabaseHelper(context);

        boolean isSaved;
        String userId = tokenManager.getUserId();
        if (userId != null) {
            isSaved = databaseHelper.isFavorite(userId, movieItem.getId());
//                moviePreferences.isMovieSavedInFav(context, refreshToken, items.getMovies().get(position).getId());

            if (!isSaved) {
                holder.bookmark.setImageResource(R.drawable.ic_bookmark_gray);
            } else {
                holder.bookmark.setImageResource(R.drawable.ic_bookmark);
            }
        } else {
            isSaved = false;
        }

        holder.bookmark.setOnClickListener(v -> {
            if (userId == null) {
                Toast.makeText(context, "Please Login first", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!isSaved) {
                databaseHelper.addFavoriteMovie(userId, movieItem.getId(), movieItem.getTitle(), movieItem.getReleaseDate(), (Double) movieItem.getVoteAverage(), movieItem.getBackdropPath());
//                moviePreferences.addMovieToFavorites(context, refreshToken, items.getMovies().get(position));
                holder.bookmark.setImageResource(R.drawable.ic_bookmark);
                Toast.makeText(context, "Movie added to favorite successfully", Toast.LENGTH_SHORT).show();
            } else {
                databaseHelper.removeFavoriteMovie(userId, movieItem.getId());
//                moviePreferences.removeMovieFromFavorites(context, refreshToken, items.getMovies().get(position));
                holder.bookmark.setImageResource(R.drawable.ic_bookmark_gray);
                Toast.makeText(context, "Movie removed from favorite successfully", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return items.getMovies().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTxt, rateTxt;
        ImageView movieImg, bookmark;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTxt = itemView.findViewById(R.id.movieTitleTxt);
            rateTxt = itemView.findViewById(R.id.movieRateTxt);
            movieImg = itemView.findViewById(R.id.movieImg);
            bookmark = itemView.findViewById(R.id.bookmark);
        }
    }
}
