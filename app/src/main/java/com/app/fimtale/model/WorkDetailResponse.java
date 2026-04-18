package com.app.fimtale.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class WorkDetailResponse {
    private int code;
    private String message;
    private Data data;
    private int duration;

    public int getCode() { return code; }
    public String getMessage() { return message; }
    public Data getData() { return data; }
    public int getDuration() { return duration; }

    public static class Data {
        private User user;
        private Work work;
        private List<Chapter> chapters;
        @SerializedName("user_operations")
        private List<UserOperation> userOperations;
        @SerializedName("user_favs")
        private List<UserFav> userFavs;

        public User getUser() { return user; }
        public Work getWork() { return work; }
        public List<Chapter> getChapters() { return chapters; }
        public List<UserOperation> getUserOperations() { return userOperations; }
        public List<UserFav> getUserFavs() { return userFavs; }
    }

    public static class User {
        @SerializedName("user_id")
        private Integer userId;
        private String username;
        @SerializedName("user_avatar")
        private String userAvatar;
        private List<Badge> badges;
        private Integer level;

        public Integer getUserId() { return userId; }
        public String getUsername() { return username; }
        public String getUserAvatar() { return userAvatar; }
        public List<Badge> getBadges() { return badges; }
        public Integer getLevel() { return level; }
    }

    public static class Badge {
        private Integer id;
        private String name;
        private String color;
        private String type;

        public Integer getId() { return id; }
        public String getName() { return name; }
        public String getColor() { return color; }
        public String getType() { return type; }
    }

    public static class Work {
        private Integer id;
        private String title;
        private Integer type;
        private Integer length;
        private Integer rating;
        private Integer publish;
        private Integer origin;
        private String intro;
        private String cover;
        private String preface;
        @SerializedName("origin_link")
        private String originLink;
        @SerializedName("prequel_id")
        private Integer prequelId;
        @SerializedName("created_at")
        private String createdAt;
        @SerializedName("last_chapter_id")
        private Integer lastChapterId;
        @SerializedName("last_chapter_title")
        private String lastChapterTitle;
        @SerializedName("last_chapter_at")
        private String lastChapterAt;
        @SerializedName("commented_at")
        private String commentedAt;
        @SerializedName("last_user_id")
        private Integer lastUserId;
        @SerializedName("last_username")
        private String lastUsername;
        @SerializedName("count_character")
        private Integer countCharacter;
        @SerializedName("count_image")
        private Integer countImage;
        @SerializedName("count_chapter")
        private Integer countChapter;
        @SerializedName("count_like")
        private Integer countLike;
        @SerializedName("count_dislike")
        private Integer countDislike;
        @SerializedName("count_high_praise")
        private Integer countHighPraise;
        @SerializedName("count_fav")
        private Integer countFav;
        @SerializedName("count_comment")
        private Integer countComment;
        @SerializedName("count_download")
        private Integer countDownload;
        @SerializedName("count_view")
        private Integer countView;
        private List<TagGroup> tags;

        public Integer getId() { return id; }
        public String getTitle() { return title; }
        public Integer getType() { return type; }
        public Integer getLength() { return length; }
        public Integer getRating() { return rating; }
        public Integer getPublish() { return publish; }
        public Integer getOrigin() { return origin; }
        public String getIntro() { return intro; }
        public String getCover() { return cover; }
        public String getPreface() { return preface; }
        public String getOriginLink() { return originLink; }
        public Integer getPrequelId() { return prequelId; }
        public String getCreatedAt() { return createdAt; }
        public Integer getLastChapterId() { return lastChapterId; }
        public String getLastChapterTitle() { return lastChapterTitle; }
        public String getLastChapterAt() { return lastChapterAt; }
        public String getCommentedAt() { return commentedAt; }
        public Integer getLastUserId() { return lastUserId; }
        public String getLastUsername() { return lastUsername; }
        public Integer getCountCharacter() { return countCharacter; }
        public Integer getCountImage() { return countImage; }
        public Integer getCountChapter() { return countChapter; }
        public Integer getCountLike() { return countLike; }
        public Integer getCountDislike() { return countDislike; }
        public Integer getCountHighPraise() { return countHighPraise; }
        public Integer getCountFav() { return countFav; }
        public Integer getCountComment() { return countComment; }
        public Integer getCountDownload() { return countDownload; }
        public Integer getCountView() { return countView; }
        public List<TagGroup> getTags() { return tags; }
    }

    public static class TagGroup {
        private Integer id;
        private String name;
        private Integer order;
        @SerializedName("limit_min")
        private Integer limitMin;
        @SerializedName("limit_max")
        private Integer limitMax;
        @SerializedName("bg_color")
        private String bgColor;
        @SerializedName("text_color")
        private String textColor;
        private List<Tag> tags;

        public Integer getId() { return id; }
        public String getName() { return name; }
        public Integer getOrder() { return order; }
        public Integer getLimitMin() { return limitMin; }
        public Integer getLimitMax() { return limitMax; }
        public String getBgColor() { return bgColor; }
        public String getTextColor() { return textColor; }
        public List<Tag> getTags() { return tags; }
    }

    public static class Tag {
        private Integer id;
        private String name;
        @SerializedName("tag_type_id")
        private Integer tagTypeId;
        @SerializedName("bg_color")
        private String bgColor;
        @SerializedName("text_color")
        private String textColor;
        private String intro;
        @SerializedName("created_at")
        private String createdAt;
        @SerializedName("updated_at")
        private String updatedAt;
        private String icon;
        @SerializedName("update_user_id")
        private Integer updateUserId;
        @SerializedName("count_fav")
        private Integer countFav;
        @SerializedName("status_enable")
        private Boolean statusEnable;

        public Integer getId() { return id; }
        public String getName() { return name; }
        public Integer getTagTypeId() { return tagTypeId; }
        public String getBgColor() { return bgColor; }
        public String getTextColor() { return textColor; }
        public String getIntro() { return intro; }
        public String getCreatedAt() { return createdAt; }
        public String getUpdatedAt() { return updatedAt; }
        public String getIcon() { return icon; }
        public Integer getUpdateUserId() { return updateUserId; }
        public Integer getCountFav() { return countFav; }
        public Boolean getStatusEnable() { return statusEnable; }
    }

    public static class Chapter {
        private Integer id;
        @SerializedName("work_id")
        private Integer workId;
        private String title;
        @SerializedName("order_num")
        private Integer orderNum;
        @SerializedName("created_at")
        private String createdAt;
        @SerializedName("edited_at")
        private String editedAt;
        @SerializedName("status_del")
        private Integer statusDel;
        @SerializedName("status_commentable")
        private Boolean statusCommentable;
        @SerializedName("status_review")
        private Integer statusReview;

        public Integer getId() { return id; }
        public Integer getWorkId() { return workId; }
        public String getTitle() { return title; }
        public Integer getOrderNum() { return orderNum; }
        public String getCreatedAt() { return createdAt; }
        public String getEditedAt() { return editedAt; }
        public Integer getStatusDel() { return statusDel; }
        public Boolean getStatusCommentable() { return statusCommentable; }
        public Integer getStatusReview() { return statusReview; }
    }

    public static class UserOperation {
        private Integer id;
        @SerializedName("user_id")
        private Integer userId;
        @SerializedName("work_id")
        private Integer workId;
        private Integer operation;
        @SerializedName("created_at")
        private String createdAt;
        @SerializedName("created_ip")
        private String createdIp;

        public Integer getId() { return id; }
        public Integer getUserId() { return userId; }
        public Integer getWorkId() { return workId; }
        public Integer getOperation() { return operation; }
        public String getCreatedAt() { return createdAt; }
        public String getCreatedIp() { return createdIp; }
    }

    public static class UserFav {
        private Integer id;
        @SerializedName("user_id")
        private Integer userId;
        @SerializedName("work_id")
        private Integer workId;
        @SerializedName("folder_id")
        private Integer folderId;
        @SerializedName("created_at")
        private String createdAt;

        public Integer getId() { return id; }
        public Integer getUserId() { return userId; }
        public Integer getWorkId() { return workId; }
        public Integer getFolderId() { return folderId; }
        public String getCreatedAt() { return createdAt; }
    }
}
