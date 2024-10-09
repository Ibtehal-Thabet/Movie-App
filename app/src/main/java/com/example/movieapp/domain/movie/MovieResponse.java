package com.example.movieapp.domain.movie;

import com.example.movieapp.domain.movie.MovieItem;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MovieResponse {

    //	@SerializedName("dates")
//	@Expose
//	private Dates dates;
    @SerializedName("page")
    @Expose
    private int page;
    @SerializedName("results")
    @Expose
    private List<MovieItem> movies;
    @SerializedName("total_pages")
    @Expose
    private int totalPages;
    @SerializedName("total_results")
    @Expose
    private int totalResults;

    public int getPage() {
        return page;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public List<MovieItem> getMovies() {
        return movies;
    }

    public int getTotalResults() {
        return totalResults;
    }
}