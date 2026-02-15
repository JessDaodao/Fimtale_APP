package com.app.fimtale.model;

import com.app.fimtale.model.RecommendedTopic;
import com.app.fimtale.model.Topic;
import java.util.Random;

public class TopicViewItem {
    private int id;
    private String title;
    private String authorName;
    private String background;
    private String intro; // 新增
    
    // Tags
    private Tags tags;

    // 统计数据
    private String wordCount;
    private String viewCount;
    private String commentCount;
    private String favoriteCount;

    public TopicViewItem(RecommendedTopic topic) {
        this.id = topic.getId();
        this.title = topic.getTitle();
        this.authorName = topic.getAuthorName();
        this.background = topic.getBackground();
        this.intro = topic.getRecommendWord();
        generateRandomStats();
    }

    public TopicViewItem(Topic topic) {
        this.id = topic.getId();
        this.title = topic.getTitle();
        this.authorName = topic.getAuthorName();
        this.background = topic.getBackground();
        this.intro = topic.getIntro();
        this.tags = topic.getTags();
        
        this.wordCount = String.valueOf(topic.getWordCount());
        this.viewCount = String.valueOf(topic.getViews());
        this.commentCount = String.valueOf(topic.getComments());
        this.favoriteCount = String.valueOf(topic.getFollowers());
    }
    
    private void generateRandomStats() {
        Random random = new Random();
        
        // 字数: 100 - 8000
        int words = 100 + random.nextInt(7900);
        this.wordCount = String.valueOf(words);
        
        // 阅读量: 100 - 8000
        int views = 100 + random.nextInt(7900);
        this.viewCount = String.valueOf(views);
        
        // 评论量: 0 - 8000
        int comments = random.nextInt(8000);
        this.commentCount = String.valueOf(comments);
        
        // 收藏量: 0 - 8000
        int favorites = random.nextInt(8000);
        this.favoriteCount = String.valueOf(favorites);
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthorName() { return authorName; }
    public String getBackground() { return background; }
    public String getIntro() { return intro; }
    public Tags getTags() { return tags; }
    
    public String getWordCount() { return wordCount; }
    public String getViewCount() { return viewCount; }
    public String getCommentCount() { return commentCount; }
    public String getFavoriteCount() { return favoriteCount; }
}
