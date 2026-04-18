package com.app.fimtale.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class WorkChapterResponse {
    private int code;
    private String message;
    private Data data;
    private int duration;

    public int getCode() { return code; }
    public String getMessage() { return message; }
    public Data getData() { return data; }
    public int getDuration() { return duration; }

    public static class Data {
        private WorkDetailResponse.User user;
        private Work work;
        private List<WorkDetailResponse.Chapter> chapters;
        private Chapter chapter;
        @SerializedName("user_operations")
        private List<WorkDetailResponse.UserOperation> userOperations;
        @SerializedName("user_favs")
        private List<WorkDetailResponse.UserFav> userFavs;

        public WorkDetailResponse.User getUser() { return user; }
        public Work getWork() { return work; }
        public List<WorkDetailResponse.Chapter> getChapters() { return chapters; }
        public Chapter getChapter() { return chapter; }
        public List<WorkDetailResponse.UserOperation> getUserOperations() { return userOperations; }
        public List<WorkDetailResponse.UserFav> getUserFavs() { return userFavs; }
    }

    public static class Work {
        private Integer id;
        private String title;

        public Integer getId() { return id; }
        public String getTitle() { return title; }
    }

    public static class Chapter {
        private Integer id;
        @SerializedName("work_id")
        private Integer workId;
        private String title;
        private String content;
        @SerializedName("order_num")
        private Integer orderNum;
        @SerializedName("created_at")
        private String createdAt;
        @SerializedName("created_ip")
        private String createdIp;
        @SerializedName("edited_at")
        private String editedAt;
        @SerializedName("count_character")
        private Integer countCharacter;
        @SerializedName("count_image")
        private Integer countImage;
        @SerializedName("count_view")
        private Integer countView;
        @SerializedName("status_del")
        private Integer statusDel;
        @SerializedName("status_commentable")
        private Boolean statusCommentable;
        @SerializedName("status_review")
        private Integer statusReview;

        public Integer getId() { return id; }
        public Integer getWorkId() { return workId; }
        public String getTitle() { return title; }
        public String getContent() { return content; }
        public Integer getOrderNum() { return orderNum; }
        public String getCreatedAt() { return createdAt; }
        public String getCreatedIp() { return createdIp; }
        public String getEditedAt() { return editedAt; }
        public Integer getCountCharacter() { return countCharacter; }
        public Integer getCountImage() { return countImage; }
        public Integer getCountView() { return countView; }
        public Integer getStatusDel() { return statusDel; }
        public Boolean getStatusCommentable() { return statusCommentable; }
        public Integer getStatusReview() { return statusReview; }
    }
}
