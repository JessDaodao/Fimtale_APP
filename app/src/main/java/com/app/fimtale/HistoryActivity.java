package com.app.fimtale;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.app.fimtale.adapter.HistoryAdapter;
import com.app.fimtale.model.HistoryResponse;
import com.app.fimtale.network.RetrofitClient;
import com.app.fimtale.utils.UserPreferences;
import com.google.android.material.appbar.MaterialToolbar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryActivity extends AppCompatActivity {

    private HistoryAdapter adapter;
    private SwipeRefreshLayout swipeRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        swipeRefresh = findViewById(R.id.swipeRefresh);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new HistoryAdapter();
        adapter.setOnItemClickListener(topic -> {
            Intent intent = new Intent(this, TopicDetailActivity.class);
            intent.putExtra("topic_id", topic.getMainId());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        swipeRefresh.setOnRefreshListener(this::loadHistory);

        loadHistory();
    }

    private void loadHistory() {
        swipeRefresh.setRefreshing(true);
        String apiKey = UserPreferences.getApiKey(this);
        String apiPass = UserPreferences.getApiPass(this);

        RetrofitClient.getInstance().getHistory(apiKey, apiPass).enqueue(new Callback<HistoryResponse>() {
            @Override
            public void onResponse(Call<HistoryResponse> call, Response<HistoryResponse> response) {
                swipeRefresh.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().getStatus() == 1) {
                        adapter.setHistoryTopics(response.body().getHistoryTopics());
                    } else {
                        Toast.makeText(HistoryActivity.this, "加载失败: 状态错误", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(HistoryActivity.this, "加载失败: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<HistoryResponse> call, Throwable t) {
                swipeRefresh.setRefreshing(false);
                Toast.makeText(HistoryActivity.this, "网络错误: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
