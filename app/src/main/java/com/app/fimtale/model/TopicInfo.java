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
    @SerializedName("ViewCount")
    private int viewCount;
    @SerializedName("CommentCount")
    private int commentCount;
    @SerializedName("FavoriteCount")
    private int favoriteCount;
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
    public int getViewCount() { return viewCount; }
    public int getCommentCount() { return commentCount; }
    public int getFavoriteCount() { return favoriteCount; }
    public String getIntro() { return intro; }
    public TopicTags getTags() { return tags; }
    public Object getBranches() { return branches; }
    
    public void setId(int id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }
    public void setBackground(String background) { this.background = background; }
    public void setContent(String content) { this.content = content; }
    public void setWordCount(int wordCount) { this.wordCount = wordCount; }
    public void setViewCount(int viewCount) { this.viewCount = viewCount; }
    public void setCommentCount(int commentCount) { this.commentCount = commentCount; }
    public void setFavoriteCount(int favoriteCount) { this.favoriteCount = favoriteCount; }
    public void setIntro(String intro) { this.intro = intro; }
    public void setTags(TopicTags tags) { this.tags = tags; }
    public void setBranches(Object branches) { this.branches = branches; }
}
