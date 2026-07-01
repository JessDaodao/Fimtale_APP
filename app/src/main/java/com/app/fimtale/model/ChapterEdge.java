package com.app.fimtale.model;

import com.google.gson.annotations.SerializedName;

/**
 * 章节间的跳转边（分支/选项标签）
 */
public class ChapterEdge {
    @SerializedName("from_chapter_id")
    private long fromChapterId;
    @SerializedName("to_chapter_id")
    private long toChapterId;
    private String label;

    public long getFromChapterId() { return fromChapterId; }
    public long getToChapterId() { return toChapterId; }
    public String getLabel() { return label; }
}
