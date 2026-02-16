package com.app.fimtale.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class TopicTags {
    @SerializedName("Type")
    private String type;
    @SerializedName("Source")
    private String source;
    @SerializedName("Rate")
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
    
    public void setType(String type) { this.type = type; }
    public void setSource(String source) { this.source = source; }
    public void setRating(String rating) { this.rating = rating; }
    public void setLength(String length) { this.length = length; }
    public void setStatus(String status) { this.status = status; }
    public void setOtherTags(List<String> otherTags) { this.otherTags = otherTags; }
}