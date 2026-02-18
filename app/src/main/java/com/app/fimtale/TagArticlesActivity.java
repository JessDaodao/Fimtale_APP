package com.app.fimtale;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.fimtale.adapter.TopicAdapter;
import com.app.fimtale.model.TagDetailResponse;
import com.app.fimtale.model.Topic;
import com.app.fimtale.model.TopicViewItem;
import com.app.fimtale.network.RetrofitClient;
import com.app.fimtale.utils.UserPreferences;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TagArticlesActivity extends AppCompatActivity {

    public static final String EXTRA_TAG_NAME = "tag_name";

    private MaterialToolbar toolbar;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TopicAdapter topicAdapter;
    private List<TopicViewItem> topicViewItemList = new ArrayList<>();
    
    private String tagName;
    private int currentPage = 1;
    private int totalPages = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_articles);

        tagName = getIntent().getStringExtra(EXTRA_TAG_NAME);
        if (tagName == null) {
            finish();
            return;
        }

        setupViews();
        fetchTagTopics();
    }

    private void setupViews() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("# " + tagName);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        topicAdapter = new TopicAdapter(topicViewItemList);
        recyclerView.setAdapter(topicAdapter);

        topicAdapter.setPaginationListener(new TopicAdapter.OnPaginationListener() {
            @Override
            public void onPrevPage() {
                if (currentPage > 1) {
                    currentPage--;
                    fetchTagTopics();
                }
            }

            @Override
            public void onNextPage() {
                if (currentPage < totalPages) {
                    currentPage++;
                    fetchTagTopics();
                }
            }
        });
    }

    private void fetchTagTopics() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);

        String apiKey = UserPreferences.getApiKey(this);
        String apiPass = UserPreferences.getApiPass(this);

        RetrofitClient.getInstance().getTagTopics(tagName, apiKey, apiPass, currentPage).enqueue(new Callback<TagDetailResponse>() {
            @Override
            public void onResponse(@NonNull Call<TagDetailResponse> call, @NonNull Response<TagDetailResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && response.body().getStatus() == 1) {
                    TagDetailResponse data = response.body();
                    
                    currentPage = data.getPage();
                    totalPages = data.getTotalPage();
                    
                    List<Topic> topicList = data.getTopicArray();
                    topicViewItemList.clear();
                    if (topicList != null) {
                        topicViewItemList.addAll(topicList.stream().map(TopicViewItem::new).collect(Collectors.toList()));
                    }
                    
                    topicAdapter.notifyDataSetChanged();
                    topicAdapter.setPageInfo(currentPage, totalPages);
                    recyclerView.setVisibility(View.VISIBLE);
                    
                    if (data.getTagInfo() != null && getSupportActionBar() != null) {
                        getSupportActionBar().setTitle("# " + data.getTagInfo().getName());
                    }
                } else {
                    Toast.makeText(TagArticlesActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<TagDetailResponse> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(TagArticlesActivity.this, "网络错误: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                recyclerView.setVisibility(View.VISIBLE);
            }
        });
    }
}
