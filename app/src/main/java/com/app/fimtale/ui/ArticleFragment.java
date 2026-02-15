package com.app.fimtale.ui;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.fimtale.R;
import com.app.fimtale.adapter.TopicAdapter;
import com.app.fimtale.model.Topic;
import com.app.fimtale.model.TopicListResponse;
import com.app.fimtale.model.TopicViewItem;
import com.app.fimtale.network.RetrofitClient;
import com.app.fimtale.utils.UserPreferences;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArticleFragment extends Fragment {

    private TabLayout tabLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FrameLayout contentContainer;
    private ProgressBar progressBar;
    
    private RecyclerView recyclerView;
    private TopicAdapter adapter;
    private List<TopicViewItem> dataList = new ArrayList<>();
    private int currentPage = 1;
    private int totalPages = 1;
    private boolean isLoading = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_article, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tabLayout = view.findViewById(R.id.tabs);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        contentContainer = view.findViewById(R.id.content_container);
        progressBar = view.findViewById(R.id.progressBar);

        tabLayout.addTab(tabLayout.newTab().setText("全部"));
        tabLayout.setVisibility(View.GONE);

        setupRecyclerView();
        setupSwipeRefresh();

        loadTopics(false);
    }

    private void setupRecyclerView() {
        recyclerView = new RecyclerView(getContext());
        recyclerView.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 
                ViewGroup.LayoutParams.MATCH_PARENT));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        int padding = (int)(8 * getResources().getDisplayMetrics().density);
        recyclerView.setPadding(padding, 0, padding, 0);
        recyclerView.setClipToPadding(false);
        recyclerView.setVisibility(View.GONE);
        
        adapter = new TopicAdapter(dataList);
        adapter.setPaginationEnabled(true);
        adapter.setPaginationListener(new TopicAdapter.OnPaginationListener() {
            @Override
            public void onPrevPage() {
                if (currentPage > 1 && !isLoading) {
                    currentPage--;
                    loadTopics(false);
                }
            }

            @Override
            public void onNextPage() {
                if (currentPage < totalPages && !isLoading) {
                    currentPage++;
                    loadTopics(false);
                }
            }
        });
        recyclerView.setAdapter(adapter);
        contentContainer.addView(recyclerView);
    }

    private void setupSwipeRefresh() {
        swipeRefreshLayout.setColorSchemeResources(R.color.md_theme_light_primary);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            currentPage = 1;
            loadTopics(true);
        });
    }

    private void loadTopics(boolean isRefresh) {
        if (isLoading) return;
        isLoading = true;

        if (!isRefresh && !swipeRefreshLayout.isRefreshing()) {
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.INVISIBLE);
        }

        String apiKey = UserPreferences.getApiKey(getContext());
        String apiPass = UserPreferences.getApiPass(getContext());

        RetrofitClient.getInstance().getTopicList(apiKey, apiPass, currentPage).enqueue(new Callback<TopicListResponse>() {
            @Override
            public void onResponse(Call<TopicListResponse> call, Response<TopicListResponse> response) {
                if (!isAdded()) return;
                isLoading = false;
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null) {
                    TopicListResponse data = response.body();
                    currentPage = data.getPage();
                    totalPages = data.getTotalPage();
                    
                    if (isRefresh || currentPage == 1) {
                         dataList.clear();
                    }
                    
                    List<Topic> topics = data.getTopicArray();
                    if (topics != null) {
                        dataList.clear();
                        dataList.addAll(topics.stream().map(TopicViewItem::new).collect(Collectors.toList()));
                    }

                    adapter.notifyDataSetChanged();
                    adapter.setPageInfo(currentPage, totalPages);
                    
                    recyclerView.setVisibility(View.VISIBLE);
                    
                    recyclerView.setAlpha(0f);
                    recyclerView.animate().alpha(1f).setDuration(300).start();

                } else {
                    Toast.makeText(getContext(), "加载失败: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TopicListResponse> call, Throwable t) {
                if (!isAdded()) return;
                isLoading = false;
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getContext(), "网络错误: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
