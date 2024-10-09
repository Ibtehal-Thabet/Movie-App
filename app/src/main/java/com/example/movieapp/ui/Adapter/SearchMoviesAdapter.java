package com.example.movieapp.ui.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movieapp.ui.Activities.MovieDetailsActivity;
import com.example.movieapp.R;
import com.example.movieapp.domain.movie.MovieSearchDetails;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SearchMoviesAdapter extends RecyclerView.Adapter<SearchMoviesAdapter.ViewHolder> {

    List<MovieSearchDetails> items;
    //    private List<MovieItem> filteredMovieList;
    Context context;

    public SearchMoviesAdapter(List<MovieSearchDetails> items) {
        this.items = items;
//        this.filteredMovieList = new ArrayList<>(items);
    }

    @NonNull
    @Override
    public SearchMoviesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search, parent, false);
        context = parent.getContext();
        return new SearchMoviesAdapter.ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchMoviesAdapter.ViewHolder holder, int position) {
//        MovieItem movie = filteredMovieList.get(position);
        MovieSearchDetails movie = items.get(position);
        holder.titleTxt.setText(movie.getTitle());
        holder.dateTxt.setText(movie.getDate());
        Picasso.get()
                .load("https://image.tmdb.org/t/p/original" + movie.getImage())
                .placeholder(R.drawable.no_image)
                .into(holder.movieImg);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), MovieDetailsActivity.class);
            intent.putExtra("movieId", movie.getId());
            holder.itemView.getContext().startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

//    public void updateList(List<MovieItem> newFilteredList) {
//        items.clear();
//        items.addAll(newFilteredList);  // Update the filtered data
//        notifyDataSetChanged();  // Notify the adapter that data has changed
//    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTxt, dateTxt;
        ImageView movieImg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTxt = itemView.findViewById(R.id.searchItemTxt);
            dateTxt = itemView.findViewById(R.id.searchItemDateTxt);
            movieImg = itemView.findViewById(R.id.searchItemImg);
        }
    }

}
