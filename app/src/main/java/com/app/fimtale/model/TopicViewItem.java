package com.app.fimtale.model;

import com.app.fimtale.model.RecommendedTopic;
import com.app.fimtale.model.Topic;
import com.app.fimtale.model.Work;
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

    public TopicViewItem(Work work) {
        this.id = work.getId();
        this.title = work.getTitle();
        this.authorName = work.getUsername();
        this.background = work.getCover();
        this.intro = work.getIntro();
        
        this.tags = new Tags();
        java.util.List<String> otherTags = new java.util.ArrayList<>();
        
        this.tags.setType(getTypeString(work.getType()));
        this.tags.setSource(getOriginString(work.getOrigin()));
        this.tags.setLength(getLengthString(work.getLength()));
        this.tags.setRating(getRatingString(work.getRating()));
        this.tags.setStatus(getPublishString(work.getPublish()));

        if (work.getTags() != null) {
            for (Work.TagCategory category : work.getTags()) {
                if (category.getTags() != null) {
                    for (Work.WorkTagInfo tagInfo : category.getTags()) {
                        String tagName = tagInfo.getName();
                        if ("题材".equals(category.getName()) && this.tags.getType() == null) {
                            this.tags.setType(tagName);
                        } else if ("分级".equals(category.getName()) && this.tags.getRating() == null) {
                            this.tags.setRating(tagName);
                        } else if ("篇幅".equals(category.getName()) && this.tags.getLength() == null) {
                            this.tags.setLength(tagName);
                        } else if ("进度".equals(category.getName()) && this.tags.getStatus() == null) {
                            this.tags.setStatus(tagName);
                        } else if ("来源".equals(category.getName()) && this.tags.getSource() == null) {
                            this.tags.setSource(tagName);
                        } else {
                            otherTags.add(tagName);
                        }
                    }
                }
            }
        }
        this.tags.setOtherTags(otherTags);

        this.wordCount = String.valueOf(work.getCountWord());
        this.viewCount = String.valueOf(work.getCountView());
        this.commentCount = String.valueOf(work.getCountComment());
        this.favoriteCount = String.valueOf(work.getCountFav());
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

    private String getTypeString(int type) {
        switch (type) {
            case 1: return "文章";
            case 2: return "图集";
            case 3: return "帖子";
            case 4: return "公告";
            default: return "未知";
        }
    }

    private String getOriginString(int origin) {
        switch (origin) {
            case 1: return "原创";
            case 2: return "翻译";
            case 3: return "转载";
            default: return "未知";
        }
    }

    private String getLengthString(int length) {
        switch (length) {
            case 1: return "长篇";
            case 2: return "中篇";
            case 3: return "短篇";
            default: return "未知";
        }
    }

    private String getRatingString(int rating) {
        switch (rating) {
            case 1: return "Everyone";
            case 2: return "Teen";
            case 3: return "Restricted";
            default: return "Everyone";
        }
    }

    private String getPublishString(int publish) {
        switch (publish) {
            case 1: return "连载中";
            case 2: return "已完结";
            case 3: return "已弃坑";
            default: return "未知";
        }
    }
}
