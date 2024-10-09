package com.example.movieapp.ui.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.movieapp.ui.Adapter.SearchMoviesAdapter;
import com.example.movieapp.BuildConfig;
import com.example.movieapp.R;
import com.example.movieapp.domain.movie.MovieItem;
import com.example.movieapp.domain.movie.MovieResponse;
import com.example.movieapp.domain.movie.MovieSearchDetails;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class SearchFragment extends Fragment {

    private SearchMoviesAdapter adapterSearchMovies;
    private RecyclerView recyclerViewSearchMovies;
    private ProgressBar loadingSearchedMovies;
    private String searchQuery;
    private MovieResponse movies = null;
    private ArrayList<MovieSearchDetails> movieList = new ArrayList<>();
    private StringRequest stringSearchMoviesRequest;
    private static final String MOVIES_API_KEY = BuildConfig.MOVIES_API_KEY;

    public SearchFragment() {
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
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        if (getArguments() != null) {
            searchQuery = getArguments().getString("search_query");
        }

        initViews(view);

        return view;
    }

    private void initViews(View view) {

        loadingSearchedMovies = view.findViewById(R.id.loading_searched_movies);
        loadingSearchedMovies.setVisibility(View.GONE);
        recyclerViewSearchMovies = view.findViewById(R.id.recyclerSearch);
        recyclerViewSearchMovies.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        adapterSearchMovies = new SearchMoviesAdapter(movieList);
        if (searchQuery != null) {
            // Use this method to update UI based on searchQuery
            sendRequest(searchQuery);
        }
    }

    private void sendRequest(String searchQuery) {
        SearchMoviesAdapter adapter = new SearchMoviesAdapter(movieList);

        if (searchQuery.length() != 0) {
            RequestQueue queue = Volley.newRequestQueue(requireActivity());
            loadingSearchedMovies.setVisibility(View.VISIBLE);
            String url = "";
            try {
                String encodedQuery = URLEncoder.encode(searchQuery, "UTF-8");
                url = "https://api.themoviedb.org/3/search/movie?query=" + encodedQuery + "&api_key=" + MOVIES_API_KEY + "&include_adult=false&language=en-US&page=1";
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

//            stringSearchMoviesRequest = new StringRequest(
            JsonObjectRequest tvRequest = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
                Gson gson = new Gson();
                loadingSearchedMovies.setVisibility(View.GONE);
                try {
                    JSONArray resArray = response.getJSONArray("results"); //Getting the results object
                    MovieItem[] movies = gson.fromJson(resArray.toString(), MovieItem[].class);
                    for (MovieItem movie : movies) {
                        movieList.add(new MovieSearchDetails(movie.getTitle(),
                                movie.getOverview(),
                                movie.getReleaseDate(),
                                movie.getBackdropPath(),
                                movie.getId()));
                    }
                    adapter.notifyDataSetChanged();
                    recyclerViewSearchMovies.setAdapter(adapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
//                    try {
//                        JSONArray resArray = response.getJSONArray("results"); //Getting the results object
//                movies = gson.fromJson(response, MovieResponse.class);
//            adapterSearchMovies.updateList(movies.getMovies());
                recyclerViewSearchMovies.setAdapter(adapterSearchMovies);
            }, error -> {
                loadingSearchedMovies.setVisibility(View.GONE);
                Toast.makeText(requireActivity(), "Error occurred during JSON Parsing", Toast.LENGTH_SHORT).show();
                Log.i("Error", "Error occurred during JSON Parsing");

            }
            );
            queue.add(tvRequest);
        }
    }

}
