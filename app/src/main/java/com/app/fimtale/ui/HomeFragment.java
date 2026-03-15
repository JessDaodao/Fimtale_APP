package com.app.fimtale.ui;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
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
import androidx.recyclerview.widget.LinearSmoothScroller;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

import com.app.fimtale.model.WorkFeedResponse;
import com.app.fimtale.network.RetrofitClient;
import com.app.fimtale.utils.UserPreferences;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private NestedScrollView scrollView;
    private LinearLayout contentLayout;
    private LinearLayout emptyStateLayout;
    private LinearLayout quickAccessLayout;
    private LinearLayout btnGallery;
    private LinearLayout btnPosts;
    private LinearLayout btnTags;
    private Button btnConfigureApi;
    private TextView tvWhyHow;
    private ViewPager2 bannerViewPager;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView errorTextView;
    private Button viewMoreButton;
    private BannerAdapter bannerAdapter;
    private TopicAdapter adapter;
    private List<RecommendedTopic> bannerList = new ArrayList<>();
    private List<TopicViewItem> topicList = new ArrayList<>();
    private Timer bannerTimer;
    private Handler bannerHandler = new Handler(Looper.getMainLooper());
    private View rootView;

    private int currentPage = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;

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
        quickAccessLayout = view.findViewById(R.id.quickAccessLayout);
        btnGallery = view.findViewById(R.id.btnGallery);
        btnPosts = view.findViewById(R.id.btnPosts);
        btnTags = view.findViewById(R.id.btnTags);
        recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        errorTextView = view.findViewById(R.id.errorTextView);
        viewMoreButton = view.findViewById(R.id.viewMoreButton);
        emptyStateLayout = view.findViewById(R.id.emptyStateLayout);
        btnConfigureApi = view.findViewById(R.id.btnConfigureApi);
        tvWhyHow = view.findViewById(R.id.tvWhyHow);

        setupBannerViewPager();
        setupRecyclerView();
        setupSwipeRefresh();
        setupEmptyState();
        setupQuickAccess();

        checkCredentialsAndLoad();
    }

    private void setupQuickAccess() {
        btnGallery.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), com.app.fimtale.TagArticlesActivity.class);
            intent.putExtra(com.app.fimtale.TagArticlesActivity.EXTRA_TAG_NAME, "画廊");
            startActivity(intent);
        });

        btnPosts.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), com.app.fimtale.TagArticlesActivity.class);
            intent.putExtra(com.app.fimtale.TagArticlesActivity.EXTRA_TAG_NAME, "帖子");
            startActivity(intent);
        });

        btnTags.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), com.app.fimtale.TagListActivity.class);
            startActivity(intent);
        });
    }

    private void setupEmptyState() {
        btnConfigureApi.setVisibility(View.GONE);
        tvWhyHow.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), com.app.fimtale.HelpActivity.class);
            startActivity(intent);
        });
    }

    private void checkCredentialsAndLoad() {
        if (UserPreferences.isUserConfigured(getContext())) {
            emptyStateLayout.setVisibility(View.GONE);
            swipeRefreshLayout.setVisibility(View.VISIBLE);
            fetchHomePageData();
        } else {
            emptyStateLayout.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            errorTextView.setVisibility(View.GONE);
        }
    }

    private void setupSwipeRefresh() {
        swipeRefreshLayout.setColorSchemeResources(R.color.md_theme_light_primary);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                ValueAnimator blurAnimator = ValueAnimator.ofFloat(0f, 50f);
                blurAnimator.setDuration(300);
                blurAnimator.addUpdateListener(animation -> {
                    float val = (float) animation.getAnimatedValue();
                    if (val > 0) {
                        scrollView.setRenderEffect(android.graphics.RenderEffect.createBlurEffect(val, val, android.graphics.Shader.TileMode.CLAMP));
                    }
                });
                blurAnimator.start();
            }
            
            scrollView.animate()
                    .scaleX(0.9f)
                    .scaleY(0.9f)
                    .setDuration(300)
                    .start();

            currentPage = 1;
            isLastPage = false;
            fetchHomePageData(true);
        });
    }

    private void setupBannerViewPager() {
        bannerAdapter = new BannerAdapter(bannerList, topic -> {
            Intent intent = new Intent(getContext(), com.app.fimtale.TopicDetailActivity.class);
            intent.putExtra(com.app.fimtale.TopicDetailActivity.EXTRA_TOPIC_ID, topic.getId());
            startActivity(intent);
        });
        bannerViewPager.setAdapter(bannerAdapter);
        bannerViewPager.setClipToPadding(false);
        bannerViewPager.setClipChildren(false);
        bannerViewPager.setOffscreenPageLimit(3);
        CompositePageTransformer compositeTransformer = new CompositePageTransformer();
        compositeTransformer.addTransformer(new MarginPageTransformer(getResources().getDimensionPixelOffset(R.dimen.page_margin)));
        compositeTransformer.addTransformer((page, position) -> {
            View imageView = page.findViewById(R.id.bannerImageView);
            if (imageView != null) {
                int width = imageView.getWidth();
                imageView.setScaleX(1.4f);
                imageView.setScaleY(1.4f);
                imageView.setTranslationX(-position * width * 0.2f);
            }
        });
        bannerViewPager.setPageTransformer(compositeTransformer);
    }

    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new TopicAdapter(topicList);
        recyclerView.setAdapter(adapter);

        viewMoreButton.setVisibility(View.GONE);

        scrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (v.getChildAt(v.getChildCount() - 1) != null) {
                if ((scrollY >= (v.getChildAt(v.getChildCount() - 1).getMeasuredHeight() - v.getMeasuredHeight())) &&
                        scrollY > oldScrollY) {
                    if (!isLoading && !isLastPage) {
                        loadMoreData();
                    }
                }
            }
        });
    }

    private void loadMoreData() {
        isLoading = true;
        currentPage++;
        
        RetrofitClient.getInstance().getWorkFeed(currentPage).enqueue(new Callback<WorkFeedResponse>() {
            @Override
            public void onResponse(Call<WorkFeedResponse> call, Response<WorkFeedResponse> response) {
                if (!isAdded()) return;
                isLoading = false;
                
                if (response.isSuccessful() && response.body() != null && response.body().getCode() == 0) {
                    WorkFeedResponse data = response.body();
                    if (data.getData() != null && data.getData().getWorks() != null && !data.getData().getWorks().isEmpty()) {
                        int startPosition = topicList.size();
                        List<TopicViewItem> newItems = data.getData().getWorks().stream()
                                .map(TopicViewItem::new)
                                .collect(Collectors.toList());
                        topicList.addAll(newItems);
                        adapter.notifyItemRangeInserted(startPosition, newItems.size());
                    } else {
                        isLastPage = true;
                    }
                }
            }

            @Override
            public void onFailure(Call<WorkFeedResponse> call, Throwable t) {
                if (!isAdded()) return;
                isLoading = false;
                currentPage--;
            }
        });
    }

    private void fetchHomePageData() {
        currentPage = 1;
        isLastPage = false;
        fetchHomePageData(true);
    }

    private void fetchHomePageData(boolean animate) {
        if (!swipeRefreshLayout.isRefreshing()) {
            progressBar.setVisibility(View.VISIBLE);
            errorTextView.setVisibility(View.GONE);
            scrollView.setVisibility(View.INVISIBLE);
            contentLayout.setVisibility(View.VISIBLE);
        }

        isLoading = true;
        RetrofitClient.getInstance().getWorkFeed(currentPage).enqueue(new Callback<WorkFeedResponse>() {
            @Override
            public void onResponse(Call<WorkFeedResponse> call, Response<WorkFeedResponse> response) {
                if (!isAdded()) return;
                isLoading = false;
                
                if (response.isSuccessful() && response.body() != null && response.body().getCode() == 0) {
                    errorTextView.setVisibility(View.GONE);
                    WorkFeedResponse data = response.body();
                    
                    stopBannerAutoScroll();
                    bannerList.clear();
                    bannerAdapter.notifyDataSetChanged();
                    bannerViewPager.setVisibility(View.GONE);

                    Runnable updateDataRunnable = () -> {
                        topicList.clear();
                        
                        if (data.getData() != null && data.getData().getWorks() != null) {
                            if (data.getData().getWorks().isEmpty()) {
                                isLastPage = true;
                            } else {
                                topicList.addAll(data.getData().getWorks().stream()
                                        .map(TopicViewItem::new)
                                        .collect(Collectors.toList()));
                            }
                        } else {
                            isLastPage = true;
                        }

                        adapter.notifyDataSetChanged();
                        
                        quickAccessLayout.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.VISIBLE);
                        viewMoreButton.setVisibility(View.GONE);
                    };

                    Runnable animationRunnable = () -> {
                        progressBar.setVisibility(View.GONE);
                        progressBar.setAlpha(1f);

                        scrollView.setAlpha(0f);
                        scrollView.setScaleX(0.9f);
                        scrollView.setScaleY(0.9f);
                        scrollView.setVisibility(View.VISIBLE);

                        updateDataRunnable.run();

                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                            ValueAnimator blurAnimator = ValueAnimator.ofFloat(50f, 0f);
                            blurAnimator.setDuration(500);
                            blurAnimator.addUpdateListener(animation -> {
                                float val = (float) animation.getAnimatedValue();
                                if (val > 0.1f) {
                                    scrollView.setRenderEffect(android.graphics.RenderEffect.createBlurEffect(val, val, android.graphics.Shader.TileMode.CLAMP));
                                } else {
                                    scrollView.setRenderEffect(null);
                                }
                            });
                            blurAnimator.addListener(new android.animation.AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(android.animation.Animator animation) {
                                    scrollView.setRenderEffect(null);
                                    scrollView.invalidate();
                                }
                            });
                            blurAnimator.start();
                        }

                        android.view.animation.PathInterpolator interpolator = new android.view.animation.PathInterpolator(1.00f, 0.00f, 0.28f, 1.00f);

                        scrollView.animate()
                                .alpha(1f)
                                .scaleX(1f)
                                .scaleY(1f)
                                .setInterpolator(interpolator)
                                .setDuration(500)
                                .start();
                    };

                    if (animate) {
                        if (progressBar.getVisibility() == View.VISIBLE) {
                            progressBar.animate()
                                    .alpha(0f)
                                    .setDuration(300)
                                    .withEndAction(animationRunnable)
                                    .start();
                        } else {
                            animationRunnable.run();
                        }
                    } else {
                        updateDataRunnable.run();
                        progressBar.setVisibility(View.GONE);
                        scrollView.setVisibility(View.VISIBLE);
                        scrollView.setAlpha(1f);
                        scrollView.setScaleX(1f);
                        scrollView.setScaleY(1f);
                    }
                    
                } else {
                    showError();
                }
                
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(Call<WorkFeedResponse> call, Throwable t) {
                if (!isAdded()) return;
                isLoading = false;
                showError();
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    private void showError() {
        progressBar.setVisibility(View.GONE);
        scrollView.setVisibility(View.INVISIBLE);
        errorTextView.setVisibility(View.VISIBLE);
        errorTextView.setText("加载失败，请尝试下拉刷新");
        errorTextView.setOnClickListener(v -> fetchHomePageData(true));
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
                            int nextItem = (currentItem + 1) % totalItems;
                            try {
                                View child = bannerViewPager.getChildAt(0);
                                if (child instanceof RecyclerView) {
                                    RecyclerView rv = (RecyclerView) child;
                                    final boolean isWrapAround = (nextItem == 0);
                                    RecyclerView.SmoothScroller smoothScroller = new LinearSmoothScroller(getContext()) {
                                        @Override
                                        protected int getHorizontalSnapPreference() {
                                            return SNAP_TO_START;
                                        }

                                        @Override
                                        protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                                            return isWrapAround ? 0.1f : 0.25f; 
                                        }
                                    };
                                    smoothScroller.setTargetPosition(nextItem);
                                    rv.getLayoutManager().startSmoothScroll(smoothScroller);
                                } else {
                                    bannerViewPager.setCurrentItem(nextItem, true);
                                }
                            } catch (Exception e) {
                                bannerViewPager.setCurrentItem(nextItem, true);
                            }
                        }
                    }
                });
            }
        }, 8000, 8000);
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
        if (emptyStateLayout != null && emptyStateLayout.getVisibility() == View.VISIBLE 
                && UserPreferences.isUserConfigured(getContext())) {
            checkCredentialsAndLoad();
        }
        if (bannerAdapter != null && bannerAdapter.getItemCount() > 0) {
            startBannerAutoScroll();
        }
    }
}
