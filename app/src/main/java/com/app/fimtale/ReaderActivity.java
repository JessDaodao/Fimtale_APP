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
    private ChapterListAdapter chapterListAdapter;
    
    private static class ReaderPage {
        static final int TYPE_TEXT = 0;
        static final int TYPE_COMMENT = 1;
        static final int TYPE_LOADING = 2;
        
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
        String apiKey = UserPreferences.getApiKey(this);
        String apiPass = UserPreferences.getApiPass(this);

        RetrofitClient.getInstance().getTopicDetail(topicId, apiKey, apiPass, "json").enqueue(new Callback<TopicDetailResponse>() {
            @Override
            public void onResponse(Call<TopicDetailResponse> call, Response<TopicDetailResponse> response) {
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
                        } else {
                             fullChapterContent = "无内容";
                        }
                    }
                    
                    if (data.getMenu() != null) {
                        chapterList = data.getMenu();
                        if (chapterListAdapter != null) {
                            List<ChapterMenuItem> filteredList = new ArrayList<>();
                            for (ChapterMenuItem item : chapterList) {
                                if (!item.getTitle().contains("前言")) {
                                    filteredList.add(item);
                                }
                            }
                            chapterListAdapter.updateData(filteredList);
                        }
                    }
                    
                    currentTopicId = topicId;
                    
                    prepareVerticalContent();
                    calculatePages();
                    
                    if (viewPager.getVisibility() == View.VISIBLE) {
                        viewPager.setCurrentItem(0, false);
                        updateCurrentChapterFromPage(0);
                    } else {
                        recyclerView.scrollToPosition(0);
                        updateCurrentChapterFromParagraph(0);
                    }
                    
                } else {
                    Toast.makeText(ReaderActivity.this, "加载失败: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TopicDetailResponse> call, Throwable t) {
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
        fetchChapterContent(chapterId);
        hideMenu();
    }

    private void updateCurrentChapterFromPage(int pageIndex) {
        updateHeader(chapterTitle, (pageIndex + 1) + "/" + pages.size());
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

        int currentOffset = 0;
        
        chapterVerticalIndices.add(verticalPages.size());
        
        verticalPages.add(new ReaderPage(ReaderPage.TYPE_TEXT, chapterTitle + "\n\n", currentTopicId));
        paragraphStartOffsets.add(currentOffset);
        currentOffset += chapterTitle.length() + 2;

        String[] paragraphs = fullChapterContent.split("\n");
        for (String paragraph : paragraphs) {
            if (!paragraph.trim().isEmpty()) {
                verticalPages.add(new ReaderPage(ReaderPage.TYPE_TEXT, "\u3000\u3000" + paragraph.trim(), currentTopicId));
                paragraphStartOffsets.add(currentOffset);
                currentOffset += paragraph.length() + 1;
            }
        }
        
        verticalPages.add(new ReaderPage(ReaderPage.TYPE_COMMENT, null, currentTopicId));
        paragraphStartOffsets.add(currentOffset);
        
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
        
        int globalOffset = 0;

        String formattedChapterContent = fullChapterContent.replaceAll("(?m)^(?=.)", "\u3000\u3000");

        chapterStartPageIndices.add(pages.size());
        
        String content = chapterTitle + "\n\n" + formattedChapterContent;
        
        StaticLayout layout = StaticLayout.Builder.obtain(content, 0, content.length(), paint, contentWidth)
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
                String pageContent = content.substring(startOffset, endOffset);
                boolean isLastPage = (endLine >= layout.getLineCount() - 1);
                
                if (!isLastPage || !pageContent.trim().isEmpty()) {
                    pages.add(new ReaderPage(ReaderPage.TYPE_TEXT, pageContent, currentTopicId));
                    pageStartOffsets.add(globalOffset + startOffset);
                }
            }
            
            startLine = endLine + 1;
        }
        globalOffset += content.length();
        
        pages.add(new ReaderPage(ReaderPage.TYPE_COMMENT, null, currentTopicId));
        pageStartOffsets.add(globalOffset);
        
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
            if (viewType == ReaderPage.TYPE_LOADING) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reader_loading, parent, false);
                return new RecyclerView.ViewHolder(view) {};
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
                if (nextChapterId != -1) {
                    tvContinueRead.setVisibility(View.VISIBLE);
                    String actionText = isVerticalMode ? "上滑" : "左滑";
                    tvContinueRead.setText(actionText + "进入下一章");
                    tvContinueRead.setOnClickListener(v -> jumpToChapter(nextChapterId));
                    
                    if (isVerticalMode) {
                        ViewGroup.LayoutParams params = tvContinueRead.getLayoutParams();
                        if (params != null) {
                            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                            tvContinueRead.setLayoutParams(params);
                        }
                        tvContinueRead.setGravity(android.view.Gravity.CENTER);
                        int padding = (int) (16 * itemView.getContext().getResources().getDisplayMetrics().density);
                        tvContinueRead.setPadding(0, padding, 0, padding);
                        tvContinueRead.setTextSize(16);
                    } else {
                        ViewGroup.LayoutParams params = tvContinueRead.getLayoutParams();
                        if (params != null) {
                            params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                            tvContinueRead.setLayoutParams(params);
                        }
                        tvContinueRead.setGravity(android.view.Gravity.NO_GRAVITY);
                        tvContinueRead.setPadding(0, 0, 0, 0);
                        tvContinueRead.setTextSize(14);
                    }
                } else {
                    tvContinueRead.setVisibility(View.GONE);
                }
                
                if (!isVerticalMode) {
                    List<ChapterMenuItem> nextChapters = new ArrayList<>();
                    if (chapterList != null) {
                        int currentIndex = -1;
                        for (int i = 0; i < chapterList.size(); i++) {
                            if (chapterList.get(i).getId() == currentTopicId) {
                                currentIndex = i;
                                break;
                            }
                        }
                        
                        if (currentIndex != -1 && currentIndex < chapterList.size() - 1) {
                            for (int i = currentIndex + 1; i < chapterList.size(); i++) {
                                ChapterMenuItem item = chapterList.get(i);
                                if (!item.getTitle().contains("前言")) {
                                    nextChapters.add(item);
                                }
                            }
                        }
                    }
                    nextChaptersAdapter.updateData(nextChapters);
                }
            }
        }
    }
    
    private int getNextChapterId() {
        if (chapterList == null || chapterList.isEmpty()) return -1;
        for (int i = 0; i < chapterList.size(); i++) {
            if (chapterList.get(i).getId() == currentTopicId) {
                if (i + 1 < chapterList.size()) {
                    return chapterList.get(i + 1).getId();
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
