package com.app.fimtale.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class TagInfo {
    @SerializedName("ID")
    private int id;

    @SerializedName("Name")
    private String name;

    @SerializedName("Followers")
    private int followers;

    @SerializedName("IconExists")
    private boolean iconExists;

    @SerializedName("Intro")
    private String intro;

    @SerializedName("TotalTopics")
    private int totalTopics;

    @SerializedName("LastTime")
    private long lastTime;

    @SerializedName("IsFavorite")
    private boolean isFavorite;

    @SerializedName("AllowedOptions")
    private List<String> allowedOptions;

    public int getId() { return id; }
    public String getName() { return name; }
    public int getFollowers() { return followers; }
    public boolean isIconExists() { return iconExists; }
    public String getIntro() { return intro; }
    public int getTotalTopics() { return totalTopics; }
    public long getLastTime() { return lastTime; }
    public boolean isFavorite() { return isFavorite; }
    public List<String> getAllowedOptions() { return allowedOptions; }
}
