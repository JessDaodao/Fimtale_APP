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

    @SerializedName("RecommenderName")
    private String recommenderName;

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

    public String getRecommenderName() {
        return recommenderName;
    }

    public RecommendedTopic() {}

    public RecommendedTopic(CuratedWorksResponse.CuratedWork curatedWork) {
        if (curatedWork.getWork() != null) {
            this.id = curatedWork.getWork().getId();
            this.title = curatedWork.getWork().getTitle();
            this.authorName = curatedWork.getWork().getUsername();
            this.background = curatedWork.getWork().getCover();
        }
        this.recommendWord = curatedWork.getReason();
        if (curatedWork.getUser() != null) {
            this.recommenderName = curatedWork.getUser().getUsername();
        } else {
            this.recommenderName = "";
        }
    }
}
