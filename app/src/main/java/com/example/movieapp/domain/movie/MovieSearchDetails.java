package com.example.movieapp.domain.movie;

public class MovieSearchDetails {
    String title, overview, date, image;
    int id;

    public MovieSearchDetails() {
    }

    public MovieSearchDetails(String title, String overview, String date, String image, int id) {
        this.title = title;
        this.overview = overview;
        this.date = date;
        this.image = image;
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
