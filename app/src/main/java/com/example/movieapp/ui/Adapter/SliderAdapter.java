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
import androidx.viewpager2.widget.ViewPager2;

import com.example.movieapp.R;
import com.example.movieapp.domain.slider.SliderItem;
import com.example.movieapp.ui.Activities.MovieDetailsActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.SliderViewHolder> {
    private List<SliderItem> sliderItems;
    private ViewPager2 viewPager2;
    private Context context;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            sliderItems.addAll(sliderItems);
            notifyDataSetChanged();
        }
    };

    public SliderAdapter(List<SliderItem> sliderItems, ViewPager2 viewPager2) {
        this.sliderItems = sliderItems;
        this.viewPager2 = viewPager2;
    }

    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_slider, parent, false);
        context = parent.getContext();
        return new SliderViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
        holder.titleTxt.setText(sliderItems.get(position).getTitle());
        holder.rateTxt.setText(String.valueOf(sliderItems.get(position).getVoteAverage()));
        holder.yearTxt.setText(sliderItems.get(position).getYear());
        holder.langTxt.setText(String.valueOf(sliderItems.get(position).getLang()));

        Picasso.get()
                .load("https://image.tmdb.org/t/p/original" + sliderItems.get(position).getImage())
                .placeholder(R.drawable.no_image)
                .into(holder.imageView);

        if (position == sliderItems.size() - 2) {
            viewPager2.post(runnable);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), MovieDetailsActivity.class);
            intent.putExtra("movieId", sliderItems.get(position).getId());
            holder.itemView.getContext().startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return sliderItems.size();
    }

    public class SliderViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView titleTxt, rateTxt, yearTxt, langTxt;

        public SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.sliderImage);
            titleTxt = itemView.findViewById(R.id.titleTxt);
            rateTxt = itemView.findViewById(R.id.rateTxt);
            yearTxt = itemView.findViewById(R.id.yearTxt);
            langTxt = itemView.findViewById(R.id.langTxt);
        }
    }
}
