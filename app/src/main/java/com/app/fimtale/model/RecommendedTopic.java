package com.app.fimtale.model;

import com.google.gson.annotations.SerializedName;

// 这个类对应 EditorRecommendTopicArray 里的每一个元素
public class RecommendedTopic {

    @SerializedName("ID")
    private int id;

    @SerializedName("Title")
    private String title;

    @SerializedName("AuthorName")
    private String authorName;

    @SerializedName("Background")
    private String background;

    @SerializedName("RecommendWord")
    private String recommendWord;

    // Getters
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getBackground() {
        return background;
    }
    public String getRecommendWord() {
        return recommendWord;
    }
}