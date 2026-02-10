package com.app.fimtale.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class TopicDetailResponse {
    @SerializedName("Status")
    private int status;

    @SerializedName("TopicInfo")
    private TopicInfo topicInfo;

    @SerializedName("AuthorInfo")
    private AuthorInfo authorInfo;

    @SerializedName("ParentInfo")
    private TopicInfo parentInfo;

    @SerializedName("Menu")
    private List<ChapterMenuItem> menu;

    public int getStatus() { return status; }
    public TopicInfo getTopicInfo() { return topicInfo; }
    public AuthorInfo getAuthorInfo() { return authorInfo; }
    public TopicInfo getParentInfo() { return parentInfo; }
    public List<ChapterMenuItem> getMenu() { return menu; }
    
    public void setStatus(int status) { this.status = status; }
    public void setTopicInfo(TopicInfo topicInfo) { this.topicInfo = topicInfo; }
    public void setAuthorInfo(AuthorInfo authorInfo) { this.authorInfo = authorInfo; }
    public void setParentInfo(TopicInfo parentInfo) { this.parentInfo = parentInfo; }
    public void setMenu(List<ChapterMenuItem> menu) { this.menu = menu; }
}
