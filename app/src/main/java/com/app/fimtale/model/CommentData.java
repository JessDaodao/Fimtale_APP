package com.app.fimtale.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * 评论数据（对应 vo.CommentWithAuthor）
 */
public class CommentData {
    private int id;
    @SerializedName("work_id")
    private int workId;
    @SerializedName("chapter_id")
    private int chapterId;
    @SerializedName("reply_comment_id")
    private int replyCommentId;
    @SerializedName("user_id")
    private int userId;
    private String content;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("edited_at")
    private String editedAt;
    @SerializedName("status_del")
    private int statusDel;
    @SerializedName("status_visibility")
    private int statusVisibility;
    @SerializedName("status_top")
    private boolean statusTop;
    @SerializedName("count_like")
    private int countLike;
    @SerializedName("count_dislike")
    private int countDislike;
    @SerializedName("score_like")
    private double scoreLike;
    @SerializedName("count_fav")
    private int countFav;
    @SerializedName("count_star_honor")
    private int countStarHonor;

    // 作者卡片
    private UserCard user;
    @SerializedName("chapter_title")
    private String chapterTitle;

    public int getId() { return id; }
    public int getWorkId() { return workId; }
    public int getChapterId() { return chapterId; }
    public int getUserId() { return userId; }
    public String getContent() { return content; }
    public String getCreatedAt() { return createdAt; }
    public int getCountLike() { return countLike; }
    public int getCountDislike() { return countDislike; }
    public UserCard getUser() { return user; }
    public String getChapterTitle() { return chapterTitle; }

    public static class UserCard {
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
        public int getLevel() { return level; }
    }

    public static class Badge {
        private int id;
        private String name;
        private String color;
        private String type;

        public int getId() { return id; }
        public String getName() { return name; }
        public String getColor() { return color; }
    }
}
