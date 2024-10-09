package com.example.movieapp.ui.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movieapp.R;

import java.util.List;

public class MovieGenresListAdapter extends RecyclerView.Adapter<MovieGenresListAdapter.ViewHolder> {
    List<String> items;
    Context context;

    public MovieGenresListAdapter(List<String> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public MovieGenresListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_genre, parent, false);
        context = parent.getContext();
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieGenresListAdapter.ViewHolder holder, int position) {
        holder.genreTxt.setText(items.get(position));

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView genreTxt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            genreTxt = itemView.findViewById(R.id.genreTxt);
        }
    }
}
