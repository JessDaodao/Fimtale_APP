package com.app.fimtale.model;

import com.google.gson.annotations.SerializedName;

/**
 * 收藏夹文件夹
 */
public class UserFavFolder {
    private int id;
    @SerializedName("user_id")
    private int userId;
    private String name;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("updated_at")
    private String updatedAt;

    public int getId() { return id; }
    public int getUserId() { return userId; }
    public String getName() { return name; }
    public String getCreatedAt() { return createdAt; }
    public String getUpdatedAt() { return updatedAt; }
}
