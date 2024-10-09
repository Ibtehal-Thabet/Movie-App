package com.example.movieapp.domain.cast;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CastItem {
    @SerializedName("adult")
    @Expose
    private boolean adult;
    @SerializedName("gender")
    @Expose
    private int gender;
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("known_for_department")
    @Expose
    private String knownForDepartment;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("original_name")
    @Expose
    private String originalName;
    @SerializedName("popularity")
    @Expose
    private double popularity;
    @SerializedName("profile_path")
    @Expose
    private String profilePath;
    @SerializedName("cast_id")
    @Expose
    private int castId;
    @SerializedName("character")
    @Expose
    private String character;
    @SerializedName("credit_id")
    @Expose
    private String creditId;
    @SerializedName("order")
    @Expose
    private int order;

    public String getDepartment() {
        return knownForDepartment;
    }

    public String getOriginalName() {
        return originalName;
    }

    public String getName() {
        return name;
    }

    public int getGender() {
        return gender;
    }

    public String getProfilePath() {
        return profilePath;
    }

    public int getCastId() {
        return castId;
    }

    public String getCharacter() {
        return character;
    }

    public Object getPopularity() {
        return popularity;
    }

    public String getCreditID() {
        return creditId;
    }

    public int getId() {
        return id;
    }

    public boolean isAdult() {
        return adult;
    }

    public int getOrder() {
        return order;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public void setProfilePath(String profilePath) {
        this.profilePath = profilePath;
    }

    public void setCastId(int castId) {
        this.castId = castId;
    }

    public void setCharacter(String character) {
        this.character = character;
    }

    public void setCreditID(String creditId) {
        this.creditId = creditId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
