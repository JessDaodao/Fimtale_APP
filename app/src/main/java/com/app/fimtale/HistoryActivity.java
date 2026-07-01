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

import com.app.fimtale.adapter.HistoryAdapter;
import com.app.fimtale.model.ApiResponse;
import com.app.fimtale.model.ListResponse;
import com.app.fimtale.model.ReadProgress;
import com.app.fimtale.network.RetrofitClient;
import com.app.fimtale.utils.UserPreferences;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryActivity extends AppCompatActivity {

    private HistoryAdapter adapter;
    private SwipeRefreshLayout swipeRefresh;
    private MaterialCardView toolbarContainer;
    private android.view.View loadingOverlay;
    private boolean isToolbarElevated = false;
    private ObjectAnimator elevationAnimator;
    private int currentPage = 1;
    private int totalPages = 1;
    private boolean isLoading = false;
    private List<ReadProgress> progressList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

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
                            loadHistory(currentPage + 1);
                        }
                    }
                }
            }
        });

        adapter = new HistoryAdapter();
        adapter.setOnItemClickListener(progress -> {
            Intent intent = new Intent(HistoryActivity.this, ReaderActivity.class);
            if (progress.getChapterId() != null) {
                intent.putExtra(ReaderActivity.EXTRA_TOPIC_ID, progress.getChapterId());
            } else {
                intent.putExtra(ReaderActivity.EXTRA_TOPIC_ID, progress.getWorkId());
            }
            intent.putExtra(ReaderActivity.EXTRA_INITIAL_PROGRESS, progress.getProgress());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        swipeRefresh.setEnabled(false);

        loadHistory(1);
    }

    private void loadHistory(int page) {
        if (isLoading) return;
        isLoading = true;
        swipeRefresh.setRefreshing(true);
        RetrofitClient.getInstance().listReadProgress(0, 0, page, 20).enqueue(new Callback<ApiResponse<ListResponse<ReadProgress>>>() {
            @Override
            public void onResponse(Call<ApiResponse<ListResponse<ReadProgress>>> call, Response<ApiResponse<ListResponse<ReadProgress>>> response) {
                isLoading = false;
                swipeRefresh.setRefreshing(false);
                hideLoadingOverlay();
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    ListResponse<ReadProgress> data = response.body().getData();
                    if (data != null) {
                        currentPage = page;
                        // 简单分页：一次返回 20 条，不足表示最后一页
                        List<ReadProgress> items = data.getList();
                        if (items != null) {
                            totalPages = (items.size() < 20) ? page : page + 1;

                            if (page == 1) {
                                progressList.clear();
                                adapter.setReadProgress(items);
                            } else {
                                adapter.addReadProgress(items);
                            }

                            RecyclerView recyclerView = findViewById(R.id.recyclerView);
                            if (page == 1) {
                                recyclerView.scrollToPosition(0);
                            }
                        }
                    }
                } else {
                    Toast.makeText(HistoryActivity.this,
                            "加载失败: " + (response.body() != null ? response.body().getMsg() : response.message()),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ListResponse<ReadProgress>>> call, Throwable t) {
                isLoading = false;
                swipeRefresh.setRefreshing(false);
                hideLoadingOverlay();
                Toast.makeText(HistoryActivity.this, "网络错误: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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
