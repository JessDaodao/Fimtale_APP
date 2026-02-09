package com.app.fimtale.model;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Tags {
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
}