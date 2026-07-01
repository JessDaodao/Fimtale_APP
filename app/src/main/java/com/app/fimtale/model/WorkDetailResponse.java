package com.app.fimtale.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * GetWork API 响应 data 部分
 * 外层由 ApiResponse<Data> 包裹
 */
public class WorkDetailResponse {

    public static class Data {
        private User user;
        private Work work;
        private List<SimpleChapter> chapters;
        @SerializedName("chapter_edges")
        private List<ChapterEdge> chapterEdges;
        @SerializedName("curation_records")
        private List<CuratedWork> curationRecords;
        @SerializedName("user_operations")
        private List<WorkOperation> userOperations;
        @SerializedName("user_favs")
        private List<UserWorkFav> userFavs;
        private Work prequel;
        private List<Work> sequels;

        public User getUser() { return user; }
        public Work getWork() { return work; }
        public List<SimpleChapter> getChapters() { return chapters; }
        public List<ChapterEdge> getChapterEdges() { return chapterEdges; }
        public List<WorkOperation> getUserOperations() { return userOperations; }
        public List<UserWorkFav> getUserFavs() { return userFavs; }
        public Work getPrequel() { return prequel; }
        public List<Work> getSequels() { return sequels; }
    }

    /** 作者卡片 */
    public static class User {
        @SerializedName("user_id")
        private int userId;
        private String username;
        @SerializedName("user_avatar")
        private String userAvatar;
        private List<Badge> badges;
        private int level;

        public int getUserId() { return userId; }
        public String getUsername() { return username; }
        public String getUserAvatar() { return userAvatar; }
        public List<Badge> getBadges() { return badges; }
        public int getLevel() { return level; }
    }

    public static class Badge {
        private int id;
        private String name;
        private String color;
        private String type;
    }

    /** 作品（对应后端 vo.Work） */
    public static class Work {
        private int id;
        private String title;
        private String intro;
        private String cover;
        private String preface;
        @SerializedName("origin_link")
        private String originLink;
        @SerializedName("prequel_id")
        private int prequelId;
        @SerializedName("created_at")
        private String createdAt;
        private int type;
        private int length;
        private int rating;
        private int publish;
        private int origin;
        @SerializedName("ranking_bias")
        private int rankingBias;
        @SerializedName("status_visibility")
        private int statusVisibility;
        @SerializedName("status_review")
        private int statusReview;
        @SerializedName("status_del")
        private int statusDel;
        @SerializedName("status_commentable")
        private boolean statusCommentable;
        @SerializedName("last_chapter_id")
        private int lastChapterId;
        @SerializedName("last_chapter_title")
        private String lastChapterTitle;
        @SerializedName("last_chapter_at")
        private String lastChapterAt;
        @SerializedName("count_character")
        private int countCharacter;
        @SerializedName("count_image")
        private int countImage;
        @SerializedName("count_chapter")
        private int countChapter;
        @SerializedName("count_like")
        private int countLike;
        @SerializedName("count_dislike")
        private int countDislike;
        @SerializedName("count_high_praise")
        private int countHighPraise;
        @SerializedName("count_fav")
        private int countFav;
        @SerializedName("count_comment")
        private int countComment;
        @SerializedName("count_download")
        private int countDownload;
        @SerializedName("count_view")
        private int countView;
        private List<String> hashtags;
        private List<TagGroup> tags;

        public int getId() { return id; }
        public String getTitle() { return title; }
        public String getIntro() { return intro; }
        public String getCover() { return cover; }
        public String getPreface() { return preface; }
        public int getType() { return type; }
        public int getLength() { return length; }
        public int getRating() { return rating; }
        public int getPublish() { return publish; }
        public int getOrigin() { return origin; }
        public String getCreatedAt() { return createdAt; }
        public int getLastChapterId() { return lastChapterId; }
        public String getLastChapterTitle() { return lastChapterTitle; }
        public int getCountCharacter() { return countCharacter; }
        public int getCountChapter() { return countChapter; }
        public int getCountLike() { return countLike; }
        public int getCountDislike() { return countDislike; }
        public int getCountHighPraise() { return countHighPraise; }
        public int getCountFav() { return countFav; }
        public int getCountComment() { return countComment; }
        public int getCountDownload() { return countDownload; }
        public int getCountView() { return countView; }
        public List<String> getHashtags() { return hashtags; }
        public List<TagGroup> getTags() { return tags; }
    }

    /** 目录项（不含正文） */
    public static class SimpleChapter {
        private int id;
        @SerializedName("work_id")
        private int workId;
        private String title;
        @SerializedName("order_num")
        private int orderNum;
        @SerializedName("created_at")
        private String createdAt;
        @SerializedName("edited_at")
        private String editedAt;
        @SerializedName("status_del")
        private int statusDel;

        public int getId() { return id; }
        public int getWorkId() { return workId; }
        public String getTitle() { return title; }
        public int getOrderNum() { return orderNum; }
        public String getCreatedAt() { return createdAt; }
        public String getEditedAt() { return editedAt; }
        public int getStatusDel() { return statusDel; }
    }

    public static class TagGroup {
        private int id;
        private String name;
        private int order;
        @SerializedName("limit_min")
        private int limitMin;
        @SerializedName("limit_max")
        private int limitMax;
        @SerializedName("bg_color")
        private String bgColor;
        @SerializedName("text_color")
        private String textColor;
        private List<Tag> tags;

        public int getId() { return id; }
        public String getName() { return name; }
        public List<Tag> getTags() { return tags; }
    }

    public static class Tag {
        private int id;
        private String name;
        @SerializedName("tag_type_id")
        private int tagTypeId;
        @SerializedName("bg_color")
        private String bgColor;
        @SerializedName("text_color")
        private String textColor;

        public int getId() { return id; }
        public String getName() { return name; }
        public int getTagTypeId() { return tagTypeId; }
        public String getBgColor() { return bgColor; }
        public String getTextColor() { return textColor; }
    }

    /** 用户对作品的收藏关系 */
    public static class UserWorkFav {
        private int id;
        @SerializedName("user_id")
        private int userId;
        @SerializedName("work_id")
        private int workId;
        @SerializedName("folder_id")
        private int folderId;
        @SerializedName("created_at")
        private String createdAt;

        public int getId() { return id; }
        public int getWorkId() { return workId; }
    }

    /** 推荐记录 */
    public static class CuratedWork {
        private int id;
        @SerializedName("work_id")
        private int workId;
        private String reason;
        @SerializedName("created_at")
        private String createdAt;

        public int getWorkId() { return workId; }
        public String getReason() { return reason; }
    }
}
