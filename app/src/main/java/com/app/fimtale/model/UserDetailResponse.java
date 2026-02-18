package com.app.fimtale.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.Map;

public class UserDetailResponse {
    @SerializedName("Status")
    private int status;

    @SerializedName("IsUser")
    private boolean isUser;

    @SerializedName("UserInfo")
    private UserInfo userInfo;

    @SerializedName("UserStatInfo")
    private Map<String, Object> userStatInfo;

    public int getStatus() {
        return status;
    }

    public boolean isUser() {
        return isUser;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public Map<String, Object> getUserStatInfo() {
        return userStatInfo;
    }

    public static class UserInfo {
        @SerializedName("ID")
        private int id;

        @SerializedName("UserName")
        private String userName;

        @SerializedName("UserHomepage")
        private String userHomepage;

        @SerializedName("Following")
        private int following;

        @SerializedName("Comments")
        private int comments;

        @SerializedName("Followers")
        private int followers;

        @SerializedName("Background")
        private String background;

        @SerializedName("UserIntro")
        private String userIntro;

        @SerializedName("UserRole")
        private int userRole;

        @SerializedName("Topics")
        private int topics;

        @SerializedName("BlogStatus")
        private int blogStatus;

        @SerializedName("Blogposts")
        private int blogposts;

        @SerializedName("Channels")
        private int channels;

        @SerializedName("Badges")
        private List<String> badges;

        @SerializedName("Medals")
        private List<String> medals;

        @SerializedName("GradeInfo")
        private GradeInfo gradeInfo;

        @SerializedName("Bits")
        private int bits;
        
        @SerializedName("LastSeen")
        private String lastSeen;

        public int getId() {
            return id;
        }

        public String getUserName() {
            return userName;
        }

        public String getUserHomepage() {
            return userHomepage;
        }

        public int getFollowing() {
            return following;
        }

        public int getComments() {
            return comments;
        }

        public int getFollowers() {
            return followers;
        }

        public String getBackground() {
            return background;
        }

        public String getUserIntro() {
            return userIntro;
        }

        public int getUserRole() {
            return userRole;
        }

        public int getTopics() {
            return topics;
        }

        public List<String> getBadges() {
            return badges;
        }

        public List<String> getMedals() {
            return medals;
        }

        public GradeInfo getGradeInfo() {
            return gradeInfo;
        }
        
        public int getBits() {
            return bits;
        }
        
        public String getLastSeen() {
            return lastSeen;
        }
    }

    public static class GradeInfo {
        @SerializedName("Grade")
        private int grade;

        @SerializedName("Exp")
        private int exp;

        @SerializedName("ExpToUpgrade")
        private int expToUpgrade;

        public int getGrade() {
            return grade;
        }

        public int getExp() {
            return exp;
        }

        public int getExpToUpgrade() {
            return expToUpgrade;
        }
    }
}
