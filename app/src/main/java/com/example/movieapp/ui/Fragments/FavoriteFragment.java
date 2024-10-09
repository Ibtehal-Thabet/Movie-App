package com.example.movieapp.ui.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.example.movieapp.R;
import com.example.movieapp.data.auth.TokenManager;
import com.example.movieapp.data.database.DatabaseHelper;
import com.example.movieapp.domain.movie.MovieItem;
import com.example.movieapp.ui.Activities.LoginActivity;
import com.example.movieapp.ui.Adapter.FavoriteMoviesAdapter;

import java.util.ArrayList;
import java.util.List;

public class FavoriteFragment extends Fragment {

    private RecyclerView.Adapter adapterFavMovies;
    private RecyclerView recyclerViewFavMovies;
    private TextView countFavItems;
    private LinearLayout emptyLayout;
    private Button loginFav;
    private RequestQueue queue;
    private DatabaseHelper databaseHelper;
    private StringRequest stringSliderRequest, stringRequestPopular, stringRequestUpcoming;

    TokenManager tokenManager;

    public FavoriteFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);
        tokenManager = new TokenManager(requireActivity());

        initViews(view);

        return view;
    }

    private void initViews(View view) {

        emptyLayout = view.findViewById(R.id.emptyLayout);
        recyclerViewFavMovies = view.findViewById(R.id.favMoviesRV);
        recyclerViewFavMovies.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        loginFav = view.findViewById(R.id.loginFav);

        String userId = tokenManager.getUserId();

        databaseHelper = new DatabaseHelper(requireActivity());
//        MoviePreferences moviePreferences = new MoviePreferences();
        List<MovieItem> movies = new ArrayList<>();
        if (userId != null) {
            movies = databaseHelper.getFavoriteMoviesByUser(userId);
            loginFav.setVisibility(View.GONE);
        } else {
            loginFav.setVisibility(View.VISIBLE);
            loginFav.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            });
        }
//                moviePreferences.getFavoriteMovies(requireActivity(), refreshToken);

        adapterFavMovies = new FavoriteMoviesAdapter(movies);
        recyclerViewFavMovies.setAdapter(adapterFavMovies);

        countFavItems = view.findViewById(R.id.countTV);
        countFavItems.setText("Favorite Items: " + movies.size());

        if (movies.isEmpty()) {
            recyclerViewFavMovies.setVisibility(View.GONE);
            emptyLayout.setVisibility(View.VISIBLE);
        } else {
            recyclerViewFavMovies.setVisibility(View.VISIBLE);
            emptyLayout.setVisibility(View.GONE);
        }
    }
}