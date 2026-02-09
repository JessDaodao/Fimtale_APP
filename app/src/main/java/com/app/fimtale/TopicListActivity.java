package com.app.fimtale;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.fimtale.adapter.TopicAdapter;
import com.app.fimtale.model.Topic;
import com.app.fimtale.model.TopicListResponse;
import com.app.fimtale.model.TopicViewItem;
import com.app.fimtale.network.RetrofitClient;
import com.app.fimtale.utils.UserPreferences; // 导入工具类
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TopicListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TopicAdapter topicAdapter;
    private List<TopicViewItem> topicList = new ArrayList<>();

    private int currentPage = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_list);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        recyclerView = findViewById(R.id.topic_list_recycler_view);
        progressBar = findViewById(R.id.topic_list_progress_bar);

        setupRecyclerView();
        setupScrollListener();
        fetchTopicListData(currentPage);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        topicAdapter = new TopicAdapter(topicList);
        recyclerView.setAdapter(topicAdapter);
    }

    private void setupScrollListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null && dy > 0) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                    if (!isLoading && !isLastPage) {
                        if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0) {
                            currentPage++;
                            fetchTopicListData(currentPage);
                        }
                    }
                }
            }
        });
    }

    private void fetchTopicListData(int page) {
        isLoading = true;
        progressBar.setVisibility(View.VISIBLE);

        // 修改：从 UserPreferences 读取 Key
        String apiKey = UserPreferences.getApiKey(this);
        String apiPass = UserPreferences.getApiPass(this);

        Call<TopicListResponse> call = RetrofitClient.getInstance().getTopicList(apiKey, apiPass, page);
        call.enqueue(new Callback<TopicListResponse>() {
            @Override
            public void onResponse(@NonNull Call<TopicListResponse> call, @NonNull Response<TopicListResponse> response) {
                progressBar.setVisibility(View.GONE);
                isLoading = false;
                if (response.isSuccessful() && response.body() != null && response.body().getStatus() == 1) {
                    TopicListResponse data = response.body();

                    if (data.getPage() >= data.getTotalPage()) {
                        isLastPage = true;
                    }

                    List<Topic> newTopics = data.getTopicArray();
                    if (newTopics != null && !newTopics.isEmpty()) {
                        List<TopicViewItem> newItems = new ArrayList<>();
                        for (Topic topic : newTopics) {
                            newItems.add(new TopicViewItem(topic));
                        }
                        int startPosition = topicList.size();
                        topicList.addAll(newItems);
                        topicAdapter.notifyItemRangeInserted(startPosition, newItems.size());
                    } else if (page > 1) {
                        isLastPage = true;
                        Toast.makeText(TopicListActivity.this, "没有更多内容了", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<TopicListResponse> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                isLoading = false;
                Toast.makeText(TopicListActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
            }
        });
    }
}