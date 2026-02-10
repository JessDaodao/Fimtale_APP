package com.app.fimtale.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.app.fimtale.R;
import com.app.fimtale.TopicListActivity;
import com.app.fimtale.adapter.BannerAdapter;
import com.app.fimtale.adapter.TopicAdapter;
import com.app.fimtale.model.MainPageResponse;
import com.app.fimtale.model.RecommendedTopic;
import com.app.fimtale.model.Topic;
import com.app.fimtale.model.TopicViewItem;
import com.app.fimtale.network.RetrofitClient;
import com.app.fimtale.utils.UserPreferences;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private ViewPager2 bannerViewPager;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView errorTextView, listTitleTextView;
    private Button viewMoreButton;
    private BannerAdapter bannerAdapter;
    private TopicAdapter topicAdapter;
    private List<RecommendedTopic> bannerList = new ArrayList<>();
    private List<TopicViewItem> topicList = new ArrayList<>();
    private Timer bannerTimer;
    private Handler bannerHandler = new Handler(Looper.getMainLooper());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bannerViewPager = view.findViewById(R.id.bannerViewPager);
        recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        errorTextView = view.findViewById(R.id.errorTextView);
        listTitleTextView = view.findViewById(R.id.listTitleTextView);
        viewMoreButton = view.findViewById(R.id.viewMoreButton);

        setupBannerViewPager();
        setupRecyclerView();

        viewMoreButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), TopicListActivity.class);
            startActivity(intent);
        });

        fetchHomePageData();
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

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        topicAdapter = new TopicAdapter(topicList);
        recyclerView.setAdapter(topicAdapter);
    }

    private void fetchHomePageData() {
        progressBar.setVisibility(View.VISIBLE);
        errorTextView.setVisibility(View.GONE);

        // 从 UserPreferences 读取 Key
        String apiKey = UserPreferences.getApiKey(getContext());
        String apiPass = UserPreferences.getApiPass(getContext());

        Call<MainPageResponse> call = RetrofitClient.getInstance().getHomePage(apiKey, apiPass);
        call.enqueue(new Callback<MainPageResponse>() {
            @Override
            public void onResponse(@NonNull Call<MainPageResponse> call, @NonNull Response<MainPageResponse> response) {
                if (!isAdded()) return;
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && response.body().getStatus() == 1) {
                    processHomePageData(response.body());
                } else {
                    errorTextView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<MainPageResponse> call, @NonNull Throwable t) {
                if (!isAdded()) return;
                progressBar.setVisibility(View.GONE);
                errorTextView.setVisibility(View.VISIBLE);
            }
        });
    }

    private void processHomePageData(MainPageResponse data) {
        if (data.getEditorRecommendTopicArray() != null && !data.getEditorRecommendTopicArray().isEmpty()) {
            bannerList.clear();
            bannerList.addAll(data.getEditorRecommendTopicArray());
            bannerAdapter.notifyDataSetChanged();
            bannerViewPager.setVisibility(View.VISIBLE);
            startBannerAutoScroll();
        }

        if (data.getNewlyPostTopicArray() != null && !data.getNewlyPostTopicArray().isEmpty()) {
            topicList.clear();
            for (Topic topic : data.getNewlyPostTopicArray()) {
                topicList.add(new TopicViewItem(topic));
            }
            topicAdapter.notifyDataSetChanged();
            recyclerView.setVisibility(View.VISIBLE);
            listTitleTextView.setVisibility(View.VISIBLE);
            viewMoreButton.setVisibility(View.VISIBLE);
        }
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
