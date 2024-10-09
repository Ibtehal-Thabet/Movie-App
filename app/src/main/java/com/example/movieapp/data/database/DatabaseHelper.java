package com.example.movieapp.data.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.movieapp.data.auth.TokenManager;
import com.example.movieapp.domain.movie.MovieItem;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "movies.db";
    private static final int DATABASE_VERSION = 2;

    private static final String TABLE_POPULAR_MOVIES = "popular_movies";
    private static final String TABLE_UPCOMING_MOVIES = "upcoming_movies";

//    private static final String TABLE_TOP_MOVIES = "top_movies";

    // Favorite table
    public static final String COLUMN_USER_ID = "user_id";
    public static final String TABLE_FAVORITES = "favorites";
    public static final String COLUMN_MOVIE_ID = "movie_id"; // This can be the movie ID from the API
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_RATE = "rate";
    public static final String COLUMN_POSTER_URL = "poster_url";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_JSON_DATA = "json_data";

    private TokenManager tokenManager;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        tokenManager = new TokenManager(context);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createPopularTable = "CREATE TABLE " + TABLE_POPULAR_MOVIES + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_JSON_DATA + " TEXT)";

        String createUpcomingTable = "CREATE TABLE " + TABLE_UPCOMING_MOVIES + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_JSON_DATA + " TEXT)";

//        String createTopTable = "CREATE TABLE " + TABLE_TOP_MOVIES + "("
//                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
//                + COLUMN_JSON_DATA + " TEXT)";

        String createFavoritesTable = "CREATE TABLE " + TABLE_FAVORITES + " ("
                + COLUMN_MOVIE_ID + " INTEGER PRIMARY KEY, "
                + COLUMN_TITLE + " TEXT, "
                + COLUMN_DATE + " TEXT, "
                + COLUMN_RATE + " DOUBLE, "
                + COLUMN_POSTER_URL + " TEXT, "
                + COLUMN_USER_ID + " TEXT)";

        db.execSQL(createPopularTable);
        db.execSQL(createUpcomingTable);
        db.execSQL(createFavoritesTable);
//        db.execSQL(createTopTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_POPULAR_MOVIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_UPCOMING_MOVIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITES);
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TOP_MOVIES);
        onCreate(db);
    }

    public void saveMovies(String tableName, String jsonData) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(tableName, null, null); // Clear old data

        ContentValues values = new ContentValues();
        values.put(COLUMN_JSON_DATA, jsonData);

        db.insert(tableName, null, values);
        db.close();
    }

    public String getMovies(String tableName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(tableName, new String[]{COLUMN_JSON_DATA}, null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            String jsonData = cursor.getString(0);
            cursor.close();
            return jsonData;
        }
        return null;
    }

    public void updateMoviesIfChanged(String tableName, String newJsonData) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Fetch the existing data
        String existingData = getMovies(tableName);

        if (existingData == null || !existingData.equals(newJsonData)) {
            db.beginTransaction();
            try {
                // Clear the old data
                db.delete(tableName, null, null);

                // Insert the new data
                ContentValues values = new ContentValues();
                values.put(COLUMN_JSON_DATA, newJsonData);
                db.insert(tableName, null, values);

                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }
        db.close();
    }


    // favorite table
    public void addFavoriteMovie(String userId, int movieId, String title, String date, double rate, String posterUrl) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_MOVIE_ID, movieId);
        values.put(COLUMN_TITLE, title);
        values.put(COLUMN_DATE, date);
        values.put(COLUMN_RATE, rate);
        values.put(COLUMN_POSTER_URL, posterUrl);
        values.put(COLUMN_USER_ID, userId); // Associate with the user ID from SharedPreferences

        db.insert(TABLE_FAVORITES, null, values);
        db.close();
    }

    // Remove a favorite movie for a user
    public void removeFavoriteMovie(String userId, int movieId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FAVORITES, COLUMN_MOVIE_ID + " = ? AND " + COLUMN_USER_ID + " = ?", new String[]{String.valueOf(movieId), userId});
        db.close();
    }

    // Retrieve all favorite movies for a specific user
    public List<MovieItem> getFavoriteMoviesByUser(String userId) {
        List<MovieItem> favoriteMovies = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_FAVORITES + " WHERE " + COLUMN_USER_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{userId});

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int movieId = cursor.getInt(cursor.getColumnIndex(COLUMN_MOVIE_ID));
                @SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE));
                @SuppressLint("Range") String date = cursor.getString(cursor.getColumnIndex(COLUMN_DATE));
                @SuppressLint("Range") double rate = cursor.getDouble(cursor.getColumnIndex(COLUMN_RATE));
                @SuppressLint("Range") String posterUrl = cursor.getString(cursor.getColumnIndex(COLUMN_POSTER_URL));

                MovieItem movie = new MovieItem();
                movie.setId(movieId);
                movie.setTitle(title);
                movie.setReleaseDate(date);
                movie.setVoteAverage(rate);
                movie.setBackdropPath(posterUrl);

                favoriteMovies.add(movie);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return favoriteMovies;
    }

    // Check if a movie is in the favorites list for a specific user
    public boolean isFavorite(String userId, int movieId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_FAVORITES + " WHERE " + COLUMN_USER_ID + " = ? AND " + COLUMN_MOVIE_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{userId, String.valueOf(movieId)});

        boolean isFavorite = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return isFavorite;
    }
}
