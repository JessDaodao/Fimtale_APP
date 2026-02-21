package com.app.fimtale.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class TagListResponse {
    @SerializedName("Status")
    private int status;

    @SerializedName("TagArray")
    private List<TagInfo> tagArray;

    @SerializedName("Page")
    private int page;

    @SerializedName("TotalPage")
    private int totalPage;

    public int getStatus() { return status; }
    public List<TagInfo> getTagArray() { return tagArray; }
    public int getPage() { return page; }
    public int getTotalPage() { return totalPage; }
}
