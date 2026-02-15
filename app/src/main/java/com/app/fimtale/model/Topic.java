package com.app.fimtale.model;

import com.google.gson.annotations.SerializedName;

public class Topic {
    @SerializedName("ID")
    private int id;
    @SerializedName("Title")
    private String title;
    @SerializedName("UserName")
    private String authorName;
    @SerializedName("Background")
    private String background;

    @SerializedName("Intro")
    private String intro;

    @SerializedName("Tags")
    private Tags tags;

    @SerializedName("Views")
    private int views;
    
    @SerializedName("Comments")
    private int comments;
    
    @SerializedName("Followers")
    private int followers;
    
    @SerializedName("WordCount")
    private int wordCount;

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthorName() { return authorName; }
    public String getBackground() { return background; }
    public String getIntro() { return intro; }
    public Tags getTags() { return tags; }
    public int getViews() { return views; }
    public int getComments() { return comments; }
    public int getFollowers() { return followers; }
    public int getWordCount() { return wordCount; }
    
    public void setId(int id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }
    public void setBackground(String background) { this.background = background; }
    public void setIntro(String intro) { this.intro = intro; }
    public void setTags(Tags tags) { this.tags = tags; }
    public void setViews(int views) { this.views = views; }
    public void setComments(int comments) { this.comments = comments; }
    public void setFollowers(int followers) { this.followers = followers; }
    public void setWordCount(int wordCount) { this.wordCount = wordCount; }
}
