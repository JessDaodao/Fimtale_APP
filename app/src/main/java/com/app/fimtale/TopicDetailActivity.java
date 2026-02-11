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
import android.util.TypedValue;
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
import android.animation.ObjectAnimator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.fimtale.adapter.CommentAdapter;
import com.app.fimtale.model.AuthorInfo;
import com.app.fimtale.model.Comment;
import com.app.fimtale.model.ChapterMenuItem;
import com.app.fimtale.model.TopicDetailResponse;
import com.app.fimtale.model.TopicInfo;
import com.app.fimtale.model.TopicTags;
import com.app.fimtale.utils.UserPreferences;
import com.bumptech.glide.Glide;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;
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
    private MaterialCardView toolbarContainer;

    private MaterialCardView imageContainer;
    private ImageView coverImageView;
    private View coverScrim, authorDivider;

    private TextView contentTextView, authorNameTextView;
    private TextView wordCountTextView, viewCountTextView, commentCountTextView;
    private ShapeableImageView authorAvatarImageView;
    private LinearLayout authorLayout;
    private ChipGroup tagChipGroup;
    private ProgressBar progressBar;
    private NestedScrollView scrollView;
    private Button startReadingButton;
    private RecyclerView rvComments;
    private CommentAdapter commentAdapter;

    private Markwon markwon;
    private int currentTopicId;
    
    private boolean isToolbarElevated = false;
    private ObjectAnimator elevationAnimator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_detail);

        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(android.R.attr.colorBackground, typedValue, true);
        getWindow().setStatusBarColor(typedValue.data);

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
        toolbarContainer = findViewById(R.id.toolbarContainer);

        imageContainer = findViewById(R.id.imageContainer);
        coverImageView = findViewById(R.id.detailCoverImageView);

        contentTextView = findViewById(R.id.detailContentTextView);

        authorLayout = findViewById(R.id.authorLayout);
        authorDivider = findViewById(R.id.authorDivider);
        authorAvatarImageView = findViewById(R.id.authorAvatarImageView);
        authorNameTextView = findViewById(R.id.authorNameTextView);
        
        wordCountTextView = findViewById(R.id.wordCountTextView);
        viewCountTextView = findViewById(R.id.viewCountTextView);
        commentCountTextView = findViewById(R.id.commentCountTextView);

        tagChipGroup = findViewById(R.id.tagChipGroup);

        progressBar = findViewById(R.id.detailProgressBar);
        scrollView = findViewById(R.id.scrollView);
        startReadingButton = findViewById(R.id.startReadingButton);
        
        rvComments = findViewById(R.id.rvComments);
        rvComments.setLayoutManager(new LinearLayoutManager(this));
        rvComments.setNestedScrollingEnabled(false);

        float targetElevation = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
        scrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            boolean shouldElevate = scrollY > 0;
            
            if (shouldElevate != isToolbarElevated) {
                isToolbarElevated = shouldElevate;
                
                if (elevationAnimator != null && elevationAnimator.isRunning()) {
                    elevationAnimator.cancel();
                }
                
                float start = toolbarContainer.getCardElevation();
                float end = shouldElevate ? targetElevation : 0;
                
                elevationAnimator = ObjectAnimator.ofFloat(toolbarContainer, "cardElevation", start, end);
                elevationAnimator.setDuration(200);
                elevationAnimator.start();
            }
        });
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

        toolbar.setTitle(topic.getTitle());

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
            if (finalCoverUrl != null) {
                Glide.with(this).load(finalCoverUrl).into(coverImageView);
            } else {
                coverImageView.setImageResource(R.drawable.placeholder_image);
            }
        } else {
            imageContainer.setVisibility(View.GONE);
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

            tagChipGroup.removeAllViews();
            tagChipGroup.setVisibility(View.VISIBLE);
            
            TopicTags tags = topic.getTags();
            if (tags != null) {
                if (!TextUtils.isEmpty(tags.getStatus())) {
                    addTagChip(tags.getStatus(), true);
                }
                
                if (tags.getOtherTags() != null) {
                    for (String tag : tags.getOtherTags()) {
                        addTagChip(tag, false);
                    }
                }
            }
        } else {
            authorLayout.setVisibility(View.GONE);
            authorDivider.setVisibility(View.GONE);
            findViewById(R.id.statsLayout).setVisibility(View.GONE);
            tagChipGroup.setVisibility(View.GONE);
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


    private void addTagChip(String text, boolean isStatus) {
        Chip chip = new Chip(this);
        chip.setText(text);
        chip.setCheckable(false);
        chip.setClickable(false);
        chip.setChipStrokeWidth(0);
        chip.setEnsureMinTouchTargetSize(false);
        
        chip.setChipStartPadding(12f);
        chip.setChipEndPadding(12f);
        chip.setChipMinHeight(24f);
        
        if (isStatus) {
            TypedValue typedValue = new TypedValue();
            getTheme().resolveAttribute(com.google.android.material.R.attr.colorPrimaryContainer, typedValue, true);
            chip.setChipBackgroundColor(ColorStateList.valueOf(typedValue.data));
            
            getTheme().resolveAttribute(com.google.android.material.R.attr.colorOnPrimaryContainer, typedValue, true);
            chip.setTextColor(typedValue.data);
        } else {
            TypedValue typedValue = new TypedValue();
            getTheme().resolveAttribute(com.google.android.material.R.attr.colorSurfaceVariant, typedValue, true);
            chip.setChipBackgroundColor(ColorStateList.valueOf(typedValue.data));
            
            getTheme().resolveAttribute(com.google.android.material.R.attr.colorOnSurfaceVariant, typedValue, true);
            chip.setTextColor(typedValue.data);
        }
        
        tagChipGroup.addView(chip);
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
            startReadingButton.setAlpha(0f);

            progressBar.animate()
                .alpha(0f)
                .setDuration(300)
                .withEndAction(() -> {
                    progressBar.setVisibility(View.GONE);
                    progressBar.setAlpha(1f);

                    scrollView.setAlpha(0f);
                    scrollView.setVisibility(View.VISIBLE);
                    appBarLayout.setAlpha(0f);
                    appBarLayout.setVisibility(View.VISIBLE);

                    scrollView.animate().alpha(1f).setDuration(500).start();
                    appBarLayout.animate().alpha(1f).setDuration(500).start();
                    startReadingButton.animate().alpha(1f).setDuration(500).start();
                })
                .start();

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
            topicInfo.setIntro("前言前言前言前言前言前言前言前言前言前言前言前言前言前言前言前言前言前言前言前言前言前言前言前言前言前言前言前言前言前言前言前言前言前言");
            
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
            
            List<Comment> comments = new ArrayList<>();
            String[] userNames = {"我是用户", "我是用户", "我是用户", "我是用户", "我是用户", "我是用户"};
            String[] commentContents = {"我草太好看了", "作者恩情还不完✋😭✋", "请继续更新，这是我了解马圈的唯一途径", "非常好文章，使我API旋转", "我是评论", "有没有读者群啊？"};
            String[] chapters = {"第一章：名称", "第二章：消息", "第三章：名称", "第四章：名称"};
            
            for (int i = 0; i < 10; i++) {
                comments.add(new Comment(
                    "https://dreamlandcon.top/img/sample.jpg",
                    userNames[random.nextInt(userNames.length)],
                    commentContents[random.nextInt(commentContents.length)],
                    chapters[random.nextInt(chapters.length)],
                    (random.nextInt(23) + 1) + "小时前"
                ));
            }
            
            commentAdapter = new CommentAdapter(comments);
            rvComments.setAdapter(commentAdapter);
            
            scrollView.scrollTo(0, 0);
        }, 1000);
    }
}
