package com.app.fimtale.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class TopicTags {
    @SerializedName("Type")
    private String type;
    @SerializedName("Source")
    private String source;
    @SerializedName("Rating")
    private String rating;
    @SerializedName("Length")
    private String length;
    @SerializedName("Status")
    private String status;
    @SerializedName("OtherTags")
    private List<String> otherTags;

    public String getType() { return type; }
    public String getSource() { return source; }
    public String getRating() { return rating; }
    public String getLength() { return length; }
    public String getStatus() { return status; }
    public List<String> getOtherTags() { return otherTags; }
}