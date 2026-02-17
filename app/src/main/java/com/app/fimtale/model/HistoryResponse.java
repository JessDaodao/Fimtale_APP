package com.app.fimtale.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class HistoryResponse {
    @SerializedName("Status")
    private int status;

    @SerializedName("HistoryTopics")
    private List<HistoryTopic> historyTopics;

    @SerializedName("Page")
    private int page;

    @SerializedName("TotalPage")
    private int totalPage;

    @SerializedName("CurrentUser")
    private MainPageResponse.CurrentUser currentUser;

    public int getStatus() { return status; }
    public List<HistoryTopic> getHistoryTopics() { return historyTopics; }
    public int getPage() { return page; }
    public int getTotalPage() { return totalPage; }
    public MainPageResponse.CurrentUser getCurrentUser() { return currentUser; }

    public static class HistoryTopic {
        @SerializedName("ID")
        private int id;

        @SerializedName("MainID")
        private int mainId;

        @SerializedName("Title")
        private String title;

        @SerializedName("Progress")
        private double progress;

        @SerializedName("DateCreated")
        private long dateCreated;

        public int getId() { return id; }
        public int getMainId() { return mainId; }
        public String getTitle() { return title; }
        public double getProgress() { return progress; }
        public long getDateCreated() { return dateCreated; }
    }
}
