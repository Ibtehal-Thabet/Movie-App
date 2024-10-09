package com.example.movieapp.ui.Activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.movieapp.BuildConfig;
import com.example.movieapp.R;
import com.example.movieapp.data.auth.TokenManager;
import com.example.movieapp.data.database.DatabaseHelper;
import com.example.movieapp.domain.cast.CastItem;
import com.example.movieapp.domain.movie.MovieItem;
import com.example.movieapp.ui.Adapter.CastListAdapter;
import com.example.movieapp.ui.Adapter.MovieGenresListAdapter;
import com.example.movieapp.ui.Extension.AlarmReceiver;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;

public class MovieDetailsActivity extends AppCompatActivity {

    private RecyclerView.Adapter adapterGenre, adapterCast;
    private RecyclerView recyclerViewGenre, recyclerViewCast;
    private BlurView blurView;
    private LinearLayout tvDayLayout, tvTimeLayout, tvChannelLayout;
    private TextView movieTitleTV, movieDateTV, movieTimeTV, movieRateTV, movieSummeryTV, tvDayTxt, tvTimeTxt, tvChannelTxt;
    private ImageView movieImg, bookmarkImg, tvDayImg, tvTimeImg, tvChannelImg;
    private ImageButton backBtn;
    private AppCompatButton watchTrailerBtn;
    private RequestQueue queue;
    private StringRequest stringRequest;

    private MovieItem movieItem;
    private int movieId;
    private String movieTitle = "";
    private List<String> genresList;
    private List<CastItem> castList;

    private Short min, hour;
    private String days = "";
    private boolean isSaved;
    private TokenManager tokenManager;
    private DatabaseHelper databaseHelper;
    private static final String MOVIES_API_KEY = BuildConfig.MOVIES_API_KEY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        movieId = getIntent().getIntExtra("movieId", 0);
        tokenManager = new TokenManager(this);
        databaseHelper = new DatabaseHelper(this);

