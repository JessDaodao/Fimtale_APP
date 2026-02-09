package com.app.fimtale.model;

import com.google.gson.annotations.SerializedName;

public class AuthorInfo {
    @SerializedName("ID")
    private int id;
    @SerializedName("UserName")
    private String userName;
    @SerializedName("Background")
    private String background;

    public int getId() { return id; }
    public String getUserName() { return userName; }
    public String getBackground() { return background; }
}