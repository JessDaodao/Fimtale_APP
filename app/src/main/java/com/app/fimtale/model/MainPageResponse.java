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

    public int getStatus() { return status; }
    public List<RecommendedTopic> getEditorRecommendTopicArray() { return editorRecommendTopicArray; }
    public List<Topic> getNewlyPostTopicArray() { return newlyPostTopicArray; }
    public List<Topic> getNewlyUpdateTopicArray() { return newlyUpdateTopicArray; }
    public int getPage() { return page; }
    public int getTotalPage() { return totalPage; }
}