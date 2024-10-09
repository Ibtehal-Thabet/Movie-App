package com.example.movieapp.domain.cast;

import com.example.movieapp.domain.cast.CastItem;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CastResponse {
    @SerializedName("cast")
    @Expose
    private List<CastItem> cast;

    public List<CastItem> getCast() {
        return cast;
    }

    public void setCast(List<CastItem> cast) {
        this.cast = cast;
    }
}
