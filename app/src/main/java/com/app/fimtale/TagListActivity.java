package com.app.fimtale;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.app.fimtale.adapter.TagAdapter;
import com.app.fimtale.model.TagInfo;
import com.app.fimtale.model.TagListResponse;
import com.app.fimtale.network.FimTaleApiService;
import com.app.fimtale.network.RetrofitClient;
import com.app.fimtale.utils.UserPreferences;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TagListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TagAdapter adapter;
    private List<TagInfo> tagList = new ArrayList<>();
    private int currentPage = 1;
    private int totalPages = 1;
    private boolean isLoading = false;
    private MaterialCardView toolbarContainer;
    private boolean isToolbarElevated = false;
    private ObjectAnimator elevationAnimator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_list);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbarContainer = findViewById(R.id.toolbarContainer);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TagAdapter(tagList, this);
        recyclerView.setAdapter(adapter);

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

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (!isLoading && layoutManager != null && layoutManager.findLastCompletelyVisibleItemPosition() == tagList.size() - 1) {
                    if (currentPage < totalPages) {
                        currentPage++;
                        loadTags();
                    }
                }
            }
        });

        loadTags();
    }

    private void loadTags() {
        isLoading = true;
        FimTaleApiService apiService = RetrofitClient.getInstance();
        String apiKey = UserPreferences.getApiKey(this);
        String apiPass = UserPreferences.getApiPass(this);

        Call<TagListResponse> call = apiService.getTags(apiKey, apiPass, currentPage);
        call.enqueue(new Callback<TagListResponse>() {
            @Override
            public void onResponse(Call<TagListResponse> call, Response<TagListResponse> response) {
                isLoading = false;
                if (response.isSuccessful() && response.body() != null) {
                    TagListResponse data = response.body();
                    totalPages = data.getTotalPage();
                    if (data.getTagArray() != null) {
                        tagList.addAll(data.getTagArray());
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    Toast.makeText(TagListActivity.this, "加载标签失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TagListResponse> call, Throwable t) {
                isLoading = false;
                Toast.makeText(TagListActivity.this, "网络错误: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
