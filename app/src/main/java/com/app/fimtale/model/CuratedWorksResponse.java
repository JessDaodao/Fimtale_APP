package com.app.fimtale.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class CuratedWorksResponse {
    @SerializedName("code")
    private int code;

    @SerializedName("data")
    private Data data;

    public int getCode() { return code; }
    public Data getData() { return data; }

    public static class Data {
        @SerializedName("curated_works")
        private List<CuratedWork> curatedWorks;

        public List<CuratedWork> getCuratedWorks() { return curatedWorks; }
    }

    public static class CuratedWork {
        @SerializedName("id")
        private int id;

        @SerializedName("work")
        private Work work;

        @SerializedName("reason")
        private String reason;

        @SerializedName("user")
        private User user;

        public int getId() { return id; }
        public Work getWork() { return work; }
        public String getReason() { return reason; }
        public User getUser() { return user; }
    }

    public static class User {
        @SerializedName("username")
        private String username;

        public String getUsername() { return username; }
    }
}
