package com.app.fimtale.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.app.fimtale.MainActivity;
import com.app.fimtale.R;
import com.app.fimtale.adapter.BannerAdapter;
import com.app.fimtale.adapter.TopicAdapter;
import com.app.fimtale.model.RecommendedTopic;
import com.app.fimtale.model.Tags;
import com.app.fimtale.model.Topic;
import com.app.fimtale.model.TopicViewItem;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class HomeFragment extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private NestedScrollView scrollView;
    private LinearLayout contentLayout;
    private TabLayout tabLayout;
    private ViewPager2 bannerViewPager;
    private RecyclerView recyclerViewHot, recyclerViewNew;
    private ProgressBar progressBar;
    private TextView errorTextView, listTitleTextView;
    private Button viewMoreButton;
    private BannerAdapter bannerAdapter;
    private TopicAdapter adapterHot, adapterNew;
    private List<RecommendedTopic> bannerList = new ArrayList<>();
    private List<TopicViewItem> topicListHot = new ArrayList<>();
    private List<TopicViewItem> topicListNew = new ArrayList<>();
    private Timer bannerTimer;
    private Handler bannerHandler = new Handler(Looper.getMainLooper());
    private View rootView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_home, container, false);
        } else {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null) {
                parent.removeView(rootView);
            }
        }
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (scrollView != null) {
            return;
        }

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        scrollView = view.findViewById(R.id.scrollView);
        contentLayout = view.findViewById(R.id.contentLayout);
        bannerViewPager = view.findViewById(R.id.bannerViewPager);
        recyclerViewHot = view.findViewById(R.id.recyclerViewHot);
        recyclerViewNew = view.findViewById(R.id.recyclerViewNew);
        progressBar = view.findViewById(R.id.progressBar);
        errorTextView = view.findViewById(R.id.errorTextView);
        listTitleTextView = view.findViewById(R.id.listTitleTextView);
        tabLayout = view.findViewById(R.id.tabLayout);
        viewMoreButton = view.findViewById(R.id.viewMoreButton);

        setupBannerViewPager();
        setupRecyclerView();
        setupTabLayout();
        setupSwipeRefresh();

        fetchHomePageData();
    }

    private void setupSwipeRefresh() {
        swipeRefreshLayout.setColorSchemeResources(R.color.md_theme_light_primary);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            generateBannerData();
            generateTopicData(false);
        });
    }

    private void setupBannerViewPager() {
        bannerAdapter = new BannerAdapter(bannerList);
        bannerViewPager.setAdapter(bannerAdapter);
        bannerViewPager.setClipToPadding(false);
        bannerViewPager.setClipChildren(false);
        bannerViewPager.setOffscreenPageLimit(3);
        CompositePageTransformer compositeTransformer = new CompositePageTransformer();
        compositeTransformer.addTransformer(new MarginPageTransformer(getResources().getDimensionPixelOffset(R.dimen.page_margin)));
        compositeTransformer.addTransformer((page, position) -> {
            float r = 1 - Math.abs(position);
            page.setScaleY(0.85f + r * 0.15f);
        });
        bannerViewPager.setPageTransformer(compositeTransformer);
    }

    private void setupTabLayout() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    listTitleTextView.setText("近日热门");
                    recyclerViewHot.setVisibility(View.VISIBLE);
                    recyclerViewNew.setVisibility(View.GONE);
                } else {
                    listTitleTextView.setText("最近更新");
                    recyclerViewHot.setVisibility(View.GONE);
                    recyclerViewNew.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        // 初始状态
        listTitleTextView.setText("近日热门");
    }

    private void setupRecyclerView() {
        recyclerViewHot.setLayoutManager(new LinearLayoutManager(getContext()));
        adapterHot = new TopicAdapter(topicListHot);
        adapterHot.setPaginationEnabled(false);
        recyclerViewHot.setAdapter(adapterHot);

        recyclerViewNew.setLayoutManager(new LinearLayoutManager(getContext()));
        adapterNew = new TopicAdapter(topicListNew);
        adapterNew.setPaginationEnabled(false);
        recyclerViewNew.setAdapter(adapterNew);

        viewMoreButton.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                MainActivity mainActivity = (MainActivity) getActivity();
                BottomNavigationView bottomNav = mainActivity.findViewById(R.id.bottom_navigation);
                bottomNav.setSelectedItemId(R.id.nav_article);
            }
        });
    }

    private void fetchHomePageData() {
        progressBar.setVisibility(View.VISIBLE);
        errorTextView.setVisibility(View.GONE);
        scrollView.setVisibility(View.INVISIBLE);
        contentLayout.setVisibility(View.VISIBLE);

        generateBannerData();
        generateTopicData(true);
    }

    private void generateBannerData() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (!isAdded()) return;

            stopBannerAutoScroll();
            bannerList.clear();
            Random random = new Random();
            String[] bannerTitles = {"滚动图标题", "滚动图标题", "滚动图标题", "滚动图标题", "滚动图标题"};
            String[] bannerDescriptions = {
                    "滚动图介绍",
                    "滚动图介绍",
                    "滚动图介绍",
                    "滚动图介绍",
                    "滚动图介绍"
            };

            for (int i = 0; i < 5; i++) {
                RecommendedTopic topic = new RecommendedTopic();
                try {
                    java.lang.reflect.Field idField = RecommendedTopic.class.getDeclaredField("id");
                    idField.setAccessible(true);
                    idField.set(topic, i + 1);

                    java.lang.reflect.Field titleField = RecommendedTopic.class.getDeclaredField("title");
                    titleField.setAccessible(true);
                    titleField.set(topic, bannerTitles[i]);

                    java.lang.reflect.Field backgroundField = RecommendedTopic.class.getDeclaredField("background");
                    backgroundField.setAccessible(true);
                    backgroundField.set(topic, "https://dreamlandcon.top/img/sample.jpg");

                    java.lang.reflect.Field recommendWordField = RecommendedTopic.class.getDeclaredField("recommendWord");
                    recommendWordField.setAccessible(true);
                    recommendWordField.set(topic, bannerDescriptions[i]);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                bannerList.add(topic);
            }
            bannerAdapter.notifyDataSetChanged();
            bannerViewPager.setCurrentItem(0, false);
            bannerViewPager.setVisibility(View.VISIBLE);
            startBannerAutoScroll();
        }, 1000);
    }

    private void generateTopicData(boolean animate) {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (!isAdded()) return;

            if (animate) {
                progressBar.animate()
                        .alpha(0f)
                        .setDuration(300)
                        .withEndAction(() -> {
                            progressBar.setVisibility(View.GONE);
                            progressBar.setAlpha(1f);

                            scrollView.setAlpha(0f);
                            scrollView.setScaleX(0.9f);
                            scrollView.setScaleY(0.9f);
                            scrollView.setVisibility(View.VISIBLE);

                            android.view.animation.PathInterpolator interpolator = new android.view.animation.PathInterpolator(1.00f, 0.00f, 0.28f, 1.00f);

                            scrollView.animate()
                                    .alpha(1f)
                                    .scaleX(1f)
                                    .scaleY(1f)
                                    .setInterpolator(interpolator)
                                    .setDuration(500)
                                    .start();
                        })
                        .start();
            } else {
                progressBar.setVisibility(View.GONE);
                scrollView.setVisibility(View.VISIBLE);
                scrollView.setAlpha(1f);
                scrollView.setScaleX(1f);
                scrollView.setScaleY(1f);
            }

            topicListHot.clear();
            topicListNew.clear();
            Random random = new Random();
            String[] topicTitles = {"文章1", "文章2", "文章3", "文章4", "文章5"};
            String[] authors = {"作者A", "作者B", "作者C", "作者D", "作者E"};
            String[] intros = {
                "1111111111",
                "2222",
                "33333333333333",
                "444444444444444",
                "55555555555555555"
            };

            for (int i = 0; i < 20; i++) {
                Topic topic = new Topic();
                topic.setId(i + 1);
                topic.setTitle(topicTitles[random.nextInt(topicTitles.length)]);
                topic.setAuthorName(authors[random.nextInt(authors.length)]);
                topic.setBackground("https://dreamlandcon.top/img/sample.jpg");
                topic.setIntro(intros[random.nextInt(intros.length)]);

                Tags topicTags = new Tags();
                topicTags.setType("类型" + (i % 3 + 1));
                topicTags.setSource("来源" + (i % 2 + 1));
                topicTags.setRating("评级" + (i % 3 + 1));
                topic.setTags(topicTags);

                topicListHot.add(new TopicViewItem(topic));
            }

            for (int i = 0; i < 20; i++) {
                Topic topic = new Topic();
                topic.setId(i + 100);
                topic.setTitle(topicTitles[random.nextInt(topicTitles.length)]);
                topic.setAuthorName(authors[random.nextInt(authors.length)]);
                topic.setBackground("https://dreamlandcon.top/img/sample.jpg");
                topic.setIntro(intros[random.nextInt(intros.length)]);

                Tags topicTags = new Tags();
                topicTags.setType("类型" + (i % 3 + 1));
                topicTags.setSource("来源" + (i % 2 + 1));
                topicTags.setRating("评级" + (i % 3 + 1));
                topic.setTags(topicTags);

                topicListNew.add(new TopicViewItem(topic));
            }

            adapterHot.notifyDataSetChanged();
            adapterNew.notifyDataSetChanged();
            
            if (tabLayout.getSelectedTabPosition() == 0) {
                recyclerViewHot.setVisibility(View.VISIBLE);
                recyclerViewNew.setVisibility(View.GONE);
            } else {
                recyclerViewHot.setVisibility(View.GONE);
                recyclerViewNew.setVisibility(View.VISIBLE);
            }
            listTitleTextView.setVisibility(View.VISIBLE);
            viewMoreButton.setVisibility(View.VISIBLE);

            if (!animate) {
                swipeRefreshLayout.setRefreshing(false);
            }
        }, 1000);
    }

    private void startBannerAutoScroll() {
        stopBannerAutoScroll();
        bannerTimer = new Timer();
        bannerTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                bannerHandler.post(() -> {
                    if (bannerViewPager != null && bannerAdapter != null) {
                        int currentItem = bannerViewPager.getCurrentItem();
                        int totalItems = bannerAdapter.getItemCount();
                        if (totalItems > 1) {
                            bannerViewPager.setCurrentItem((currentItem + 1) % totalItems, true);
                        }
                    }
                });
            }
        }, 5000, 5000);
    }

    private void stopBannerAutoScroll() {
        if (bannerTimer != null) {
            bannerTimer.cancel();
            bannerTimer = null;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        stopBannerAutoScroll();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (bannerAdapter != null && bannerAdapter.getItemCount() > 0) {
            startBannerAutoScroll();
        }
    }
}
