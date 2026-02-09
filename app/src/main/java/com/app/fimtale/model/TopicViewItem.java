package com.app.fimtale.model;

public class TopicViewItem {
    private int id;
    private String title;
    private String authorName;
    private String background;
    private String intro; // 新增

    public TopicViewItem(RecommendedTopic topic) {
        this.id = topic.getId();
        this.title = topic.getTitle();
        this.authorName = topic.getAuthorName();
        this.background = topic.getBackground();
        this.intro = topic.getRecommendWord();
    }

    public TopicViewItem(Topic topic) {
        this.id = topic.getId();
        this.title = topic.getTitle();
        this.authorName = topic.getAuthorName();
        this.background = topic.getBackground();
        this.intro = topic.getIntro();
    }
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthorName() { return authorName; }
    public String getBackground() { return background; }
    public String getIntro() { return intro; }
}