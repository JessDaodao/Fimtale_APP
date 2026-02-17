package com.app.fimtale.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class MainPageResponse {
    @SerializedName("Status")
    private int status;

    @SerializedName("EditorRecommendTopicArray")
    private List<RecommendedTopic> editorRecommendTopicArray;

    @SerializedName("NewlyPostTopicArray")
    private List<Topic> newlyPostTopicArray;

    @SerializedName("NewlyUpdateTopicArray")
    private List<Topic> newlyUpdateTopicArray;

    @SerializedName("Page")
    private int page;

    @SerializedName("TotalPage")
    private int totalPage;

    @SerializedName("CurrentUser")
    private CurrentUser currentUser;

    public int getStatus() { return status; }
    public List<RecommendedTopic> getEditorRecommendTopicArray() { return editorRecommendTopicArray; }
    public List<Topic> getNewlyPostTopicArray() { return newlyPostTopicArray; }
    public List<Topic> getNewlyUpdateTopicArray() { return newlyUpdateTopicArray; }
    public int getPage() { return page; }
    public int getTotalPage() { return totalPage; }
    public CurrentUser getCurrentUser() { return currentUser; }

    public static class CurrentUser {
        @SerializedName("ID")
        private int id;

        @SerializedName("UserName")
        private String userName;

        @SerializedName("Background")
        private String background;

        @SerializedName("UserIntro")
        private String userIntro;

        public int getId() { return id; }
        public String getUserName() { return userName; }
        public String getBackground() { return background; }
        public String getUserIntro() { return userIntro; }
    }
}