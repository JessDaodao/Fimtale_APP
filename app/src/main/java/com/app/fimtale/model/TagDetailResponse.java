package com.app.fimtale.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class TagDetailResponse {
    @SerializedName("Status")
    private int status;

    @SerializedName("TagInfo")
    private TagInfo tagInfo;

    @SerializedName("TopicArray")
    private List<Topic> topicArray;

    @SerializedName("Page")
    private int page;

    @SerializedName("TotalPage")
    private int totalPage;

    public int getStatus() { return status; }
    public TagInfo getTagInfo() { return tagInfo; }
    public List<Topic> getTopicArray() { return topicArray; }
    public int getPage() { return page; }
    public int getTotalPage() { return totalPage; }
}
