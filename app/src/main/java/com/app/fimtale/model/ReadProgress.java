package com.app.fimtale.model;

import com.google.gson.annotations.SerializedName;

/**
 * 阅读进度（对应后端 po.UserReadProgress）
 */
public class ReadProgress {
    private int id;
    @SerializedName("user_id")
    private int userId;
    @SerializedName("work_id")
    private int workId;
    @SerializedName("chapter_id")
    private Integer chapterId;  // null 表示进度挂在作品本身
    private float progress;     // 0.0 ~ 1.0
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("updated_at")
    private String updatedAt;

    /** 扩展字段：后端 ReadProgressRes 额外返回 title */
    private String title;

    public int getId() { return id; }
    public int getUserId() { return userId; }
    public int getWorkId() { return workId; }
    public Integer getChapterId() { return chapterId; }
    public float getProgress() { return progress; }
    public String getCreatedAt() { return createdAt; }
    public String getUpdatedAt() { return updatedAt; }
    public String getTitle() { return title; }
}
