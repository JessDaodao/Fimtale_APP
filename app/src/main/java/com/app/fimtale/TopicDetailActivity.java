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

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private AppBarLayout appBarLayout;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private MaterialToolbar toolbar;

    private FrameLayout imageContainer;
    private ImageView coverImageView;
    private View coverScrim, authorDivider;

    private TextView contentTextView, authorNameTextView;
    private ShapeableImageView authorAvatarImageView;
    private LinearLayout authorLayout, navigationLayout;
    private ChipGroup tagChipGroup;
    private ProgressBar progressBar;
    private NestedScrollView scrollView;
    private Button prevChapterButton, nextChapterButton;

    private Markwon markwon;
    private int currentTopicId;
    private List<ChapterMenuItem> chapterMenu;
    private int currentChapterIndex = -1;

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
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
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
        tagChipGroup = findViewById(R.id.tagChipGroup);

        progressBar = findViewById(R.id.detailProgressBar);
        scrollView = findViewById(R.id.scrollView);
        navigationLayout = findViewById(R.id.navigationLayout);
        prevChapterButton = findViewById(R.id.prevChapterButton);
        nextChapterButton = findViewById(R.id.nextChapterButton);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_topic_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_toc) {
            if (chapterMenu != null && !chapterMenu.isEmpty()) {
                drawerLayout.openDrawer(GravityCompat.END);
            } else {
                Toast.makeText(this, "暂无目录", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
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

        drawerLayout.closeDrawer(GravityCompat.END);

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
            tagChipGroup.setVisibility(View.VISIBLE);

            authorNameTextView.setText(author.getUserName());
            Glide.with(this).load(author.getBackground()).into(authorAvatarImageView);

            setupTags(topic.getTags());
        } else {
            authorLayout.setVisibility(View.GONE);
            authorDivider.setVisibility(View.GONE);
            tagChipGroup.setVisibility(View.GONE);
        }

        markwon.setMarkdown(contentTextView, cleanedHtml);
        updateNavigationButtons(topic);
    }

    private void setupTags(TopicTags tags) {
        tagChipGroup.removeAllViews();
        if (tags == null) return;

        addChip(tags.getType(), Color.parseColor("#2196F3"));
        addChip(tags.getSource(), Color.parseColor("#2196F3"));

        int ratingColor = Color.parseColor("#4CAF50");
        if ("Teen".equals(tags.getRating())) ratingColor = Color.parseColor("#FFC107");
        else if ("Mature".equals(tags.getRating())) ratingColor = Color.parseColor("#F44336");
        addChip(tags.getRating(), ratingColor);

        addChip(tags.getLength(), Color.parseColor("#2196F3"));

        int statusColor = Color.parseColor("#673AB7");
        if (!"已完结".equals(tags.getStatus())) statusColor = Color.parseColor("#2196F3");
        addChip(tags.getStatus(), statusColor);

        if (tags.getOtherTags() != null) {
            for (String tag : tags.getOtherTags()) {
                if ("原设崩坏".equals(tag)) {
                    addChip(tag, Color.parseColor("#F44336"));
                } else {
                    addChip(tag, Color.parseColor("#4CAF50"));
                }
            }
        }
    }

    private void addChip(String text, int color) {
        if (TextUtils.isEmpty(text)) return;
        Chip chip = new Chip(this);
        chip.setText(text);
        chip.setTextColor(Color.WHITE);
        chip.setTextAppearance(com.google.android.material.R.style.TextAppearance_MaterialComponents_Body2);
        chip.setChipMinHeight(32.0f);
        chip.setChipBackgroundColor(ColorStateList.valueOf(color));
        chip.setChipStrokeWidth(0f);
        chip.setClickable(false);
        chip.setCheckable(false);
        chip.setEnsureMinTouchTargetSize(false);
        tagChipGroup.addView(chip);
    }

    private void updateSideMenu() {
        if (chapterMenu == null || chapterMenu.isEmpty()) return;
        Menu menu = navigationView.getMenu();
        menu.clear();
        for (int i = 0; i < chapterMenu.size(); i++) {
            ChapterMenuItem item = chapterMenu.get(i);
            MenuItem menuItem = menu.add(0, i, i, item.getTitle());
            menuItem.setIcon(R.drawable.ic_menu_book);
            if (item.getId() == currentTopicId) {
                menuItem.setChecked(true);
                menuItem.setEnabled(false);
            }
        }
        navigationView.setNavigationItemSelectedListener(item -> {
            int index = item.getItemId();
            if (index >= 0 && index < chapterMenu.size()) {
                loadChapter(chapterMenu.get(index).getId());
            }
            return true;
        });
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

    private void updateNavigationButtons(TopicInfo topic) {
        Object branchesObject = topic.getBranches();
        if (branchesObject instanceof Map) {
            Map<String, Double> branches = (LinkedTreeMap<String, Double>) branchesObject;
            if (!branches.isEmpty()) {
                String buttonText = branches.keySet().iterator().next();
                int nextId = branches.get(buttonText).intValue();
                navigationLayout.setVisibility(View.VISIBLE);
                prevChapterButton.setVisibility(View.GONE);
                nextChapterButton.setVisibility(View.VISIBLE);
                nextChapterButton.setText(buttonText);
                nextChapterButton.setTag(nextId);
                return;
            }
        }

        if (chapterMenu == null || chapterMenu.isEmpty()) {
            navigationLayout.setVisibility(View.GONE);
            return;
        }

        navigationLayout.setVisibility(View.VISIBLE);
        prevChapterButton.setVisibility(View.VISIBLE);
        nextChapterButton.setVisibility(View.VISIBLE);

        currentChapterIndex = -1;
        for (int i = 0; i < chapterMenu.size(); i++) {
            if (chapterMenu.get(i).getId() == currentTopicId) {
                currentChapterIndex = i;
                break;
            }
        }

        prevChapterButton.setEnabled(currentChapterIndex > 0);
        if (prevChapterButton.isEnabled()) {
            prevChapterButton.setTag(chapterMenu.get(currentChapterIndex - 1).getId());
        }

        if (currentChapterIndex != -1 && currentChapterIndex < chapterMenu.size() - 1) {
            nextChapterButton.setText("下一章");
            nextChapterButton.setEnabled(true);
            nextChapterButton.setTag(chapterMenu.get(currentChapterIndex + 1).getId());
        } else {
            nextChapterButton.setText("已完结");
            nextChapterButton.setEnabled(false);
        }
    }

    private void setupClickListeners() {
        View.OnClickListener listener = v -> {
            if (v.getTag() instanceof Integer) {
                loadChapter((int) v.getTag());
            }
        };
        prevChapterButton.setOnClickListener(listener);
        nextChapterButton.setOnClickListener(listener);
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
            
            chapterMenu = new ArrayList<>();
            for (int i = 1; i <= 10; i++) {
                ChapterMenuItem item = new ChapterMenuItem();
                item.setId(i);
                item.setTitle("第" + i + "章");
                chapterMenu.add(item);
            }
            data.setMenu(chapterMenu);
            
            currentTopicId = topicId;
            updateUI(data);
            updateSideMenu();
            scrollView.scrollTo(0, 0);
        }, 1000);
    }
}
