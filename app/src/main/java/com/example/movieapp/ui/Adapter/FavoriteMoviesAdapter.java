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
import com.example.movieapp.ui.Activities.MovieDetailsActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FavoriteMoviesAdapter extends RecyclerView.Adapter<FavoriteMoviesAdapter.ViewHolder> {

    List<MovieItem> items;
    Context context;
    private DatabaseHelper databaseHelper;
    TokenManager tokenManager;

    public FavoriteMoviesAdapter(List<MovieItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public FavoriteMoviesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favorite, parent, false);
        context = parent.getContext();
        return new FavoriteMoviesAdapter.ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteMoviesAdapter.ViewHolder holder, int position) {
        holder.titleTxt.setText(items.get(position).getTitle());
        holder.dateTxt.setText(items.get(position).getReleaseDate());
        holder.rateTxt.setText(String.valueOf(items.get(position).getVoteAverage()));
        Picasso.get()
                .load("https://image.tmdb.org/t/p/original" + items.get(position).getBackdropPath())
                .placeholder(R.drawable.no_image_placeholder)
                .into(holder.movieImg);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), MovieDetailsActivity.class);
            intent.putExtra("movieId", items.get(position).getId());
            holder.itemView.getContext().startActivity(intent);
        });

        tokenManager = new TokenManager(context);
        databaseHelper = new DatabaseHelper(context);
        String refreshToken = tokenManager.getRefreshToken();
        String userId = tokenManager.getUserId();
        holder.bookmark.setOnClickListener(v -> {
            databaseHelper.removeFavoriteMovie(userId, items.get(position).getId());
//            moviePreferences.removeMovieFromFavorites(context, refreshToken, items.get(position));
            Toast.makeText(context, "Movie removed from favorite successfully", Toast.LENGTH_SHORT).show();
            notifyDataSetChanged();
            notifyItemRemoved(position);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTxt, rateTxt, dateTxt;
        ImageView movieImg, bookmark;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTxt = itemView.findViewById(R.id.movieFavTitle);
            dateTxt = itemView.findViewById(R.id.movieFavDate);
            rateTxt = itemView.findViewById(R.id.movieFavRate);
            movieImg = itemView.findViewById(R.id.movieFavImg);
            bookmark = itemView.findViewById(R.id.bookmark_fav);
        }
    }

}
