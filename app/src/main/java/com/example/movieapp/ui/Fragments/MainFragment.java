package com.example.movieapp.ui.Fragments;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.movieapp.BuildConfig;
import com.example.movieapp.R;
import com.example.movieapp.data.auth.TokenManager;
import com.example.movieapp.data.database.DatabaseHelper;
import com.example.movieapp.domain.movie.MovieResponse;
import com.example.movieapp.domain.slider.SliderItem;
import com.example.movieapp.ui.Adapter.MoviesListAdapter;
import com.example.movieapp.ui.Adapter.SliderAdapter;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainFragment extends Fragment {

    private RecyclerView.Adapter adapterPopularMovies, adapterUpcomingMovies;
    private RecyclerView recyclerViewPopularMovies, recyclerViewUpcomingMovies;

    private ViewPager2 viewPager2;
    private RequestQueue queue;
    private StringRequest stringSliderRequest, stringRequestPopular, stringRequestUpcoming;
    private static final String MOVIES_API_KEY = BuildConfig.MOVIES_API_KEY;
    private ProgressBar loadingBanner, loadingPopularMovies, loadingUpcoming;
    private DatabaseHelper databaseHelper;
    private TokenManager tokenManager;
    Handler sliderHandler = new Handler();
    Runnable sliderRunnable = new Runnable() {
        @Override
        public void run() {
            viewPager2.setCurrentItem(viewPager2.getCurrentItem() + 1);
        }
    };


    public MainFragment() {
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
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        databaseHelper = new DatabaseHelper(requireActivity());
        tokenManager = new TokenManager(requireActivity());


        initViews(view);
        loadPopularMovies();
        loadUpcomingMovies();
//        sendPopularMoviesRequest();
//        sendUpcomingMoviesRequest();

        return view;
    }

    private void initViews(View view) {
        recyclerViewPopularMovies = view.findViewById(R.id.popular_movies_RV);
        recyclerViewPopularMovies.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewUpcomingMovies = view.findViewById(R.id.upcoming_movies_RV);
        recyclerViewUpcomingMovies.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        viewPager2 = view.findViewById(R.id.viewPager2);
        loadingBanner = view.findViewById(R.id.pagerProgressBar);
        loadingPopularMovies = view.findViewById(R.id.loading_popular_movies);
        loadingUpcoming = view.findViewById(R.id.loading_upcoming_movies);
        initSlider();
    }

    private void sendPopularMoviesRequest() {
        queue = Volley.newRequestQueue(requireActivity());
        loadingPopularMovies.setVisibility(View.VISIBLE);
        stringRequestPopular = new StringRequest(Request.Method.GET, "https://api.themoviedb.org/3/movie/popular?api_key=" + MOVIES_API_KEY, response -> {
            Gson gson = new Gson();
            loadingPopularMovies.setVisibility(View.GONE);

            MovieResponse items = gson.fromJson(response, MovieResponse.class);
            adapterPopularMovies = new MoviesListAdapter(items);
            recyclerViewPopularMovies.setAdapter(adapterPopularMovies);

            // Save the data to SQLite for future use
//            databaseHelper.saveMovies("popular_movies", response);

            // Only update the data if it has changed
            databaseHelper.updateMoviesIfChanged("popular_movies", response);

        }, error -> {
            loadingPopularMovies.setVisibility(View.GONE);
            Toast.makeText(requireActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();

        });
        queue.add(stringRequestPopular);
    }

    private void sendUpcomingMoviesRequest() {
        queue = Volley.newRequestQueue(requireActivity());
        loadingUpcoming.setVisibility(View.VISIBLE);
        stringRequestUpcoming = new StringRequest(Request.Method.GET, "https://api.themoviedb.org/3/movie/upcoming?api_key=" + MOVIES_API_KEY, response -> {
            Gson gson = new Gson();
            loadingUpcoming.setVisibility(View.GONE);

            MovieResponse items = gson.fromJson(response, MovieResponse.class);
            adapterUpcomingMovies = new MoviesListAdapter(items);
            recyclerViewUpcomingMovies.setAdapter(adapterUpcomingMovies);

            // Save the data to SQLite for future use
//            databaseHelper.saveMovies("upcoming_movies", response);

            // Only update the data if it has changed
            databaseHelper.updateMoviesIfChanged("upcoming_movies", response);

        }, error -> {
            loadingUpcoming.setVisibility(View.GONE);
            Toast.makeText(requireActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();

        });
        queue.add(stringRequestUpcoming);
    }

    private void initSlider() {
        queue = Volley.newRequestQueue(requireActivity());
        loadingBanner.setVisibility(View.VISIBLE);
        ArrayList<SliderItem> items = new ArrayList<>();
        JsonObjectRequest stringSliderRequest = new JsonObjectRequest("https://api.themoviedb.org/3/movie/now_playing?api_key=" + MOVIES_API_KEY, response -> {
            loadingBanner.setVisibility(View.GONE);
            try {
                JSONArray resultsArray = response.getJSONArray("results");
                for (int i = 0; i < 5; i++) {
                    JSONObject object = resultsArray.getJSONObject(i);
                    SliderItem sliderItem = new SliderItem();
                    int id = object.getInt("id");
                    sliderItem.setId(id);
                    String img = object.getString("backdrop_path");
                    sliderItem.setImage(img);
                    String title = object.getString("title");
                    sliderItem.setTitle(title);
                    String year = object.getString("release_date");
                    sliderItem.setYear(year);
                    String rate = object.getString("vote_average");
                    sliderItem.setVoteAverage(Double.parseDouble(rate));
                    String lang = object.getString("original_language");
                    sliderItem.setLang(lang);
                    items.add(sliderItem);
                }
                setBanners(items);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

        }, error -> {
            loadingBanner.setVisibility(View.GONE);
            Toast.makeText(requireActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();

        });
        queue.add(stringSliderRequest);
    }

    private void setBanners(ArrayList<SliderItem> items) {
        viewPager2.setAdapter(new SliderAdapter(items, viewPager2));
        viewPager2.setClipToPadding(false);
        viewPager2.setClipChildren(false);
        viewPager2.setOffscreenPageLimit(3);
        viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));
        compositePageTransformer.addTransformer((page, position) -> {
            float r = 1 - Math.abs(position);
            page.setScaleY(0.85f + r * 0.15f);
        });

        viewPager2.setPageTransformer(compositePageTransformer);
        viewPager2.setCurrentItem(1);
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                sliderHandler.removeCallbacks(sliderRunnable);
            }
        });

    }

    private void fetchDataFromApi(DatabaseHelper dbHelper) {
        // Call your API and get the response
//        String apiData = ...;

        // Save the data to the database
//        dbHelper.saveData(apiData);

        // Use the data
//        displayData(apiData);
    }

    private void loadPopularMovies() {
        String cachedData = databaseHelper.getMovies("popular_movies");

        if (cachedData != null) {
            Gson gson = new Gson();
            MovieResponse items = gson.fromJson(cachedData, MovieResponse.class);
            adapterPopularMovies = new MoviesListAdapter(items);
            recyclerViewPopularMovies.setAdapter(adapterPopularMovies);
        } else {
            sendPopularMoviesRequest();
        }
    }

    private void loadUpcomingMovies() {
        String cachedData = databaseHelper.getMovies("upcoming_movies");

        if (cachedData != null) {
            Gson gson = new Gson();
            MovieResponse items = gson.fromJson(cachedData, MovieResponse.class);
            adapterUpcomingMovies = new MoviesListAdapter(items);
            recyclerViewUpcomingMovies.setAdapter(adapterUpcomingMovies);
        } else {
            sendUpcomingMoviesRequest();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        sliderHandler.removeCallbacks(sliderRunnable);
    }

    @Override
    public void onResume() {
        super.onResume();
        sliderHandler.postDelayed(sliderRunnable, 2000);
    }
}