        initView();
        sendRequest();
        getMovieGenresList();
        getMovieCastList();
    }

    private void initView() {
        float radius = 10f;
        View decorView = getWindow().getDecorView();
        ViewGroup rootView = (ViewGroup) decorView.findViewById(android.R.id.content);
        Drawable windowsBackground = decorView.getBackground();
        blurView = findViewById(R.id.blurView);
        blurView.setupWith(rootView, new RenderScriptBlur(this))
                .setFrameClearDrawable(windowsBackground)
                .setBlurRadius(radius);
        blurView.setOutlineProvider(ViewOutlineProvider.BACKGROUND);
        blurView.setClipToOutline(true);

        movieTitleTV = findViewById(R.id.titleTV);
        movieDateTV = findViewById(R.id.movieDate);
        movieTimeTV = findViewById(R.id.movieTime);
        movieRateTV = findViewById(R.id.movieRate);
        movieSummeryTV = findViewById(R.id.movieSummery);
        tvDayTxt = findViewById(R.id.tvDayTxt);
        tvTimeTxt = findViewById(R.id.tvTimeTxt);
        tvChannelTxt = findViewById(R.id.tvChannelTxt);
        tvDayLayout = findViewById(R.id.tvDayLayout);
        tvTimeLayout = findViewById(R.id.tvTimeLayout);
        tvChannelLayout = findViewById(R.id.tvChannelLayout);
        tvDayImg = findViewById(R.id.tvDayImg);
        tvTimeImg = findViewById(R.id.tvTimeImg);
        tvChannelImg = findViewById(R.id.tvChannelImg);
        movieImg = findViewById(R.id.movieDetailsImg);
        bookmarkImg = findViewById(R.id.bookmarkImg);
        backBtn = findViewById(R.id.backBtn);
        watchTrailerBtn = findViewById(R.id.watchTrailerBtn);

        recyclerViewGenre = findViewById(R.id.genreRV);
        recyclerViewCast = findViewById(R.id.castRV);


        backBtn.setOnClickListener(v -> finish());

        watchTrailerBtn.setOnClickListener(v -> {
            watchTrailer();
        });

        String userId = tokenManager.getUserId();
        if (userId != null) {
            isSaved = databaseHelper.isFavorite(userId, movieId);

            if (!isSaved) {
                bookmarkImg.setImageResource(R.drawable.bookmark);
            } else {
                bookmarkImg.setImageResource(R.drawable.ic_bookmark);
            }
        }

        bookmarkImg.setOnClickListener(v -> {
            if (userId == null) {
                Toast.makeText(this, "Please Login first", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!isSaved) {
                databaseHelper.addFavoriteMovie(userId, movieId, movieItem.getTitle(), movieItem.getReleaseDate(), (Double) movieItem.getVoteAverage(), movieItem.getBackdropPath());
                bookmarkImg.setImageResource(R.drawable.ic_bookmark);
                Toast.makeText(this, "Movie added to favorite successfully", Toast.LENGTH_SHORT).show();
            } else {
                databaseHelper.removeFavoriteMovie(userId, movieId);
                bookmarkImg.setImageResource(R.drawable.bookmark);
                Toast.makeText(this, "Movie removed from favorite successfully", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void sendRequest() {

        queue = Volley.newRequestQueue(this);
        stringRequest = new StringRequest(Request.Method.GET, "https://api.themoviedb.org/3/movie/" + movieId + "?api_key=" + MOVIES_API_KEY, response -> {
            Gson gson = new Gson();

            movieItem = gson.fromJson(response, MovieItem.class);
            movieTitle = movieItem.getTitle();
            getShowTV();
            Picasso.get()
                    .load("https://image.tmdb.org/t/p/original" + movieItem.getPosterPath())
                    .placeholder(R.drawable.no_image_placeholder)
                    .into(movieImg);
            movieTitleTV.setText(movieItem.getTitle());
            movieDateTV.setText(movieItem.getReleaseDate());
            movieTimeTV.setText(movieItem.getRuntime() + " min");
            movieRateTV.setText(String.valueOf(movieItem.getVoteAverage()));
            movieSummeryTV.setText(movieItem.getOverview());

        }, error -> {
            Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();

        });
        queue.add(stringRequest);

    }

    private void watchTrailer() {
        queue = Volley.newRequestQueue(this);
        JsonObjectRequest requestYoutube = new JsonObjectRequest("https://api.themoviedb.org/3/movie/" + movieId + "/videos?api_key=" + MOVIES_API_KEY, response -> {
            try {
                JSONArray resultsArray = response.getJSONArray("results");
                for (int i = 0; i < resultsArray.length(); i++) {
                    JSONObject videoObject = resultsArray.getJSONObject(i);
                    String type = videoObject.getString("type");
                    if (type.equals("Trailer")) {
                        String trailerKey = videoObject.getString("key");
                        String trailerUrl = "https://www.youtube.com/watch?v=" + trailerKey + "/embed";
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        //   intent.setPackage("com.google.android.youtube"); // Replace with the YouTube app package name
                        intent.setData(Uri.parse(trailerUrl));
                        startActivity(intent);
                    }
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }, error -> {
            Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
        });
        queue.add(requestYoutube);
    }

    private void getMovieGenresList() {
        genresList = new ArrayList<>();
        queue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest("https://api.themoviedb.org/3/movie/" + movieId + "?api_key=" + MOVIES_API_KEY, response -> {
            try {
                JSONArray resultsArray = response.getJSONArray("genres");
                for (int i = 0; i < resultsArray.length(); i++) {
                    JSONObject genreObject = resultsArray.getJSONObject(i);
                    String name = genreObject.getString("name");
                    genresList.add(name);
                }
                recyclerViewGenre.setAdapter(new MovieGenresListAdapter(genresList));
                recyclerViewGenre.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }, error -> {
            Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
        });
        queue.add(request);
    }

    private void getMovieCastList() {
        castList = new ArrayList<>();
        queue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest("https://api.themoviedb.org/3/movie/" + movieId + "/credits?api_key=" + MOVIES_API_KEY, response -> {
            try {
                JSONArray resultsArray = response.getJSONArray("cast");
                for (int i = 0; i < 5; i++) {
                    JSONObject genreObject = resultsArray.getJSONObject(i);
                    CastItem castItem = new CastItem();
                    String name = genreObject.getString("name");
                    castItem.setName(name);
                    String profilePath = genreObject.getString("profile_path");
                    castItem.setProfilePath(profilePath);
                    castList.add(castItem);
                }
                recyclerViewCast.setAdapter(new CastListAdapter(castList));
                recyclerViewCast.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }, error -> {
            Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
        });
        queue.add(request);
    }

    private void getShowTV() {
        String tv_url = "https://api.tvmaze.com/singlesearch/shows?q=" + movieTitle.toString();
        Log.i("response", movieTitle);
        JsonObjectRequest request_tv = new JsonObjectRequest(tv_url, response -> {
            try {
                if (response.getJSONObject("schedule").getString("time").isEmpty()) {
                    tvDayImg.setVisibility(View.GONE);
                    tvTimeLayout.setVisibility(View.GONE);
                    tvChannelLayout.setVisibility(View.GONE);
                    tvDayTxt.setText("This movie will not display soon");
                    return;
                }
                days = response.getJSONObject("schedule").getJSONArray("days").get(0).toString();
                String time = response.getJSONObject("schedule").getString("time");
                tvDayTxt.setText(days);
                tvTimeTxt.setText(time);

                String[] parts = time.split(":");
                hour = (short) Integer.parseInt(parts[0]);
                min = (short) Integer.parseInt(parts[1]);

                String channel = "";
                if (response.getString("network").equals("null")) {
                    channel = response.getJSONObject("webChannel").getString("name");
                } else {
                    channel = response.getJSONObject("network").getString("name");
                }
                tvChannelTxt.append(channel);
            } catch (JSONException e) {
                Log.i("response catch", "Movie will not display Soon");
            }
        }, error -> {
            tvDayImg.setVisibility(View.GONE);
            tvTimeLayout.setVisibility(View.GONE);
            tvChannelLayout.setVisibility(View.GONE);
            tvDayTxt.setText("This movie will not display soon");
            Log.i("Error", "Error");
        });
        queue.add(request_tv);
    }

    public void reminder(int day, Short hour, Short min) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE); // OR PendingIntent.FLAG_IMMUTABLE
        Calendar calendar = Calendar.getInstance();
//        calendar.set(Calendar.YEAR, 2024);
//        calendar.set(Calendar.MONTH, Calendar.OCTOBER);
//        calendar.set(Calendar.DAY_OF_MONTH, 4);
        calendar.set(Calendar.DAY_OF_WEEK, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    private int dayNameComparison(String dayName) {
        DateFormatSymbols objDaySymbol = new DateFormatSymbols();
        String symbolDayNames[] = objDaySymbol.getWeekdays();
        for (int countDayname = 0; countDayname < symbolDayNames.length; countDayname++) {
            if (dayName.equalsIgnoreCase(symbolDayNames[countDayname])) {
                System.out.println(dayName + " = " + symbolDayNames[countDayname]);
                return countDayname;
            }
        }
        return 0;
    }
}