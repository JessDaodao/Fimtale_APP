package com.app.fimtale.model;

import com.google.gson.annotations.SerializedName;

public class Topic {
    @SerializedName("ID")
    private int id;
    @SerializedName("Title")
    private String title;
    @SerializedName("UserName")
    private String authorName;
    @SerializedName("Background")
    private String background;

    @SerializedName("Intro")
    private String intro;

    @SerializedName("Tags")
    private Tags tags;

    // --- Getters ---
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthorName() { return authorName; }
    public String getBackground() { return background; }
    public String getIntro() { return intro; }
    public Tags getTags() { return tags; }
}