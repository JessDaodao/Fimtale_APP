package com.app.fimtale.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class FavoritesResponse {
    @SerializedName("Status")
    private int status;

    @SerializedName("Folders")
    private List<Object> folders;

    @SerializedName("Page")
    private int page;

    @SerializedName("TotalPage")
    private int totalPage;

    @SerializedName("TopicArray")
    private List<Topic> topicArray;

    @SerializedName("CurrentUser")
    private MainPageResponse.CurrentUser currentUser;

    @SerializedName("RequestTime")
    private long requestTime;

    @SerializedName("TotalDuration")
    private double totalDuration;

    public int getStatus() { return status; }
    public List<Object> getFolders() { return folders; }
    public int getPage() { return page; }
    public int getTotalPage() { return totalPage; }
    public List<Topic> getTopicArray() { return topicArray; }
    public MainPageResponse.CurrentUser getCurrentUser() { return currentUser; }
    public long getRequestTime() { return requestTime; }
    public double getTotalDuration() { return totalDuration; }
}
