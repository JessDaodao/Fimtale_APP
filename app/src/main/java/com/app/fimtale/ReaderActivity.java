package com.app.fimtale;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowInsets;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.core.view.GravityCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.core.view.ViewCompat;
import androidx.core.graphics.Insets;
import android.graphics.Color;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import androidx.transition.TransitionManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import java.util.ArrayList;
import java.util.List;

public class ReaderActivity extends AppCompatActivity {

    public static final String EXTRA_TOPIC_ID = "topic_id";
    public static final String EXTRA_CHAPTER_ID = "chapter_id";

    private ViewPager2 viewPager;
    private RecyclerView recyclerView;
    private View menuOverlay;
    private View dimLayer;
    private MaterialToolbar topToolbar;
    private LinearLayout bottomSheetContainer;
    private View bottomMenu;
    private View btnChapterList;
    private View btnSettings;
    
    private View readerHeader;
    private View readerFooter;
    private TextView tvChapterTitle;
    private TextView tvChapterProgress;

    private LinearLayout settingsPanel;
    private LinearLayout chapterListPanel;
    private RecyclerView rvChapterList;
    private TextView tvFontSize;
    private Button btnDecreaseFont, btnIncreaseFont;
    private RadioGroup rgPageMode;
    private RadioButton rbHorizontal, rbVertical;

    private boolean isMenuVisible = false;
    private List<String> pages = new ArrayList<>();
    private List<String> verticalPages = new ArrayList<>();
    private List<Integer> chapterStartPageIndices = new ArrayList<>();
    private List<Integer> chapterVerticalIndices = new ArrayList<>();
    private List<Integer> pageStartOffsets = new ArrayList<>();
    private List<Integer> paragraphStartOffsets = new ArrayList<>();
    private ReaderAdapter adapter;
    private ReaderAdapter recyclerAdapter;
    private int currentChapterId = 1;
    private int lastWidth = 0;
    private int lastHeight = 0;
    
    private float currentFontSize = 20f;
    private SharedPreferences prefs;
    private Insets lastSystemBars = null;

