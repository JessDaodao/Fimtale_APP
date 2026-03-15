package com.app.fimtale.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class WorkDetailResponse {
    @SerializedName("code")
    private int code;

    @SerializedName("data")
    private Data data;

    public int getCode() { return code; }
    public Data getData() { return data; }

    public static class Data {
        @SerializedName("work")
        private Work work;

        @SerializedName("user")
        private User user;

        @SerializedName("chapters")
        private List<Chapter> chapters;

        public Work getWork() { return work; }
        public User getUser() { return user; }
        public List<Chapter> getChapters() { return chapters; }
    }

    public static class User {
        @SerializedName("user_id")
        private int userId;
        @SerializedName("username")
        private String username;
        @SerializedName("user_avatar")
        private String userAvatar;

        public int getUserId() { return userId; }
        public String getUsername() { return username; }
        public String getUserAvatar() { return userAvatar; }
    }

    public static class Chapter {
        @SerializedName("id")
        private int id;
        @SerializedName("work_id")
        private int workId;
        @SerializedName("title")
        private String title;
        @SerializedName("order_num")
        private int orderNum;

        public int getId() { return id; }
        public int getWorkId() { return workId; }
        public String getTitle() { return title; }
        public int getOrderNum() { return orderNum; }
    }
}
