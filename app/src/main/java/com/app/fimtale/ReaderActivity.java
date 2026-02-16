package com.app.fimtale;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.os.Bundle;
import android.text.Html;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.core.graphics.Insets;
import android.graphics.Color;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionManager;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import android.widget.ImageView;

import com.app.fimtale.adapter.CommentAdapter;
import com.app.fimtale.adapter.TopicAdapter;
import com.app.fimtale.model.ChapterMenuItem;
import com.app.fimtale.model.Comment;
import com.app.fimtale.model.Topic;
import com.app.fimtale.model.TopicDetailResponse;
import com.app.fimtale.model.TopicInfo;
import com.app.fimtale.model.TopicListResponse;
import com.app.fimtale.model.TopicViewItem;
import com.app.fimtale.network.RetrofitClient;
import com.app.fimtale.utils.UserPreferences;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.slider.Slider;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReaderActivity extends AppCompatActivity {

    public static final String EXTRA_TOPIC_ID = "topic_id";

    private ViewPager2 viewPager;
    private RecyclerView recyclerView;
    private View menuOverlay;
    private View dimLayer;
    private MaterialToolbar topToolbar;
    private LinearLayout bottomSheetContainer;
    private View bottomMenu;
    private View btnChapterList;
    private View btnSettings;
    private View guideOverlay;
    
    private View readerHeader;
    private View readerFooter;
    private TextView tvChapterTitle;
    private TextView tvChapterProgress;
    private TextView tvBatteryLevel;

    private BroadcastReceiver batteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            int batteryPct = (int) (level * 100 / (float) scale);
            if (tvBatteryLevel != null) {
                tvBatteryLevel.setText("电量：" + batteryPct + "%");
            }
        }
    };

    private LinearLayout settingsPanel;
    private LinearLayout chapterListPanel;
    private RecyclerView rvChapterList;
    private Slider sliderFontSize;
    private TabLayout tabPageMode;

    private boolean isMenuVisible = false;
    private List<ReaderPage> pages = new ArrayList<>();
    private List<ReaderPage> verticalPages = new ArrayList<>();
    private List<Integer> chapterStartPageIndices = new ArrayList<>();
    private List<Integer> chapterVerticalIndices = new ArrayList<>();
    private List<Integer> pageStartOffsets = new ArrayList<>();
    private List<Integer> paragraphStartOffsets = new ArrayList<>();
    private ReaderAdapter adapter;
    private ReaderAdapter recyclerAdapter;
    private int currentTopicId;
    private int lastWidth = 0;
    private int lastHeight = 0;
    
    private float currentFontSize = 20f;
    private SharedPreferences prefs;
    private Insets lastSystemBars = null;

    private String fullChapterContent = "加载中...";
    private String chapterTitle = "";
    private List<ChapterMenuItem> chapterList = new ArrayList<>();
    private List<ChapterMenuItem> filteredChapterList = new ArrayList<>();
    private ChapterListAdapter chapterListAdapter;
    
    private boolean isLoadingChapter = false;
    private boolean canTriggerChapterChange = false;

    private List<ContentSegment> parsedSegments = new ArrayList<>();

    private static class ContentSegment {
        int type;
        String content;
        ContentSegment(int type, String content) {
            this.type = type;
            this.content = content;
        }
    }

    private static class ReaderPage {
        static final int TYPE_TEXT = 0;
        static final int TYPE_COMMENT = 1;
        static final int TYPE_LOADING = 2;
        static final int TYPE_NEXT_CHAPTER_TRIGGER = 3;
        static final int TYPE_PREV_CHAPTER_TRIGGER = 4;
        static final int TYPE_IMAGE = 5;
        
        int type;
        String content;
        int chapterId;
        
        ReaderPage(int type, String content, int chapterId) {
            this.type = type;
            this.content = content;
            this.chapterId = chapterId;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);

        currentTopicId = getIntent().getIntExtra(EXTRA_TOPIC_ID, -1);
        if (currentTopicId == -1) {
            finish();
            return;
        }

        viewPager = findViewById(R.id.viewPager);
        recyclerView = findViewById(R.id.recyclerView);
        menuOverlay = findViewById(R.id.menuOverlay);
        dimLayer = findViewById(R.id.dimLayer);
        topToolbar = findViewById(R.id.topToolbar);
        bottomSheetContainer = findViewById(R.id.bottomSheetContainer);
        bottomMenu = findViewById(R.id.bottomMenu);
        btnChapterList = findViewById(R.id.btnChapterList);
        btnSettings = findViewById(R.id.btnSettings);
        guideOverlay = findViewById(R.id.guideOverlay);

        settingsPanel = findViewById(R.id.settingsPanel);
        chapterListPanel = findViewById(R.id.chapterListPanel);
        rvChapterList = findViewById(R.id.rvChapterList);
        
        readerHeader = findViewById(R.id.readerHeader);
        readerFooter = findViewById(R.id.readerFooter);
        tvChapterTitle = findViewById(R.id.tvChapterTitle);
        tvChapterProgress = findViewById(R.id.tvChapterProgress);
        tvBatteryLevel = findViewById(R.id.tvBatteryLevel);
        
        sliderFontSize = findViewById(R.id.sliderFontSize);
        tabPageMode = findViewById(R.id.tabPageMode);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            android.view.WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.layoutInDisplayCutoutMode = android.view.WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            getWindow().setAttributes(lp);
        }

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);

        ViewCompat.setOnApplyWindowInsetsListener(menuOverlay, (v, windowInsets) -> {
            Insets systemBars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            if (systemBars.top > 0 || systemBars.bottom > 0) {
                lastSystemBars = systemBars;
            }
            
            Insets insets = (lastSystemBars != null) ? lastSystemBars : systemBars;
            
            ViewGroup.MarginLayoutParams topParams = (ViewGroup.MarginLayoutParams) topToolbar.getLayoutParams();
            topParams.topMargin = insets.top + (int)(4 * getResources().getDisplayMetrics().density);
            topToolbar.setLayoutParams(topParams);
            
            ViewGroup.MarginLayoutParams bottomParams = (ViewGroup.MarginLayoutParams) bottomSheetContainer.getLayoutParams();
            bottomParams.bottomMargin = insets.bottom + (int)(16 * getResources().getDisplayMetrics().density);
            bottomSheetContainer.setLayoutParams(bottomParams);
            
            int headerTopPadding = insets.top + (int)(12 * getResources().getDisplayMetrics().density);
            readerHeader.setPadding(
                readerHeader.getPaddingLeft(),
                headerTopPadding,
                readerHeader.getPaddingRight(),
                readerHeader.getPaddingBottom()
            );

            int footerBottomPadding = insets.bottom + (int)(8 * getResources().getDisplayMetrics().density);
            readerFooter.setPadding(
                readerFooter.getPaddingLeft(),
                readerFooter.getPaddingTop(),
                readerFooter.getPaddingRight(),
                footerBottomPadding
            );
            
            int bodyTopPadding = insets.top + (int)(50 * getResources().getDisplayMetrics().density);
            int bodyBottomPadding = insets.bottom + (int)(30 * getResources().getDisplayMetrics().density);
            viewPager.setPadding(0, bodyTopPadding, 0, bodyBottomPadding);
            recyclerView.setPadding(0, bodyTopPadding, 0, bodyBottomPadding);
            
            return windowInsets;
        });

        topToolbar.setNavigationOnClickListener(v -> finish());
        
        dimLayer.setOnClickListener(v -> hideMenu());
        
        btnChapterList.setOnClickListener(v -> toggleChapterList());

        btnSettings.setOnClickListener(v -> toggleSettingsPanel());

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        currentFontSize = prefs.getFloat("reader_font_size", 20f);
        boolean isVertical = prefs.getBoolean("reader_is_vertical", false);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerAdapter = new ReaderAdapter(verticalPages, true);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    canTriggerChapterChange = true;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (isMenuVisible && dy != 0) {
                    hideMenu();
                }
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null) {
                    int position = layoutManager.findFirstVisibleItemPosition();
                    updateCurrentChapterFromParagraph(position);
                }
            }
        });

        adapter = new ReaderAdapter(pages, false);
        viewPager.setAdapter(adapter);

        updateFontSize(currentFontSize);
        sliderFontSize.setValue(currentFontSize);

        if (isVertical) {
            TabLayout.Tab tab = tabPageMode.getTabAt(1);
            if (tab != null) tab.select();
        } else {
            TabLayout.Tab tab = tabPageMode.getTabAt(0);
            if (tab != null) tab.select();
        }

        sliderFontSize.addOnChangeListener((slider, value, fromUser) -> {
            if (fromUser) {
                currentFontSize = value;
                updateFontSize(currentFontSize);
                saveFontSize();
            }
        });

        tabPageMode.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    updatePageMode(false);
                    prefs.edit().putBoolean("reader_is_vertical", false).apply();
                } else {
                    updatePageMode(true);
                    prefs.edit().putBoolean("reader_is_vertical", true).apply();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        viewPager.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                viewPager.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                calculatePages();
                
                if (isVertical) {
                    updatePageMode(true);
                } else {
                    updatePageMode(false);
                }

                topToolbar.setTranslationY(-topToolbar.getBottom());
                bottomSheetContainer.setTranslationY(menuOverlay.getHeight() - bottomSheetContainer.getTop());
                
                fetchChapterContent(currentTopicId);
            }
        });

        hideSystemUI();
        
        setupChapterList();
        
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateCurrentChapterFromPage(position);
                
                if (adapter != null && position < adapter.getItemCount()) {
                    int type = adapter.getItemViewType(position);
                    if (type == ReaderPage.TYPE_NEXT_CHAPTER_TRIGGER) {
                        int nextId = getNextChapterId();
                        if (nextId != -1) {
                            jumpToChapter(nextId);
                        }
                    } else if (type == ReaderPage.TYPE_PREV_CHAPTER_TRIGGER) {
                        int prevId = getPrevChapterId();
                        if (prevId != -1) {
                            jumpToChapter(prevId, true);
                        }
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
                if (state == ViewPager2.SCROLL_STATE_DRAGGING && isMenuVisible) {
                    hideMenu();
                }
            }
        });

        checkAndShowGuide();
        
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryReceiver, ifilter);
        
    }
    
    private void fetchChapterContent(int topicId) {
        fetchChapterContent(topicId, false);
    }
    
    private void fetchChapterContent(int topicId, boolean scrollToEnd) {
        if (isLoadingChapter) return;
        isLoadingChapter = true;
        canTriggerChapterChange = false;

        String apiKey = UserPreferences.getApiKey(this);
        String apiPass = UserPreferences.getApiPass(this);

        RetrofitClient.getInstance().getTopicDetail(topicId, apiKey, apiPass, "json").enqueue(new Callback<TopicDetailResponse>() {
            @Override
            public void onResponse(Call<TopicDetailResponse> call, Response<TopicDetailResponse> response) {
                isLoadingChapter = false;
                if (response.isSuccessful() && response.body() != null && response.body().getStatus() == 1) {
                    TopicDetailResponse data = response.body();
                    TopicInfo topic = data.getTopicInfo();
                    
                    if (topic != null) {
                        chapterTitle = topic.getTitle();
                        topToolbar.setTitle(chapterTitle);
                        tvChapterTitle.setText(chapterTitle);
                        
                        String content = topic.getContent();
                        if (content != null) {
                             fullChapterContent = Html.fromHtml(content, Html.FROM_HTML_MODE_COMPACT).toString();
                             parseContent(content);
                        } else {
                             fullChapterContent = "无内容";
                             parsedSegments.clear();
                             parsedSegments.add(new ContentSegment(ReaderPage.TYPE_TEXT, "无内容"));
                        }
                    }
                    
                    if (data.getMenu() != null) {
                        chapterList = data.getMenu();
                        filteredChapterList.clear();
                        for (ChapterMenuItem item : chapterList) {
                            if (!item.getTitle().contains("前言")) {
                                filteredChapterList.add(item);
                            }
                        }
                        if (chapterListAdapter != null) {
                            chapterListAdapter.updateData(filteredChapterList);
                        }
                    }
                    
                    currentTopicId = topicId;
                    
                    prepareVerticalContent();
                    calculatePages();
                    
                    if (viewPager.getVisibility() == View.VISIBLE) {
                        int initialPage = 0;
                        if (!pages.isEmpty() && pages.get(0).type == ReaderPage.TYPE_PREV_CHAPTER_TRIGGER) {
                            initialPage = 1;
                        }

                        if (scrollToEnd) {
                             initialPage = pages.size() - 1;
                             if (initialPage > 0 && pages.get(initialPage).type == ReaderPage.TYPE_NEXT_CHAPTER_TRIGGER) {
                                 initialPage--;
                             }
                        }

                        viewPager.setCurrentItem(initialPage, false);
                        updateCurrentChapterFromPage(initialPage);
                    } else {
                        if (scrollToEnd) {
                             int pos = verticalPages.size() - 1;
                             if (pos > 0 && verticalPages.get(pos).type == ReaderPage.TYPE_NEXT_CHAPTER_TRIGGER) {
                                 pos--;
                             }
                             recyclerView.scrollToPosition(pos);
                             updateCurrentChapterFromParagraph(pos);
                        } else {
                             int pos = 0;
                             if (!verticalPages.isEmpty() && verticalPages.get(0).type == ReaderPage.TYPE_PREV_CHAPTER_TRIGGER) {
                                 pos = 1;
                             }
                             recyclerView.scrollToPosition(pos);
                             updateCurrentChapterFromParagraph(pos);
                        }
                    }
                    
                } else {
                    Toast.makeText(ReaderActivity.this, "加载失败: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TopicDetailResponse> call, Throwable t) {
                isLoadingChapter = false;
                Toast.makeText(ReaderActivity.this, "网络错误: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(batteryReceiver);
    }

    private void checkAndShowGuide() {
        boolean hasShownGuide = prefs.getBoolean("has_shown_reader_guide", false);
        if (!hasShownGuide) {
            guideOverlay.setAlpha(1f);
            guideOverlay.setVisibility(View.VISIBLE);
            guideOverlay.setOnClickListener(v -> {
                guideOverlay.animate()
                        .alpha(0f)
                        .setDuration(500)
                        .withEndAction(() -> guideOverlay.setVisibility(View.GONE))
                        .start();
                prefs.edit().putBoolean("has_shown_reader_guide", true).apply();
            });
        }
    }
    
    private void setupChapterList() {
        rvChapterList.setLayoutManager(new LinearLayoutManager(this));
        chapterListAdapter = new ChapterListAdapter();
        rvChapterList.setAdapter(chapterListAdapter);
    }

    private void jumpToChapter(int chapterId) {
        jumpToChapter(chapterId, false);
    }
    
    private void jumpToChapter(int chapterId, boolean scrollToEnd) {
        fetchChapterContent(chapterId, scrollToEnd);
        hideMenu();
    }

    private void updateCurrentChapterFromPage(int pageIndex) {
        if (pageIndex < 0 || pageIndex >= pages.size()) return;
        
        ReaderPage page = pages.get(pageIndex);
        if (page.type == ReaderPage.TYPE_PREV_CHAPTER_TRIGGER || page.type == ReaderPage.TYPE_NEXT_CHAPTER_TRIGGER) {
            updateHeader(chapterTitle, "");
            return;
        }

        int startOffset = 0;
        int endOffset = 0;
        
        if (!pages.isEmpty() && pages.get(0).type == ReaderPage.TYPE_PREV_CHAPTER_TRIGGER) {
            startOffset = 1;
        }
        
        if (!pages.isEmpty() && pages.get(pages.size() - 1).type == ReaderPage.TYPE_NEXT_CHAPTER_TRIGGER) {
            endOffset = 1;
        }
        
        int realTotal = pages.size() - startOffset - endOffset;
        int realIndex = pageIndex - startOffset + 1;
        
        if (realIndex < 1) realIndex = 1;
        if (realIndex > realTotal) realIndex = realTotal;
        
        updateHeader(chapterTitle, realIndex + "/" + realTotal);
    }

    private void updateCurrentChapterFromParagraph(int paragraphIndex) {
         if (verticalPages.isEmpty()) return;
         float percent = (float)(paragraphIndex + 1) * 100 / verticalPages.size();
         updateHeader(chapterTitle, String.format("%.1f%%", percent));
    }
    
    private void updateHeader(String title, String progressText) {
        tvChapterTitle.setText(title);
        tvChapterProgress.setText(progressText);
    }

    private void updateFontSize(float size) {
        calculatePages();
        if (recyclerView.getVisibility() == View.VISIBLE) {
            prepareVerticalContent();
            recyclerAdapter.updateData(verticalPages);
        }
    }

    private void saveFontSize() {
        prefs.edit().putFloat("reader_font_size", currentFontSize).apply();
    }

    private void parseContent(String htmlContent) {
        parsedSegments.clear();
        Pattern imgPattern = Pattern.compile("<img[^>]+src\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>");
        Matcher matcher = imgPattern.matcher(htmlContent);
        int lastEnd = 0;
        
        while (matcher.find()) {
            String textPart = htmlContent.substring(lastEnd, matcher.start());
            String plainText = Html.fromHtml(textPart, Html.FROM_HTML_MODE_COMPACT).toString();
            if (!plainText.trim().isEmpty()) {
                parsedSegments.add(new ContentSegment(ReaderPage.TYPE_TEXT, plainText));
            }
            
            String imgSrc = matcher.group(1);
            if (imgSrc != null && !imgSrc.isEmpty()) {
                parsedSegments.add(new ContentSegment(ReaderPage.TYPE_IMAGE, imgSrc));
            }
            
            lastEnd = matcher.end();
        }
        
        String tail = htmlContent.substring(lastEnd);
        String tailPlainText = Html.fromHtml(tail, Html.FROM_HTML_MODE_COMPACT).toString();
        if (!tailPlainText.trim().isEmpty()) {
            parsedSegments.add(new ContentSegment(ReaderPage.TYPE_TEXT, tailPlainText));
        }
        
        if (parsedSegments.isEmpty()) {
            String plain = Html.fromHtml(htmlContent, Html.FROM_HTML_MODE_COMPACT).toString();
             if (!plain.isEmpty()) {
                 parsedSegments.add(new ContentSegment(ReaderPage.TYPE_TEXT, plain));
             }
        }
    }

    private void prepareVerticalContent() {
        verticalPages.clear();
        paragraphStartOffsets.clear();
        chapterVerticalIndices.clear();
        
        if (fullChapterContent.equals("加载中...")) {
            verticalPages.add(new ReaderPage(ReaderPage.TYPE_LOADING, null, currentTopicId));
            if (recyclerAdapter != null) {
                recyclerAdapter.notifyDataSetChanged();
            }
            return;
        }

        if (getPrevChapterId() != -1) {
            verticalPages.add(new ReaderPage(ReaderPage.TYPE_PREV_CHAPTER_TRIGGER, null, -1));
        }

        int currentOffset = 0;
        
        chapterVerticalIndices.add(verticalPages.size());
        
        verticalPages.add(new ReaderPage(ReaderPage.TYPE_TEXT, chapterTitle + "\n\n", currentTopicId));
        paragraphStartOffsets.add(currentOffset);
        currentOffset += chapterTitle.length() + 2;

        for (ContentSegment segment : parsedSegments) {
            if (segment.type == ReaderPage.TYPE_TEXT) {
                String[] paragraphs = segment.content.split("\n");
                for (String paragraph : paragraphs) {
                    if (!paragraph.trim().isEmpty()) {
                        verticalPages.add(new ReaderPage(ReaderPage.TYPE_TEXT, "\u3000\u3000" + paragraph.trim(), currentTopicId));
                        paragraphStartOffsets.add(currentOffset);
                        currentOffset += paragraph.length() + 1;
                    }
                }
            } else if (segment.type == ReaderPage.TYPE_IMAGE) {
                verticalPages.add(new ReaderPage(ReaderPage.TYPE_IMAGE, segment.content, currentTopicId));
                paragraphStartOffsets.add(currentOffset);
                currentOffset += 1;
            }
        }
        
        verticalPages.add(new ReaderPage(ReaderPage.TYPE_COMMENT, null, currentTopicId));
        paragraphStartOffsets.add(currentOffset);
        
        if (getNextChapterId() != -1) {
            verticalPages.add(new ReaderPage(ReaderPage.TYPE_NEXT_CHAPTER_TRIGGER, null, -1));
        }
        
        if (recyclerAdapter != null) {
            recyclerAdapter.notifyDataSetChanged();
        }
    }

    private void updatePageMode(boolean isVertical) {
        if (isVertical) {
            viewPager.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            
            readerHeader.setBackgroundColor(getThemeColor(android.R.attr.colorBackground));
            readerFooter.setBackgroundColor(getThemeColor(android.R.attr.colorBackground));
            
            prepareVerticalContent();
            recyclerAdapter.updateData(verticalPages);
            
            recyclerView.post(() -> {
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null) {
                    int position = layoutManager.findFirstVisibleItemPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        updateCurrentChapterFromParagraph(position);
                    } else {
                        updateCurrentChapterFromParagraph(0);
                    }
                }
            });

        } else {
            recyclerView.setVisibility(View.GONE);
            viewPager.setVisibility(View.VISIBLE);
            
            readerHeader.setBackground(null);
            readerFooter.setBackground(null);
            
            calculatePages();
            adapter.updateData(pages);
            
            viewPager.post(() -> {
                int currentItem = viewPager.getCurrentItem();
                updateCurrentChapterFromPage(currentItem);
            });
        }
    }
    
    private int getThemeColor(int attr) {
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(attr, typedValue, true);
        return typedValue.data;
    }

    private void calculatePages() {
        int width = viewPager.getWidth();
        int height = viewPager.getHeight();

        if (fullChapterContent.equals("加载中...")) {
            pages.clear();
            pages.add(new ReaderPage(ReaderPage.TYPE_LOADING, null, currentTopicId));
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
            return;
        }

        if (width > 0 && height > 0) {
            lastWidth = width;
            lastHeight = height;
        } else if (lastWidth > 0 && lastHeight > 0) {
            width = lastWidth;
            height = lastHeight;
        } else {
            width = getResources().getDisplayMetrics().widthPixels;
            height = getResources().getDisplayMetrics().heightPixels;
            lastWidth = width;
            lastHeight = height;
        }
        
        int padding = (int) (24 * getResources().getDisplayMetrics().density);
        int contentWidth = width - padding * 2;
        int contentHeight = height - viewPager.getPaddingTop() - viewPager.getPaddingBottom() - padding * 2;

        if (contentWidth <= 0 || contentHeight <= 0) return;

        TextPaint paint = new TextPaint();
        paint.setTextSize(currentFontSize * getResources().getDisplayMetrics().scaledDensity);
        paint.setAntiAlias(true);
        paint.setColor(getResources().getColor(android.R.color.primary_text_dark, getTheme()));

        pages.clear();
        chapterStartPageIndices.clear();
        pageStartOffsets.clear();

        if (getPrevChapterId() != -1) {
            pages.add(new ReaderPage(ReaderPage.TYPE_PREV_CHAPTER_TRIGGER, null, -1));
            pageStartOffsets.add(0);
        }
        
        int globalOffset = 0;

        chapterStartPageIndices.add(pages.size());
        
        List<ContentSegment> allSegments = new ArrayList<>();
        allSegments.add(new ContentSegment(ReaderPage.TYPE_TEXT, chapterTitle + "\n\n"));
        allSegments.addAll(parsedSegments);

        for (ContentSegment segment : allSegments) {
            if (segment.type == ReaderPage.TYPE_TEXT) {
                String formattedContent = segment.content.replaceAll("(?m)^(?=.)", "\u3000\u3000");
                
                StaticLayout layout = StaticLayout.Builder.obtain(formattedContent, 0, formattedContent.length(), paint, contentWidth)
                        .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                        .setLineSpacing(10f * getResources().getDisplayMetrics().density, 1.0f)
                        .setIncludePad(false)
                        .build();

                int startLine = 0;
                while (startLine < layout.getLineCount()) {
                    int lineTop = layout.getLineTop(startLine);
                    int endLine = layout.getLineForVertical(lineTop + contentHeight);
                    
                    if (layout.getLineBottom(endLine) > lineTop + contentHeight) {
                        endLine--;
                    }
                    
                    if (endLine < startLine) endLine = startLine;
                    
                    int startOffset = layout.getLineStart(startLine);
                    int endOffset = layout.getLineEnd(endLine);
                    
                    if (endOffset > startOffset) {
                        String pageContent = formattedContent.substring(startOffset, endOffset);
                        boolean isLastPage = (endLine >= layout.getLineCount() - 1);
                        
                        if (!isLastPage || !pageContent.trim().isEmpty()) {
                            pages.add(new ReaderPage(ReaderPage.TYPE_TEXT, pageContent, currentTopicId));
                            pageStartOffsets.add(globalOffset + startOffset);
                        }
                    }
                    
                    startLine = endLine + 1;
                }
                globalOffset += formattedContent.length();
            } else if (segment.type == ReaderPage.TYPE_IMAGE) {
                pages.add(new ReaderPage(ReaderPage.TYPE_IMAGE, segment.content, currentTopicId));
                pageStartOffsets.add(globalOffset);
                globalOffset += 1;
            }
        }
        
        pages.add(new ReaderPage(ReaderPage.TYPE_COMMENT, null, currentTopicId));
        pageStartOffsets.add(globalOffset);
        
        if (getNextChapterId() != -1) {
            pages.add(new ReaderPage(ReaderPage.TYPE_NEXT_CHAPTER_TRIGGER, null, -1));
            pageStartOffsets.add(globalOffset);
        }
        
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private void toggleMenu() {
        if (isMenuVisible) {
            hideMenu();
        } else {
            showMenu();
        }
    }

    private void toggleSettingsPanel() {
        TransitionManager.beginDelayedTransition(bottomSheetContainer);
        if (chapterListPanel.getVisibility() == View.VISIBLE) {
            chapterListPanel.setVisibility(View.GONE);
        }

        if (settingsPanel.getVisibility() == View.VISIBLE) {
            settingsPanel.setVisibility(View.GONE);
        } else {
            settingsPanel.setVisibility(View.VISIBLE);
        }
    }

    private void toggleChapterList() {
        TransitionManager.beginDelayedTransition(bottomSheetContainer);
        if (settingsPanel.getVisibility() == View.VISIBLE) {
            settingsPanel.setVisibility(View.GONE);
        }

        if (chapterListPanel.getVisibility() == View.VISIBLE) {
            chapterListPanel.setVisibility(View.GONE);
        } else {
            chapterListPanel.setVisibility(View.VISIBLE);
            if (!filteredChapterList.isEmpty()) {
                int currentIndex = -1;
                for (int i = 0; i < filteredChapterList.size(); i++) {
                    if (filteredChapterList.get(i).getId() == currentTopicId) {
                        currentIndex = i;
                        break;
                    }
                }
                if (currentIndex != -1) {
                    int finalIndex = currentIndex;
                    rvChapterList.post(() -> {
                        LinearLayoutManager layoutManager = (LinearLayoutManager) rvChapterList.getLayoutManager();
                        if (layoutManager != null) {
                            layoutManager.scrollToPositionWithOffset(finalIndex, 0);
                        }
                    });
                }
            }
        }
    }

    private void showMenu() {
        menuOverlay.setVisibility(View.VISIBLE);
        dimLayer.setClickable(true);
        dimLayer.animate().alpha(1f).setDuration(300).start();
        
        showSystemUI();
        
        topToolbar.animate().translationY(0).setDuration(300).start();
        bottomSheetContainer.animate().translationY(0).setDuration(300).start();
        
        isMenuVisible = true;
    }

    private void hideMenu() {
        dimLayer.setClickable(false);
        dimLayer.animate().alpha(0f).setDuration(300).start();
        
        topToolbar.animate().translationY(-topToolbar.getBottom()).setDuration(300).start();
        
        bottomSheetContainer.animate().translationY(menuOverlay.getHeight() - bottomSheetContainer.getTop()).setDuration(300)
                .withEndAction(() -> {
                    settingsPanel.setVisibility(View.GONE);
                    chapterListPanel.setVisibility(View.GONE);
                    hideSystemUI();
                }).start();
        
        isMenuVisible = false;
    }

    private void hideSystemUI() {
        WindowInsetsControllerCompat controller = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        controller.hide(WindowInsetsCompat.Type.systemBars());
        controller.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
    }

    private void showSystemUI() {
        WindowInsetsControllerCompat controller = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        controller.show(WindowInsetsCompat.Type.systemBars());
        controller.setAppearanceLightStatusBars(false);
    }

    private class ReaderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private List<ReaderPage> data;
        private boolean isVerticalMode;

        ReaderAdapter(List<ReaderPage> data, boolean isVerticalMode) {
            this.data = data;
            this.isVerticalMode = isVerticalMode;
        }
        
        @Override
        public int getItemViewType(int position) {
            return data.get(position).type;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == ReaderPage.TYPE_LOADING || viewType == ReaderPage.TYPE_NEXT_CHAPTER_TRIGGER || viewType == ReaderPage.TYPE_PREV_CHAPTER_TRIGGER) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reader_loading, parent, false);
                return new LoadingViewHolder(view);
            } else if (viewType == ReaderPage.TYPE_COMMENT) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reader_comment_page, parent, false);
                
                if (isVerticalMode) {
                    ViewGroup.LayoutParams params = view.getLayoutParams();
                    params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    view.setLayoutParams(params);
                    
                    RecyclerView rv = view.findViewById(R.id.rvRecommendedTopics);
                    LinearLayout.LayoutParams rvParams = (LinearLayout.LayoutParams) rv.getLayoutParams();
                    rvParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    rvParams.weight = 0;
                    rv.setLayoutParams(rvParams);
                }
                
                return new CommentViewHolder(view);
            } else if (viewType == ReaderPage.TYPE_IMAGE) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reader_image, parent, false);
                
                if (isVerticalMode) {
                    ViewGroup.LayoutParams params = view.getLayoutParams();
                    params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    view.setLayoutParams(params);
                } else {
                    ViewGroup.LayoutParams params = view.getLayoutParams();
                    params.height = ViewGroup.LayoutParams.MATCH_PARENT;
                    view.setLayoutParams(params);
                }
                
                return new ImageViewHolder(view);
            } else {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reader_page, parent, false);
                
                if (isVerticalMode) {
                    ViewGroup.LayoutParams params = view.getLayoutParams();
                    params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    view.setLayoutParams(params);
                    
                    TextView textView = view.findViewById(R.id.pageContentTextView);
                    ViewGroup.LayoutParams textParams = textView.getLayoutParams();
                    textParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    textView.setLayoutParams(textParams);
                    
                    int horizontalPadding = (int) (24 * parent.getContext().getResources().getDisplayMetrics().density);
                    int bottomPadding = (int) (10 * parent.getContext().getResources().getDisplayMetrics().density);
                    textView.setPadding(horizontalPadding, 0, horizontalPadding, bottomPadding);
                }
                
                return new TextViewHolder(view);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ReaderPage page = data.get(position);
            
            if (holder instanceof CommentViewHolder) {
                CommentViewHolder commentHolder = (CommentViewHolder) holder;
                commentHolder.bind(page.chapterId);
            } else if (holder instanceof TextViewHolder) {
                TextViewHolder textHolder = (TextViewHolder) holder;
                textHolder.textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, currentFontSize);
                textHolder.textView.setText(page.content);
                textHolder.textView.setOnClickListener(v -> toggleMenu());
                textHolder.itemView.setOnClickListener(v -> toggleMenu());
            } else if (holder instanceof ImageViewHolder) {
                ImageViewHolder imageHolder = (ImageViewHolder) holder;
                
                int cornerRadius = (int) (12 * holder.itemView.getContext().getResources().getDisplayMetrics().density);
                
                try {
                    Glide.with(imageHolder.imageView.getContext())
                            .load(page.content)
                            .apply(RequestOptions.bitmapTransform(new RoundedCorners(cornerRadius)))
                            .placeholder(R.drawable.placeholder_image)
                            .error(android.R.drawable.ic_menu_report_image)
                            .into(imageHolder.imageView);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                imageHolder.itemView.setOnClickListener(v -> toggleMenu());
            } else if (holder instanceof LoadingViewHolder) {
                LoadingViewHolder loadingHolder = (LoadingViewHolder) holder;
                
                if (page.type == ReaderPage.TYPE_LOADING) {
                    loadingHolder.tvLoading.setText("加载中...");
                    loadingHolder.itemView.setOnClickListener(null);
                } else if (page.type == ReaderPage.TYPE_NEXT_CHAPTER_TRIGGER) {
                    if (isVerticalMode) {
                        loadingHolder.tvLoading.setText("点击跳转下一章");
                        TypedValue typedValue = new TypedValue();
                        holder.itemView.getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, typedValue, true);
                        loadingHolder.itemView.setBackgroundResource(typedValue.resourceId);
                        
                        ViewGroup.LayoutParams params = loadingHolder.itemView.getLayoutParams();
                        if (params != null) {
                            params.height = (int) (60 * holder.itemView.getContext().getResources().getDisplayMetrics().density);
                            loadingHolder.itemView.setLayoutParams(params);
                        }
                        
                        loadingHolder.itemView.setOnClickListener(v -> {
                            if (!isLoadingChapter) {
                                int nextId = getNextChapterId();
                                if (nextId != -1) {
                                    jumpToChapter(nextId);
                                } else {
                                    Toast.makeText(ReaderActivity.this, "没有下一章了", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        loadingHolder.tvLoading.setText("加载中...");
                        loadingHolder.itemView.setOnClickListener(null);
                        loadingHolder.itemView.setBackground(null);
                    }
                } else if (page.type == ReaderPage.TYPE_PREV_CHAPTER_TRIGGER) {
                    if (isVerticalMode) {
                        loadingHolder.tvLoading.setText("点击跳转上一章");
                        TypedValue typedValue = new TypedValue();
                        holder.itemView.getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, typedValue, true);
                        loadingHolder.itemView.setBackgroundResource(typedValue.resourceId);

                        ViewGroup.LayoutParams params = loadingHolder.itemView.getLayoutParams();
                        if (params != null) {
                            params.height = (int) (60 * holder.itemView.getContext().getResources().getDisplayMetrics().density);
                            loadingHolder.itemView.setLayoutParams(params);
                        }

                        loadingHolder.itemView.setOnClickListener(v -> {
                            if (!isLoadingChapter) {
                                int prevId = getPrevChapterId();
                                if (prevId != -1) {
                                    jumpToChapter(prevId, true);
                                } else {
                                    Toast.makeText(ReaderActivity.this, "没有上一章了", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        loadingHolder.tvLoading.setText("加载中...");
                        loadingHolder.itemView.setOnClickListener(null);
                        loadingHolder.itemView.setBackground(null);
                    }
                }
            }
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
        
        public void updateData(List<ReaderPage> newData) {
            this.data = newData;
            notifyDataSetChanged();
        }

        class TextViewHolder extends RecyclerView.ViewHolder {
            TextView textView;

            TextViewHolder(View itemView) {
                super(itemView);
                textView = itemView.findViewById(R.id.pageContentTextView);
            }
        }

        class ImageViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;

            ImageViewHolder(View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.ivReaderImage);
            }
        }

        class LoadingViewHolder extends RecyclerView.ViewHolder {
            TextView tvLoading;
            LoadingViewHolder(View itemView) {
                super(itemView);
                tvLoading = itemView.findViewById(R.id.tvLoading);
            }
        }
        
        class CommentViewHolder extends RecyclerView.ViewHolder {
            TextView tvChapterTitle;
            RecyclerView rvRecommendedTopics;
            TextView tvContinueRead;
            ChapterListAdapter nextChaptersAdapter;
            
            CommentViewHolder(View itemView) {
                super(itemView);
                tvChapterTitle = itemView.findViewById(R.id.tvChapterTitle);
                rvRecommendedTopics = itemView.findViewById(R.id.rvRecommendedTopics);
                tvContinueRead = itemView.findViewById(R.id.tvContinueRead);
                
                rvRecommendedTopics.setLayoutManager(new LinearLayoutManager(itemView.getContext()) {
                    @Override
                    public boolean canScrollVertically() {
                        return !isVerticalMode;
                    }
                });
                
                nextChaptersAdapter = new ChapterListAdapter();
                rvRecommendedTopics.setAdapter(nextChaptersAdapter);
            }
            
            void bind(int chapterId) {
                // 根据模式调整可见性和样式
                if (isVerticalMode) {
                    if (tvChapterTitle != null) tvChapterTitle.setVisibility(View.GONE);
                    if (rvRecommendedTopics != null) rvRecommendedTopics.setVisibility(View.GONE);
                } else {
                    if (tvChapterTitle != null) {
                        tvChapterTitle.setVisibility(View.VISIBLE);
                        tvChapterTitle.setText("章节列表");
                    }
                    if (rvRecommendedTopics != null) rvRecommendedTopics.setVisibility(View.VISIBLE);
                }

                int nextChapterId = getNextChapterId();
                tvContinueRead.setVisibility(View.VISIBLE);
                
                if (nextChapterId != -1) {
                    if (isVerticalMode) {
                        tvContinueRead.setVisibility(View.GONE);
                    } else {
                        tvContinueRead.setText("左滑进入下一章");
                        tvContinueRead.setOnClickListener(v -> jumpToChapter(nextChapterId));
                        tvContinueRead.setVisibility(View.VISIBLE);
                    }
                } else {
                    tvContinueRead.setText("当前为最后一章");
                    tvContinueRead.setOnClickListener(null);
                    tvContinueRead.setVisibility(View.VISIBLE);
                }
                
                if (!isVerticalMode) {
                    ViewGroup.LayoutParams params = tvContinueRead.getLayoutParams();
                    if (params != null) {
                        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                        tvContinueRead.setLayoutParams(params);
                    }
                    tvContinueRead.setGravity(android.view.Gravity.NO_GRAVITY);
                    tvContinueRead.setPadding(0, 0, 0, 0);
                    tvContinueRead.setTextSize(14);
                }
                
                if (!isVerticalMode) {
                    nextChaptersAdapter.updateData(filteredChapterList);
                    
                    int currentIndex = -1;
                    for (int i = 0; i < filteredChapterList.size(); i++) {
                        if (filteredChapterList.get(i).getId() == currentTopicId) {
                            currentIndex = i;
                            break;
                        }
                    }
                    
                    if (currentIndex != -1) {
                        rvRecommendedTopics.scrollToPosition(currentIndex);
                    }
                }
            }
        }
    }
    
    private int getNextChapterId() {
        if (filteredChapterList == null || filteredChapterList.isEmpty()) return -1;
        for (int i = 0; i < filteredChapterList.size(); i++) {
            if (filteredChapterList.get(i).getId() == currentTopicId) {
                if (i + 1 < filteredChapterList.size()) {
                    return filteredChapterList.get(i + 1).getId();
                }
                break;
            }
        }
        return -1;
    }
    
    private int getPrevChapterId() {
        if (filteredChapterList == null || filteredChapterList.isEmpty()) return -1;
        for (int i = 0; i < filteredChapterList.size(); i++) {
            if (filteredChapterList.get(i).getId() == currentTopicId) {
                if (i - 1 >= 0) {
                    return filteredChapterList.get(i - 1).getId();
                }
                break;
            }
        }
        return -1;
    }

    private class ChapterListAdapter extends RecyclerView.Adapter<ChapterListAdapter.ViewHolder> {
        private List<ChapterMenuItem> displayList = new ArrayList<>();

        public void updateData(List<ChapterMenuItem> newData) {
            displayList.clear();
            if (newData != null) {
                displayList.addAll(newData);
            }
            notifyDataSetChanged();
        }
        
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            TextView view = new TextView(parent.getContext());
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            view.setMinHeight((int) (56 * parent.getContext().getResources().getDisplayMetrics().density));
            view.setGravity(android.view.Gravity.CENTER_VERTICAL);
            
            int paddingHorizontal = (int) (16 * parent.getContext().getResources().getDisplayMetrics().density);
            int paddingVertical = (int) (12 * parent.getContext().getResources().getDisplayMetrics().density);
            view.setPadding(paddingHorizontal, paddingVertical, paddingHorizontal, paddingVertical);
            
            view.setTextSize(16);
            
            TypedValue typedValue = new TypedValue();
            parent.getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, typedValue, true);
            view.setBackgroundResource(typedValue.resourceId);
            
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            ChapterMenuItem item = displayList.get(position);
            holder.textView.setText(item.getTitle());
            
            TypedValue typedValue = new TypedValue();
            if (item.getId() == currentTopicId) {
                getTheme().resolveAttribute(com.google.android.material.R.attr.colorPrimary, typedValue, true);
                holder.textView.setTextColor(typedValue.data);
            } else {
                getTheme().resolveAttribute(com.google.android.material.R.attr.colorOnSurface, typedValue, true);
                holder.textView.setTextColor(typedValue.data);
            }
            
            holder.itemView.setOnClickListener(v -> {
                jumpToChapter(item.getId());
                hideMenu();
            });
        }

        @Override
        public int getItemCount() {
            return displayList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView textView;
            ViewHolder(TextView itemView) {
                super(itemView);
                textView = itemView;
            }
        }
    }
}
