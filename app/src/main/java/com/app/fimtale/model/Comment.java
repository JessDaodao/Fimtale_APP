package com.app.fimtale.model;

public class Comment {
    private String avatarUrl;
    private String userName;
    private String content;
    private String chapterTitle;
    private String time;

    public Comment(String avatarUrl, String userName, String content, String chapterTitle, String time) {
        this.avatarUrl = avatarUrl;
        this.userName = userName;
        this.content = content;
        this.chapterTitle = chapterTitle;
        this.time = time;
    }

    public String getAvatarUrl() { return avatarUrl; }
    public String getUserName() { return userName; }
    public String getContent() { return content; }
    public String getChapterTitle() { return chapterTitle; }
    public String getTime() { return time; }
}
