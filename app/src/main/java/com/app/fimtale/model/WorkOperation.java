package com.app.fimtale.model;

import com.google.gson.annotations.SerializedName;

/**
 * 用户对作品的操作（点赞/点踩/HP）
 * operation: 1=点赞, 2=点踩, 3=高度评价(HP)
 */
public class WorkOperation {
    private int id;
    @SerializedName("user_id")
    private int userId;
    @SerializedName("work_id")
    private int workId;
    private int operation;
    @SerializedName("created_at")
    private String createdAt;

    public int getId() { return id; }
    public int getUserId() { return userId; }
    public int getWorkId() { return workId; }
    public int getOperation() { return operation; }
    public String getCreatedAt() { return createdAt; }
}
