package com.app.fimtale.model;

import com.google.gson.annotations.SerializedName;

public class ChapterMenuItem {
    @SerializedName("ID")
    private int id;

    @SerializedName("Title")
    private String title;

    public int getId() { return id; }
    public String getTitle() { return title; }
    
    public void setId(int id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
}
