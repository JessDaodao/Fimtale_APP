package com.app.fimtale;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.app.fimtale.adapter.TopicAdapter;
import com.app.fimtale.model.ApiResponse;
import com.app.fimtale.model.ListResponse;
import com.app.fimtale.model.TopicViewItem;
import com.app.fimtale.model.WorkDetailResponse;
import com.app.fimtale.network.RetrofitClient;
import com.app.fimtale.utils.UserPreferences;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoritesActivity extends AppCompatActivity {

    private TopicAdapter adapter;
    private SwipeRefreshLayout swipeRefresh;
    private MaterialCardView toolbarContainer;
    private android.view.View loadingOverlay;
    private boolean isToolbarElevated = false;
    private ObjectAnimator elevationAnimator;
    private List<TopicViewItem> topics = new ArrayList<>();
    private int currentPage = 1;
    private int totalPages = 1;
    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        toolbarContainer = findViewById(R.id.toolbarContainer);
        loadingOverlay = findViewById(R.id.loadingOverlay);

        swipeRefresh = findViewById(R.id.swipeRefresh);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        float targetElevation = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                boolean shouldElevate = recyclerView.canScrollVertically(-1);

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

                if (dy > 0) {
                    LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    if (layoutManager != null) {
                        int visibleItemCount = layoutManager.getChildCount();
                        int totalItemCount = layoutManager.getItemCount();
                        int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                        if (!isLoading && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                                && firstVisibleItemPosition >= 0
                                && currentPage < totalPages) {
                            loadFavorites(currentPage + 1);
                        }
                    }
                }
            }
        });

        adapter = new TopicAdapter(topics);
        recyclerView.setAdapter(adapter);

        swipeRefresh.setEnabled(false);

        loadFavorites(1);
    }

    private void loadFavorites(int page) {
        if (isLoading) return;
        isLoading = true;
        swipeRefresh.setRefreshing(true);
        RetrofitClient.getInstance().getFavoriteWorks(null, page, 20).enqueue(new Callback<ApiResponse<ListResponse<WorkDetailResponse.Work>>>() {
            @Override
            public void onResponse(Call<ApiResponse<ListResponse<WorkDetailResponse.Work>>> call, Response<ApiResponse<ListResponse<WorkDetailResponse.Work>>> response) {
                isLoading = false;
                swipeRefresh.setRefreshing(false);
                hideLoadingOverlay();
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    ListResponse<WorkDetailResponse.Work> data = response.body().getData();
                    if (data != null) {
                        currentPage = page;
                        List<WorkDetailResponse.Work> items = data.getList();
                        if (items != null) {
                            totalPages = (items.size() < 20) ? page : page + 1;

                            if (page == 1) {
                                topics.clear();
                            }

                            int startInsertPos = topics.size();
                            List<TopicViewItem> newItems = new ArrayList<>();
                            for (WorkDetailResponse.Work work : items) {
                                newItems.add(new TopicViewItem(work));
                            }
                            topics.addAll(newItems);

                            if (page == 1) {
                                adapter.notifyDataSetChanged();
                            } else {
                                adapter.notifyItemRangeInserted(startInsertPos, newItems.size());
                            }

                            if (page == 1) {
                                RecyclerView recyclerView = findViewById(R.id.recyclerView);
                                recyclerView.scrollToPosition(0);
                            }
                        }
                    }
                } else {
                    Toast.makeText(FavoritesActivity.this,
                            "加载失败: " + (response.body() != null ? response.body().getMsg() : response.message()),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ListResponse<WorkDetailResponse.Work>>> call, Throwable t) {
                isLoading = false;
                swipeRefresh.setRefreshing(false);
                hideLoadingOverlay();
                Toast.makeText(FavoritesActivity.this, "网络错误: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void hideLoadingOverlay() {
        if (loadingOverlay != null && loadingOverlay.getVisibility() == android.view.View.VISIBLE) {
            loadingOverlay.animate()
                    .alpha(0f)
                    .setDuration(300)
                    .withEndAction(() -> loadingOverlay.setVisibility(android.view.View.GONE))
                    .start();
        }
    }
}
