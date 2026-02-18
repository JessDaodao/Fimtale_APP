package com.app.fimtale;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.app.fimtale.adapter.TopicAdapter;
import com.app.fimtale.model.FavoritesResponse;
import com.app.fimtale.model.Topic;
import com.app.fimtale.model.TopicViewItem;
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
    private boolean isToolbarElevated = false;
    private ObjectAnimator elevationAnimator;
    private List<TopicViewItem> topics = new ArrayList<>();
    private int currentPage = 1;
    private int totalPages = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        toolbarContainer = findViewById(R.id.toolbarContainer);

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
            }
        });

        adapter = new TopicAdapter(topics);
        adapter.setPaginationEnabled(true);
        adapter.setPaginationListener(new TopicAdapter.OnPaginationListener() {
            @Override
            public void onPrevPage() {
                if (currentPage > 1) {
                    loadFavorites(currentPage - 1);
                }
            }

            @Override
            public void onNextPage() {
                if (currentPage < totalPages) {
                    loadFavorites(currentPage + 1);
                }
            }
        });
        recyclerView.setAdapter(adapter);

        swipeRefresh.setOnRefreshListener(() -> loadFavorites(1));

        loadFavorites(1);
    }

    private void loadFavorites(int page) {
        swipeRefresh.setRefreshing(true);
        String apiKey = UserPreferences.getApiKey(this);
        String apiPass = UserPreferences.getApiPass(this);

        RetrofitClient.getInstance().getFavorites(apiKey, apiPass, page).enqueue(new Callback<FavoritesResponse>() {
            @Override
            public void onResponse(Call<FavoritesResponse> call, Response<FavoritesResponse> response) {
                swipeRefresh.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null) {
                    FavoritesResponse data = response.body();
                    if (data.getStatus() == 1) {
                        currentPage = data.getPage();
                        totalPages = data.getTotalPage();
                        
                        topics.clear();
                        if (data.getTopicArray() != null) {
                            for (Topic topic : data.getTopicArray()) {
                                topics.add(new TopicViewItem(topic));
                            }
                        }
                        
                        adapter.setPageInfo(currentPage, totalPages);
                        adapter.notifyDataSetChanged();
                        
                        if (page != 1) {
                            RecyclerView recyclerView = findViewById(R.id.recyclerView);
                            recyclerView.scrollToPosition(0);
                        }
                    } else {
                        Toast.makeText(FavoritesActivity.this, "加载失败: 状态错误", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(FavoritesActivity.this, "加载失败: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<FavoritesResponse> call, Throwable t) {
                swipeRefresh.setRefreshing(false);
                Toast.makeText(FavoritesActivity.this, "网络错误: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
