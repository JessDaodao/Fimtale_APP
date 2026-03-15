package com.app.fimtale.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class WorkFeedResponse {
    @SerializedName("code")
    private int code;

    @SerializedName("data")
    private Data data;

    public int getCode() { return code; }
    public Data getData() { return data; }

    public static class Data {
        @SerializedName("works")
        private List<Work> works;

        public List<Work> getWorks() { return works; }
    }
}
