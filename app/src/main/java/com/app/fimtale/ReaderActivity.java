package com.app.fimtale;

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
import android.view.WindowInsetsController;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import java.util.ArrayList;
import java.util.List;

public class ReaderActivity extends AppCompatActivity {

    public static final String EXTRA_TOPIC_ID = "topic_id";
    public static final String EXTRA_CHAPTER_ID = "chapter_id";

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ViewPager2 viewPager;
    private View menuOverlay;
    private MaterialToolbar topToolbar;
    private View bottomMenu;
    private View btnChapterList;
    
    private boolean isMenuVisible = false;
    private List<String> pages = new ArrayList<>();
    private List<Integer> chapterStartPageIndices = new ArrayList<>();
    private ReaderAdapter adapter;
    private int currentChapterId = 1;

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

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        viewPager = findViewById(R.id.viewPager);
        menuOverlay = findViewById(R.id.menuOverlay);
        topToolbar = findViewById(R.id.topToolbar);
        bottomMenu = findViewById(R.id.bottomMenu);
        btnChapterList = findViewById(R.id.btnChapterList);

        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(android.R.attr.colorBackground, typedValue, true);
        getWindow().setStatusBarColor(typedValue.data);

        topToolbar.setNavigationOnClickListener(v -> finish());
        
        btnChapterList.setOnClickListener(v -> {
            hideMenu();
            drawerLayout.openDrawer(GravityCompat.START);
        });

        adapter = new ReaderAdapter(pages);
        viewPager.setAdapter(adapter);

        viewPager.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                viewPager.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                calculatePages();
                
                topToolbar.setTranslationY(-topToolbar.getHeight());
                bottomMenu.setTranslationY(bottomMenu.getHeight());
            }
        });

        hideSystemUI();
        
        setupChapterList();
        
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateCurrentChapter(position);
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
        Menu menu = navigationView.getMenu();
        menu.clear();
        for (int i = 1; i <= 10; i++) {
            menu.add(0, i, i, "第" + i + "章");
        }
        navigationView.setNavigationItemSelectedListener(item -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            int chapterId = item.getItemId();
            jumpToChapter(chapterId);
            return true;
        });
    }

    private void jumpToChapter(int chapterId) {
        int index = chapterId - 1;
        if (index >= 0 && index < chapterStartPageIndices.size()) {
            int pageIndex = chapterStartPageIndices.get(index);
            viewPager.setCurrentItem(pageIndex, false);
        }
    }

    private void updateCurrentChapter(int pageIndex) {
        int newChapterId = 1;
        for (int i = 0; i < chapterStartPageIndices.size(); i++) {
            if (pageIndex >= chapterStartPageIndices.get(i)) {
                newChapterId = i + 1;
            } else {
                break;
            }
        }
        
        if (newChapterId != currentChapterId) {
            currentChapterId = newChapterId;
            navigationView.setCheckedItem(currentChapterId);
            topToolbar.setTitle("第" + currentChapterId + "章");
        }
    }

    private void calculatePages() {
        int width = viewPager.getWidth();
        int height = viewPager.getHeight();
        
        int padding = (int) (24 * getResources().getDisplayMetrics().density);
        int contentWidth = width - padding * 2;
        int contentHeight = height - padding * 2;

        if (contentWidth <= 0 || contentHeight <= 0) return;

        TextPaint paint = new TextPaint();
        paint.setTextSize(20 * getResources().getDisplayMetrics().scaledDensity);
        paint.setAntiAlias(true);
        paint.setColor(getResources().getColor(android.R.color.primary_text_dark, getTheme()));

        pages.clear();
        chapterStartPageIndices.clear();
        
        for (int i = 1; i <= 10; i++) {
            chapterStartPageIndices.add(pages.size());
            
            String chapterTitle = "第" + i + "章\n\n";
            String content = chapterTitle + fullChapterContent;
            
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
                
                startLine = endLine + 1;
            }
        }
        
        adapter.notifyDataSetChanged();
        
        if (!chapterStartPageIndices.isEmpty()) {
            updateCurrentChapter(viewPager.getCurrentItem());
        }
    }

    private void toggleMenu() {
        if (isMenuVisible) {
            hideMenu();
        } else {
            showMenu();
        }
    }

    private void showMenu() {
        menuOverlay.setVisibility(View.VISIBLE);
        showSystemUI();
        
        topToolbar.animate().translationY(0).setDuration(300).start();
        bottomMenu.animate().translationY(0).setDuration(300).start();
        
        isMenuVisible = true;
    }

    private void hideMenu() {
        topToolbar.animate().translationY(-topToolbar.getHeight()).setDuration(300).start();
        bottomMenu.animate().translationY(bottomMenu.getHeight()).setDuration(300)
                .withEndAction(() -> {
                    menuOverlay.setVisibility(View.INVISIBLE);
                    hideSystemUI();
                }).start();
        
        isMenuVisible = false;
    }

    private void hideSystemUI() {
        WindowInsetsController controller = getWindow().getInsetsController();
        if (controller != null) {
            // 仅隐藏导航栏，保留状态栏
            controller.hide(WindowInsets.Type.navigationBars());
            controller.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
        }
    }

    private void showSystemUI() {
        WindowInsetsController controller = getWindow().getInsetsController();
        if (controller != null) {
            controller.show(WindowInsets.Type.navigationBars());
        }
    }

    private class ReaderAdapter extends RecyclerView.Adapter<ReaderAdapter.ViewHolder> {
        private List<String> data;

        ReaderAdapter(List<String> data) {
            this.data = data;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reader_page, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.textView.setText(data.get(position));
            holder.textView.setOnClickListener(v -> toggleMenu());
            holder.itemView.setOnClickListener(v -> toggleMenu());
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView textView;

            ViewHolder(View itemView) {
                super(itemView);
                textView = itemView.findViewById(R.id.pageContentTextView);
            }
        }
    }
}
