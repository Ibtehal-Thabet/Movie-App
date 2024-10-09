package com.example.movieapp.ui.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movieapp.R;
import com.example.movieapp.domain.cast.CastItem;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CastListAdapter extends RecyclerView.Adapter<CastListAdapter.ViewHolder> {
    List<CastItem> items;
    Context context;

    public CastListAdapter(List<CastItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public CastListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_actor, parent, false);
        context = parent.getContext();
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull CastListAdapter.ViewHolder holder, int position) {
        holder.nameTxt.setText(items.get(position).getName());
        Picasso.get()
                .load("https://image.tmdb.org/t/p/original" + items.get(position).getProfilePath())
                .placeholder(R.drawable.person_placholder)
                .into(holder.actorImg);

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTxt;
        ImageView actorImg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTxt = itemView.findViewById(R.id.actorName);
            actorImg = itemView.findViewById(R.id.actorImg);
        }
    }
}
