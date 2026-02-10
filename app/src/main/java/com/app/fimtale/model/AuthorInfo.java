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
    
    public void setId(int id) { this.id = id; }
    public void setUserName(String userName) { this.userName = userName; }
    public void setBackground(String background) { this.background = background; }
}
