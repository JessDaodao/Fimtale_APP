package com.app.fimtale.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class TopicInfo {
    @SerializedName("ID")
    private int id;
    @SerializedName("Title")
    private String title;
    @SerializedName("UserName")
    private String authorName;
    @SerializedName("Background")
    private String background;
    @SerializedName("Content")
    private String content;
    @SerializedName("WordCount")
    private int wordCount;
    @SerializedName("Intro")
    private String intro;

    @SerializedName("Tags")
    private TopicTags tags;
    @SerializedName("Branches")
    private Object branches;

    // Getters
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthorName() { return authorName; }
    public String getBackground() { return background; }
    public String getContent() { return content; }
    public int getWordCount() { return wordCount; }
    public String getIntro() { return intro; }
    public TopicTags getTags() { return tags; }
    public Object getBranches() { return branches; }
}