package com.app.fimtale;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.drawerlayout.widget.DrawerLayout;

import com.app.fimtale.model.AuthorInfo;
import com.app.fimtale.model.ChapterMenuItem;
import com.app.fimtale.model.TopicDetailResponse;
import com.app.fimtale.model.TopicInfo;
import com.app.fimtale.model.TopicTags;
import com.app.fimtale.utils.UserPreferences;
import com.bumptech.glide.Glide;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.noties.markwon.Markwon;
import io.noties.markwon.html.HtmlPlugin;
import io.noties.markwon.image.ImagesPlugin;

public class TopicDetailActivity extends AppCompatActivity {

    public static final String EXTRA_TOPIC_ID = "topic_id";

    private AppBarLayout appBarLayout;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private MaterialToolbar toolbar;

    private FrameLayout imageContainer;
    private ImageView coverImageView;
    private View coverScrim, authorDivider;

    private TextView contentTextView, authorNameTextView;
    private TextView wordCountTextView, viewCountTextView, commentCountTextView;
    private ShapeableImageView authorAvatarImageView;
    private LinearLayout authorLayout;
    private ProgressBar progressBar;
    private NestedScrollView scrollView;
    private Button startReadingButton;

    private Markwon markwon;
    private int currentTopicId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_detail);

        setupViews();

        markwon = Markwon.builder(this)
                .usePlugin(HtmlPlugin.create())
                .usePlugin(ImagesPlugin.create())
                .build();

        currentTopicId = getIntent().getIntExtra(EXTRA_TOPIC_ID, -1);
        if (currentTopicId != -1) {
            loadChapter(currentTopicId);
        } else {
            finish();
        }

        setupClickListeners();
    }

    private void setupViews() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        appBarLayout = findViewById(R.id.app_bar);
        collapsingToolbarLayout = findViewById(R.id.toolbar_layout);

        appBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            int totalScrollRange = appBarLayout.getTotalScrollRange();
            // 当折叠程度超过 90% 时认为已折叠
            boolean isCollapsed = Math.abs(verticalOffset) >= totalScrollRange * 0.9;

            int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
            boolean isNightMode = nightModeFlags == Configuration.UI_MODE_NIGHT_YES;

            if (!isNightMode) {
                if (isCollapsed) {
                    toolbar.setNavigationIconTint(Color.BLACK);
                    setMenuIconTint(Color.BLACK);
                } else {
                    toolbar.setNavigationIconTint(Color.WHITE);
                    setMenuIconTint(Color.WHITE);
                }
            } else {
                toolbar.setNavigationIconTint(Color.WHITE);
                setMenuIconTint(Color.WHITE);
            }
        });

        imageContainer = findViewById(R.id.imageContainer);
        coverImageView = findViewById(R.id.detailCoverImageView);
        coverScrim = findViewById(R.id.coverScrim);

        contentTextView = findViewById(R.id.detailContentTextView);

        authorLayout = findViewById(R.id.authorLayout);
        authorDivider = findViewById(R.id.authorDivider);
        authorAvatarImageView = findViewById(R.id.authorAvatarImageView);
        authorNameTextView = findViewById(R.id.authorNameTextView);
        
        wordCountTextView = findViewById(R.id.wordCountTextView);
        viewCountTextView = findViewById(R.id.viewCountTextView);
        commentCountTextView = findViewById(R.id.commentCountTextView);

        progressBar = findViewById(R.id.detailProgressBar);
        scrollView = findViewById(R.id.scrollView);
        startReadingButton = findViewById(R.id.startReadingButton);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_topic_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private void setMenuIconTint(int color) {
        Menu menu = toolbar.getMenu();
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            if (item.getIcon() != null) {
                Drawable drawable = item.getIcon();
                drawable.setTint(color);
                item.setIcon(drawable);
            }
        }
    }

    private void loadChapter(int topicId) {
        progressBar.setVisibility(View.VISIBLE);
        scrollView.setVisibility(View.INVISIBLE);
        appBarLayout.setVisibility(View.INVISIBLE);

        generateRandomData(topicId);
    }

    private void updateUI(TopicDetailResponse data) {
        TopicInfo topic = data.getTopicInfo();
        AuthorInfo author = data.getAuthorInfo();
        TopicInfo parentInfo = data.getParentInfo();

        collapsingToolbarLayout.setTitle(topic.getTitle());

        Pair<String, String> processedContent = preprocessHtmlContent(topic.getContent());
        String cleanedHtml = processedContent.first;
        String extractedImageUrl = processedContent.second;

        boolean isIntroPage = (parentInfo != null && parentInfo.getId() == topic.getId());
        String finalCoverUrl = extractedImageUrl;
        if (finalCoverUrl == null && !TextUtils.isEmpty(topic.getBackground())) {
            finalCoverUrl = topic.getBackground();
        }

        if (isIntroPage || finalCoverUrl != null) {
            imageContainer.setVisibility(View.VISIBLE);
            appBarLayout.setExpanded(true, true);
            if (finalCoverUrl != null) {
                Glide.with(this).load(finalCoverUrl).into(coverImageView);
            } else {
                coverImageView.setImageResource(R.drawable.placeholder_image);
            }
        } else {
            imageContainer.setVisibility(View.GONE);
            appBarLayout.setExpanded(false, false);
        }

        if (isIntroPage && author != null) {
            authorLayout.setVisibility(View.VISIBLE);
            authorDivider.setVisibility(View.VISIBLE);
            findViewById(R.id.statsLayout).setVisibility(View.VISIBLE);

            authorNameTextView.setText(author.getUserName());
            Glide.with(this).load(author.getBackground()).into(authorAvatarImageView);

            wordCountTextView.setText(String.valueOf(topic.getWordCount()));
            viewCountTextView.setText(String.valueOf(topic.getViewCount()));
            commentCountTextView.setText(String.valueOf(topic.getCommentCount()));
        } else {
            authorLayout.setVisibility(View.GONE);
            authorDivider.setVisibility(View.GONE);
            findViewById(R.id.statsLayout).setVisibility(View.GONE);
        }

        String intro = topic.getIntro();
        if (TextUtils.isEmpty(intro)) {
            if (cleanedHtml.length() > 100) {
                intro = cleanedHtml.substring(0, 100) + "...";
            } else {
                intro = cleanedHtml;
            }
        }
        markwon.setMarkdown(contentTextView, intro);
        
        startReadingButton.setVisibility(View.VISIBLE);
    }


    private Pair<String, String> preprocessHtmlContent(String html) {
        if (html == null) return new Pair<>("", null);
        String imageUrl = null;
        String cleanedHtml = html;
        Pattern imgPattern = Pattern.compile("<img[^>]+src\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>");
        Matcher imgMatcher = imgPattern.matcher(cleanedHtml);
        if (imgMatcher.find()) {
            imageUrl = imgMatcher.group(1);
            cleanedHtml = imgMatcher.replaceFirst("");
        }
        cleanedHtml = cleanedHtml.replaceAll("(?i)<h[1-6][^>]*>.*?</h[1-6]>", "");
        return new Pair<>(cleanedHtml.trim(), imageUrl);
    }

    private void setupClickListeners() {
        startReadingButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, ReaderActivity.class);
            intent.putExtra(ReaderActivity.EXTRA_TOPIC_ID, currentTopicId);
            startActivity(intent);
        });
    }

    private void generateRandomData(int topicId) {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            progressBar.setVisibility(View.GONE);
            scrollView.setVisibility(View.VISIBLE);
            appBarLayout.setVisibility(View.VISIBLE);

            TopicDetailResponse data = new TopicDetailResponse();
            
            TopicInfo topicInfo = new TopicInfo();
            topicInfo.setId(topicId);
            
            Random random = new Random();
            String[] titles = {"第一章：我是傻逼", "第二章：我", "第三章：666", "第四章：我不知道", "第五章：？"};
            String[] contents = {
                    "正文1正文1正文1正文1正文1正文1正文1正文1正文1正文1正文1正文1正文1正文1正文1正文1正文1正文1正文1正文1正文1正文1正文1正文1正文1正文1正文1正文1正文1正文1正文1正文1正文1正文1正文1正文1正文1正文1正文1正文1",
                    "正文2正文2正文2正文2正文2正文2正文2正文2正文2正文2正文2正文2正文2正文2正文2正文2正文2正文2正文2正文2正文2正文2正文2正文2正文2正文2正文2正文2正文2正文2正文2正文2正文2",
                    "正文正文正文正文正文正文正文正文正文正文正文正文正文正文正文",
                    "正文正文正文正文正文正文正文正文正文正文正文正文正文",
                    "正文正文正文正文正文正文正文正文正文正文正文正文",
                    "正文正文正文正文正文正文正文正文正文正文正文正文正文正文正文正文正文正文正文正文正文正文正文正文正文正文正文正文正文正文正文正文正文正文正文正文正文正文正文正文正文正文正文正文正文",
                    "正文正文正文正文正文正文正文正文正文正文正文正文正文正文正文正文正文正文正文",
                    "正文正文正文正文正文正文正文正文正文正文正文",
                    "正文正文正文正文正文正文正文正文正文正文正文正文正文正文正文正文正文正文正文正文正文正文正文正文",
                    "正文正文正文正文正文正文正文正文正文正文正文正文正文正文正文"
            };
            
            topicInfo.setTitle(titles[random.nextInt(titles.length)]);
            topicInfo.setContent(contents[random.nextInt(contents.length)]);
            topicInfo.setBackground("https://dreamlandcon.top/img/sample.jpg");
            topicInfo.setWordCount(1000 + random.nextInt(5000));
            topicInfo.setViewCount(random.nextInt(10000));
            topicInfo.setCommentCount(random.nextInt(500));
            topicInfo.setIntro("这是一个测试前言。这里简要介绍了小说的背景和主要人物。点击下方按钮开始阅读正文。");
            
            TopicTags tags = new TopicTags();
            tags.setType("类型" + (random.nextInt(3) + 1));
            tags.setSource("来源" + (random.nextInt(2) + 1));
            tags.setRating("评级" + (random.nextInt(3) + 1));
            tags.setLength("长度" + (random.nextInt(3) + 1));
            tags.setStatus("连载中");
            List<String> otherTags = new ArrayList<>();
            otherTags.add("标签" + (random.nextInt(5) + 1));
            otherTags.add("标签" + (random.nextInt(5) + 1));
            tags.setOtherTags(otherTags);
            topicInfo.setTags(tags);
            
            data.setTopicInfo(topicInfo);
            
            AuthorInfo authorInfo = new AuthorInfo();
            authorInfo.setId(random.nextInt(1000));
            authorInfo.setUserName("作者" + (random.nextInt(10) + 1));
            authorInfo.setBackground("https://dreamlandcon.top/img/sample.jpg");
            data.setAuthorInfo(authorInfo);
            
            data.setParentInfo(topicInfo);
            
            currentTopicId = topicId;
            updateUI(data);
            scrollView.scrollTo(0, 0);
        }, 1000);
    }
}