    private String fullChapterContent = "第一章：新的开始\n\n" +
            "这是一个关于勇气与冒险的故事。在遥远的东方，有一座神秘的山峰，云雾缭绕，传说中住着一位仙人。\n" +
            "李明是一个普通的少年，但他心中怀揣着不平凡的梦想。他渴望登上那座山峰，去探寻传说中的秘密。\n" +
            "清晨的阳光洒在小村庄上，金色的光辉给大地披上了一层暖意。李明背起行囊，告别了年迈的父母，踏上了征途。\n" +
            "路途遥远且艰辛，但他从未想过放弃。每当遇到困难，他都会想起父亲的教诲：“只要心中有光，脚下就有路。”\n" +
            "他穿过了茂密的森林，跨过了湍急的河流。在森林里，他遇到了各种奇异的生物，有的友善，有的凶猛。他学会了如何与自然相处，如何在险境中求生。\n" +
            "夜幕降临，繁星点点。李明躺在草地上，仰望星空。他想起了小时候听过的故事，那些关于英雄的传说。他相信，自己终有一天也能成为故事里的主角。\n" +
            "日子一天天过去，李明离那座神秘的山峰越来越近。他能感受到空气中弥漫着一股古老而神秘的气息。\n" +
            "终于，他来到了山脚下。抬头望去，山峰高耸入云，仿佛连接着天地。他深吸一口气，迈出了登山的第一步。\n" +
            "山路崎岖难行，每一步都需要付出巨大的努力。汗水浸湿了他的衣衫，但他的眼神依然坚定。\n" +
            "在半山腰，他遇到了一位采药的老人。老人看着他，微笑着说：“年轻人，你的眼神里有光。继续前行吧，山顶的风景会让你不虚此行。”\n" +
            "李明谢过老人，继续向上攀登。随着海拔的升高，气温逐渐降低，寒风呼啸。但他心中的火焰却越烧越旺。\n" +
            "经过数日的艰难攀登，李明终于登上了山顶。眼前的景象让他惊呆了。云海翻腾，金光万道，仿佛置身于仙境之中。\n" +
            "他在山顶的一块巨石上坐下，静静地感受着这一刻的宁静与美好。他明白，这段旅程不仅让他看到了美景，更让他找回了真实的自己。\n" +
            "（以下为重复内容以测试分页功能）\n" +
            "这是一个关于勇气与冒险的故事。在遥远的东方，有一座神秘的山峰，云雾缭绕，传说中住着一位仙人。\n" +
            "李明是一个普通的少年，但他心中怀揣着不平凡的梦想。他渴望登上那座山峰，去探寻传说中的秘密。\n" +
            "清晨的阳光洒在小村庄上，金色的光辉给大地披上了一层暖意。李明背起行囊，告别了年迈的父母，踏上了征途。\n" +
            "路途遥远且艰辛，但他从未想过放弃。每当遇到困难，他都会想起父亲的教诲：“只要心中有光，脚下就有路。”\n" +
            "他穿过了茂密的森林，跨过了湍急的河流。在森林里，他遇到了各种奇异的生物，有的友善，有的凶猛。他学会了如何与自然相处，如何在险境中求生。\n" +
            "夜幕降临，繁星点点。李明躺在草地上，仰望星空。他想起了小时候听过的故事，那些关于英雄的传说。他相信，自己终有一天也能成为故事里的主角。\n" +
            "日子一天天过去，李明离那座神秘的山峰越来越近。他能感受到空气中弥漫着一股古老而神秘的气息。\n" +
            "终于，他来到了山脚下。抬头望去，山峰高耸入云，仿佛连接着天地。他深吸一口气，迈出了登山的第一步。\n" +
            "山路崎岖难行，每一步都需要付出巨大的努力。汗水浸湿了他的衣衫，但他的眼神依然坚定。\n" +
            "在半山腰，他遇到了一位采药的老人。老人看着他，微笑着说：“年轻人，你的眼神里有光。继续前行吧，山顶的风景会让你不虚此行。”\n" +
            "李明谢过老人，继续向上攀登。随着海拔的升高，气温逐渐降低，寒风呼啸。但他心中的火焰却越烧越旺。\n" +
            "经过数日的艰难攀登，李明终于登上了山顶。眼前的景象让他惊呆了。云海翻腾，金光万道，仿佛置身于仙境之中。\n" +
            "他在山顶的一块巨石上坐下，静静地感受着这一刻的宁静与美好。他明白，这段旅程不仅让他看到了美景，更让他找回了真实的自己。\n";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);

        viewPager = findViewById(R.id.viewPager);
        recyclerView = findViewById(R.id.recyclerView);
        menuOverlay = findViewById(R.id.menuOverlay);
        dimLayer = findViewById(R.id.dimLayer);
        topToolbar = findViewById(R.id.topToolbar);
        bottomSheetContainer = findViewById(R.id.bottomSheetContainer);
        bottomMenu = findViewById(R.id.bottomMenu);
        btnChapterList = findViewById(R.id.btnChapterList);
        btnSettings = findViewById(R.id.btnSettings);

        settingsPanel = findViewById(R.id.settingsPanel);
        chapterListPanel = findViewById(R.id.chapterListPanel);
        rvChapterList = findViewById(R.id.rvChapterList);
        
        readerHeader = findViewById(R.id.readerHeader);
        readerFooter = findViewById(R.id.readerFooter);
        tvChapterTitle = findViewById(R.id.tvChapterTitle);
        tvChapterProgress = findViewById(R.id.tvChapterProgress);
        
        tvFontSize = findViewById(R.id.tvFontSize);
        btnDecreaseFont = findViewById(R.id.btnDecreaseFont);
        btnIncreaseFont = findViewById(R.id.btnIncreaseFont);
        rgPageMode = findViewById(R.id.rgPageMode);
        rbHorizontal = findViewById(R.id.rbHorizontal);
        rbVertical = findViewById(R.id.rbVertical);

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

        prepareVerticalContent();

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

        if (isVertical) {
            rbVertical.setChecked(true);
        } else {
            rbHorizontal.setChecked(true);
        }

        btnDecreaseFont.setOnClickListener(v -> {
            if (currentFontSize > 12) {
                currentFontSize -= 2;
                updateFontSize(currentFontSize);
                saveFontSize();
            }
        });

        btnIncreaseFont.setOnClickListener(v -> {
            if (currentFontSize < 40) {
                currentFontSize += 2;
                updateFontSize(currentFontSize);
                saveFontSize();
            }
        });

        rgPageMode.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbHorizontal) {
                updatePageMode(false);
                prefs.edit().putBoolean("reader_is_vertical", false).apply();
            } else if (checkedId == R.id.rbVertical) {
                updatePageMode(true);
                prefs.edit().putBoolean("reader_is_vertical", true).apply();
            }
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
    }
    
    private void setupChapterList() {
        rvChapterList.setLayoutManager(new LinearLayoutManager(this));
        rvChapterList.setAdapter(new ChapterListAdapter());
    }

    private void jumpToChapter(int chapterId) {
        int index = chapterId - 1;
        
        currentChapterId = chapterId;
        topToolbar.setTitle("第" + currentChapterId + "章");
        if (rvChapterList.getAdapter() != null) {
            rvChapterList.getAdapter().notifyDataSetChanged();
        }

        if (recyclerView.getVisibility() == View.VISIBLE) {
            if (index >= 0 && index < chapterVerticalIndices.size()) {
                int position = chapterVerticalIndices.get(index);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null) {
                    layoutManager.scrollToPositionWithOffset(position, 0);
                }
            }
        } else {
            if (index >= 0 && index < chapterStartPageIndices.size()) {
                int pageIndex = chapterStartPageIndices.get(index);
                viewPager.setCurrentItem(pageIndex, false);
            }
        }
    }

    private void updateCurrentChapterFromPage(int pageIndex) {
        int newChapterId = 1;
        for (int i = 0; i < chapterStartPageIndices.size(); i++) {
            if (pageIndex >= chapterStartPageIndices.get(i)) {
                newChapterId = i + 1;
            } else {
                break;
            }
        }
        updateChapterUI(newChapterId);
        
        if (newChapterId > 0 && newChapterId <= chapterStartPageIndices.size()) {
            int startPage = chapterStartPageIndices.get(newChapterId - 1);
            int endPage = (newChapterId < chapterStartPageIndices.size()) 
                          ? chapterStartPageIndices.get(newChapterId) 
                          : pages.size();
            int totalPages = endPage - startPage;
            int currentPageInChapter = pageIndex - startPage + 1;
            
            // Ensure valid range
            if (currentPageInChapter < 1) currentPageInChapter = 1;
            if (currentPageInChapter > totalPages) currentPageInChapter = totalPages;
            if (totalPages < 1) totalPages = 1;

            updateHeader(newChapterId, currentPageInChapter + "/" + totalPages);
        }
    }

    private void updateCurrentChapterFromParagraph(int paragraphIndex) {
        int newChapterId = 1;
        for (int i = 0; i < chapterVerticalIndices.size(); i++) {
            if (paragraphIndex >= chapterVerticalIndices.get(i)) {
                newChapterId = i + 1;
            } else {
                break;
            }
        }
        updateChapterUI(newChapterId);
        
        if (newChapterId > 0 && newChapterId <= chapterVerticalIndices.size()) {
            int startItem = chapterVerticalIndices.get(newChapterId - 1);
            int endItem = (newChapterId < chapterVerticalIndices.size())
                          ? chapterVerticalIndices.get(newChapterId)
                          : verticalPages.size();
            int totalItems = endItem - startItem;
            int currentItemInChapter = paragraphIndex - startItem + 1;
            
            if (currentItemInChapter < 1) currentItemInChapter = 1;
            if (currentItemInChapter > totalItems) currentItemInChapter = totalItems;
            if (totalItems < 1) totalItems = 1;
            
            float percent = (float)currentItemInChapter * 100 / totalItems;
            updateHeader(newChapterId, String.format("%.1f%%", percent));
        }
    }
    
    private void updateHeader(int chapterId, String progressText) {
        tvChapterTitle.setText("第" + chapterId + "章");
        tvChapterProgress.setText(progressText);
    }

    private void updateChapterUI(int newChapterId) {
        if (newChapterId != currentChapterId) {
            currentChapterId = newChapterId;
            topToolbar.setTitle("第" + currentChapterId + "章");
            if (rvChapterList.getAdapter() != null) {
                rvChapterList.getAdapter().notifyDataSetChanged();
            }
        }
    }

    private void updateFontSize(float size) {
        tvFontSize.setText(String.valueOf((int) size));
        calculatePages();
        if (recyclerView.getVisibility() == View.VISIBLE) {
            recyclerAdapter.notifyDataSetChanged();
        }
    }

    private void saveFontSize() {
        prefs.edit().putFloat("reader_font_size", currentFontSize).apply();
    }

    private void prepareVerticalContent() {
        verticalPages.clear();
        paragraphStartOffsets.clear();
        chapterVerticalIndices.clear();
        
        int currentOffset = 0;
        for (int i = 1; i <= 10; i++) {
            chapterVerticalIndices.add(verticalPages.size());
            String chapterTitle = "第" + i + "章\n\n";
            verticalPages.add(chapterTitle);
            paragraphStartOffsets.add(currentOffset);
            currentOffset += chapterTitle.length();

            String[] paragraphs = fullChapterContent.split("\n");
            for (String paragraph : paragraphs) {
                if (!paragraph.trim().isEmpty()) {
                    verticalPages.add("\u3000\u3000" + paragraph + "\n");
                    paragraphStartOffsets.add(currentOffset);
                    currentOffset += paragraph.length() + 1;
                } else {
                    currentOffset += 1;
                }
            }
        }
    }

    private void updatePageMode(boolean isVertical) {
        if (isVertical) {
            int currentItem = viewPager.getCurrentItem();
            int offset = 0;
            if (currentItem < pageStartOffsets.size()) {
                offset = pageStartOffsets.get(currentItem);
            }
            
            int targetPosition = 0;
            for (int i = 0; i < paragraphStartOffsets.size(); i++) {
                if (paragraphStartOffsets.get(i) <= offset) {
                    targetPosition = i;
                } else {
                    break;
                }
            }

            viewPager.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.scrollToPosition(targetPosition);
            
            readerHeader.setBackgroundColor(getThemeColor(android.R.attr.colorBackground));
            readerFooter.setBackgroundColor(getThemeColor(android.R.attr.colorBackground));
            
            updateCurrentChapterFromParagraph(targetPosition);
        } else {
            LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            int firstVisibleItem = 0;
            if (layoutManager != null) {
                firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
            }
            if (firstVisibleItem == RecyclerView.NO_POSITION) firstVisibleItem = 0;
            
            int offset = 0;
            if (firstVisibleItem < paragraphStartOffsets.size()) {
                offset = paragraphStartOffsets.get(firstVisibleItem);
            }

            int targetPage = 0;
            for (int i = 0; i < pageStartOffsets.size(); i++) {
                if (pageStartOffsets.get(i) <= offset) {
                    targetPage = i;
                } else {
                    break;
                }
            }
            
            recyclerView.setVisibility(View.GONE);
            viewPager.setVisibility(View.VISIBLE);
            viewPager.setCurrentItem(targetPage, false);
            
            readerHeader.setBackground(null);
            readerFooter.setBackground(null);
            
            updateCurrentChapterFromPage(targetPage);
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

        String formattedChapterContent = fullChapterContent.replaceAll("(?m)^", "\u3000\u3000");

        for (int i = 1; i <= 10; i++) {
            chapterStartPageIndices.add(pages.size());
            
            String chapterTitle = "第" + i + "章\n\n";
            String content = chapterTitle + formattedChapterContent;
            
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
                
                pages.add(content.substring(startOffset, endOffset));
                pageStartOffsets.add(globalOffset + startOffset);
                
                startLine = endLine + 1;
            }
            globalOffset += content.length();
        }
        
        adapter.notifyDataSetChanged();
        
        if (!chapterStartPageIndices.isEmpty()) {
            if (viewPager.getVisibility() == View.VISIBLE) {
                updateCurrentChapterFromPage(viewPager.getCurrentItem());
            }
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

    private class ReaderAdapter extends RecyclerView.Adapter<ReaderAdapter.ViewHolder> {
        private List<String> data;
        private boolean isVerticalMode;

        ReaderAdapter(List<String> data, boolean isVerticalMode) {
            this.data = data;
            this.isVerticalMode = isVerticalMode;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
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
                int verticalPadding = (int) (2 * parent.getContext().getResources().getDisplayMetrics().density);
                textView.setPadding(horizontalPadding, verticalPadding, horizontalPadding, verticalPadding);
            }
            
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, currentFontSize);
            holder.textView.setText(data.get(position));
            holder.textView.setOnClickListener(v -> toggleMenu());
            holder.itemView.setOnClickListener(v -> toggleMenu());
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
        
        public void updateData(List<String> newData) {
            this.data = newData;
            notifyDataSetChanged();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView textView;

            ViewHolder(View itemView) {
                super(itemView);
                textView = itemView.findViewById(R.id.pageContentTextView);
            }
        }
    }

    private class ChapterListAdapter extends RecyclerView.Adapter<ChapterListAdapter.ViewHolder> {
        private int count = 10;

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            TextView view = new TextView(parent.getContext());
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            int padding = (int) (16 * parent.getContext().getResources().getDisplayMetrics().density);
            view.setPadding(padding, padding, padding, padding);
            view.setTextSize(16);
            
            TypedValue typedValue = new TypedValue();
            parent.getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, typedValue, true);
            view.setBackgroundResource(typedValue.resourceId);
            
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            int chapterId = position + 1;
            holder.textView.setText("第" + chapterId + "章");
            
            TypedValue typedValue = new TypedValue();
            if (chapterId == currentChapterId) {
                getTheme().resolveAttribute(com.google.android.material.R.attr.colorPrimary, typedValue, true);
                holder.textView.setTextColor(typedValue.data);
            } else {
                getTheme().resolveAttribute(com.google.android.material.R.attr.colorOnSurface, typedValue, true);
                holder.textView.setTextColor(typedValue.data);
            }
            
            holder.itemView.setOnClickListener(v -> {
                jumpToChapter(chapterId);
                hideMenu();
            });
        }

        @Override
        public int getItemCount() {
            return count;
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
