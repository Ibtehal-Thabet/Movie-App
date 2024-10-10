package com.example.movieapp.ui.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movieapp.R;
import com.example.movieapp.domain.movie.MovieItem;
import com.example.movieapp.ui.Activities.MovieDetailsActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FavoriteMoviesAdapter extends RecyclerView.Adapter<FavoriteMoviesAdapter.ViewHolder> {

    List<MovieItem> items;
    private OnFavoriteClickListener listener;
    Context context;

    public FavoriteMoviesAdapter(List<MovieItem> items, OnFavoriteClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FavoriteMoviesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favorite, parent, false);
        context = parent.getContext();
        return new FavoriteMoviesAdapter.ViewHolder(inflate);
    }

    @SuppressLint("NotifyDataSetChanged")
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


        holder.bookmark.setOnClickListener(v -> {
            listener.onRemoveFavorite(items.get(position).getId());
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public interface OnFavoriteClickListener {
        void onRemoveFavorite(int movieId);
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
