package com.app.fimtale.model;

import com.google.gson.annotations.SerializedName;

public class ChapterDetailResponse {
    @SerializedName("code")
    private int code;

    @SerializedName("data")
    private Data data;

    public int getCode() { return code; }
    public Data getData() { return data; }

    public static class Data {
        @SerializedName("chapter")
        private Chapter chapter;

        public Chapter getChapter() { return chapter; }
    }

    public static class Chapter {
        @SerializedName("id")
        private int id;
        @SerializedName("work_id")
        private int workId;
        @SerializedName("title")
        private String title;
        @SerializedName("content")
        private String content;

        public int getId() { return id; }
        public int getWorkId() { return workId; }
        public String getTitle() { return title; }
        public String getContent() { return content; }
    }
}
