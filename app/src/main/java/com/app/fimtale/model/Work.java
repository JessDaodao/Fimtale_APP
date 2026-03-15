package com.app.fimtale.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Work {
    @SerializedName("id")
    private int id;

    @SerializedName("user_id")
    private int userId;

    @SerializedName("username")
    private String username;

    @SerializedName("user_avatar")
    private String userAvatar;

    @SerializedName("title")
    private String title;

    @SerializedName("type")
    private int type;

    @SerializedName("length")
    private int length;

    @SerializedName("rating")
    private int rating;

    @SerializedName("publish")
    private int publish;

    @SerializedName("origin")
    private int origin;

    @SerializedName("intro")
    private String intro;

    @SerializedName("cover")
    private String cover;

    @SerializedName("preface")
    private String preface;

    @SerializedName("count_word")
    private int countWord;

    @SerializedName("count_character")
    private int countCharacter;

    @SerializedName("count_view")
    private int countView;

    @SerializedName("count_comment")
    private int countComment;

    @SerializedName("count_fav")
    private int countFav;

    @SerializedName("count_image")
    private int countImage;

    @SerializedName("count_chapter")
    private int countChapter;

    @SerializedName("tags")
    private List<TagCategory> tags;

    public int getId() { return id; }
    public int getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getUserAvatar() { return userAvatar; }
    public String getTitle() { return title; }
    public int getType() { return type; }
    public int getLength() { return length; }
    public int getRating() { return rating; }
    public int getPublish() { return publish; }
    public int getOrigin() { return origin; }
    public String getIntro() { return intro; }
    public String getCover() { return cover; }
    public String getPreface() { return preface; }
    public int getCountWord() { return countWord > 0 ? countWord : countCharacter; }
    public int getCountView() { return countView; }
    public int getCountComment() { return countComment; }
    public int getCountFav() { return countFav; }
    public int getCountImage() { return countImage; }
    public int getCountChapter() { return countChapter; }
    public List<TagCategory> getTags() { return tags; }

    public static class TagCategory {
        @SerializedName("id")
        private int id;

        @SerializedName("name")
        private String name;

        @SerializedName("tags")
        private List<WorkTagInfo> tags;

        public int getId() { return id; }
        public String getName() { return name; }
        public List<WorkTagInfo> getTags() { return tags; }
    }

    public static class WorkTagInfo {
        @SerializedName("id")
        private int id;

        @SerializedName("name")
        private String name;

        public int getId() { return id; }
        public String getName() { return name; }
    }
}
