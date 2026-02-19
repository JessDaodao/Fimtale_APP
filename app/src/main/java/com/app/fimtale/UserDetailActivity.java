package com.app.fimtale;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.text.TextUtils;
import android.graphics.drawable.Drawable;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.TypedValue;
import android.content.res.ColorStateList;

import com.app.fimtale.adapter.TopicAdapter;
import com.app.fimtale.model.Topic;
import com.app.fimtale.model.TopicListResponse;
import com.app.fimtale.model.TopicViewItem;
import com.app.fimtale.model.UserDetailResponse;
import com.app.fimtale.network.RetrofitClient;
import com.app.fimtale.utils.UserPreferences;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Map;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserDetailActivity extends AppCompatActivity {

    public static final String EXTRA_USERNAME = "extra_username";

    private MaterialCardView imageContainer;
    private ImageView ivBackground;
    private ImageView ivAvatar;
    private TextView tvUsername;
    private TextView tvUserRole;
    private TextView tvLastSeen;
    private TextView tvIntro;
    private TextView tvFollowing;
    private TextView tvFollowers;
    private TextView tvTopics;
    private ChipGroup chipGroupBadges;
    private TextView tvMedalsTitle;
    private ChipGroup chipGroupMedals;
    private View loadingMask;
    private CollapsingToolbarLayout collapsingToolbar;
    private MaterialCardView toolbarContainer;
    private Toolbar toolbar;
    private TextView tvToolbarTitle;
    private NestedScrollView scrollView;
    private boolean isToolbarElevated = false;
    private boolean isTitleVisible = false;
    private ObjectAnimator elevationAnimator;

    private RecyclerView rvUserTopics;
    private TopicAdapter topicAdapter;
    private java.util.List<TopicViewItem> topicList = new java.util.ArrayList<>();
    private TextView tvUserTopicsTitle;
    private int currentPage = 1;
    private String currentUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);

        String username = getIntent().getStringExtra(EXTRA_USERNAME);
        if (TextUtils.isEmpty(username)) {
            finish();
            return;
        }
        this.currentUsername = username;

        loadData(username);
        loadUserTopics(username, 1);

        initView();
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        tvToolbarTitle = findViewById(R.id.tvToolbarTitle);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }

        collapsingToolbar = findViewById(R.id.collapsingToolbar);
        toolbarContainer = findViewById(R.id.toolbarContainer);
        scrollView = findViewById(R.id.scrollView);
        imageContainer = findViewById(R.id.imageContainer);
        ivBackground = findViewById(R.id.ivBackground);
        ivAvatar = findViewById(R.id.ivAvatar);
        tvUsername = findViewById(R.id.tvUsername);
        tvUserRole = findViewById(R.id.tvUserRole);
        tvLastSeen = findViewById(R.id.tvLastSeen);
        tvIntro = findViewById(R.id.tvIntro);
        tvFollowing = findViewById(R.id.tvFollowing);
        tvFollowers = findViewById(R.id.tvFollowers);
        tvTopics = findViewById(R.id.tvTopics);
        chipGroupBadges = findViewById(R.id.chipGroupBadges);
        tvMedalsTitle = findViewById(R.id.tvMedalsTitle);
        chipGroupMedals = findViewById(R.id.chipGroupMedals);
        loadingMask = findViewById(R.id.loadingMask);
        rvUserTopics = findViewById(R.id.rvUserTopics);
        tvUserTopicsTitle = findViewById(R.id.tvUserTopicsTitle);

        rvUserTopics.setLayoutManager(new LinearLayoutManager(this));
        topicAdapter = new TopicAdapter(topicList);
        topicAdapter.setPaginationEnabled(true);
        topicAdapter.setPaginationListener(new TopicAdapter.OnPaginationListener() {
            @Override
            public void onPrevPage() {
                if (currentPage > 1) {
                    loadUserTopics(currentUsername, currentPage - 1);
                }
            }

            @Override
            public void onNextPage() {
                loadUserTopics(currentUsername, currentPage + 1);
            }
        });
        rvUserTopics.setAdapter(topicAdapter);

        float targetElevation = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
        float titleThreshold = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 240, getResources().getDisplayMetrics());
        
        scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                boolean shouldElevate = v.canScrollVertically(-1);

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

                if (scrollY > titleThreshold) {
                    if (!isTitleVisible) {
                        isTitleVisible = true;
                        tvToolbarTitle.setText(currentUsername);
                        tvToolbarTitle.animate().alpha(1.0f).setDuration(200).start();
                    }
                } else {
                    if (isTitleVisible) {
                        isTitleVisible = false;
                        tvToolbarTitle.animate().alpha(0.0f).setDuration(200).start();
                    }
                }
            }
        });
    }

    private void loadData(String username) {
        if (loadingMask != null) loadingMask.setVisibility(View.VISIBLE);
        String apiKey = UserPreferences.getApiKey(this);
        String apiPass = UserPreferences.getApiPass(this);

        RetrofitClient.getInstance().getUserDetail(username, apiKey, apiPass).enqueue(new Callback<UserDetailResponse>() {
            @Override
            public void onResponse(Call<UserDetailResponse> call, Response<UserDetailResponse> response) {
                hideLoadingMask();
                if (response.isSuccessful() && response.body() != null) {
                    UserDetailResponse data = response.body();
                    if (data.getStatus() == 1 && data.getUserInfo() != null) {
                        bindData(data);
                    } else {
                        Toast.makeText(UserDetailActivity.this, "获取用户信息失败", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(UserDetailActivity.this, "网络请求失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserDetailResponse> call, Throwable t) {
                hideLoadingMask();
                Toast.makeText(UserDetailActivity.this, "网络错误: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void hideLoadingMask() {
        if (loadingMask != null && loadingMask.getVisibility() == View.VISIBLE) {
            loadingMask.animate()
                    .alpha(0f)
                    .setDuration(400)
                    .withEndAction(() -> loadingMask.setVisibility(View.GONE))
                    .start();
        }
    }

    private void bindData(UserDetailResponse data) {
        UserDetailResponse.UserInfo info = data.getUserInfo();

        tvUsername.setText(info.getUserName());
        tvUserRole.setText("LV." + info.getGradeInfo().getGrade());

        if (!TextUtils.isEmpty(info.getLastSeen())) {
            try {
                long timestamp = Long.parseLong(info.getLastSeen()) * 1000L;
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                tvLastSeen.setText("最后活动: " + sdf.format(new Date(timestamp)));
            } catch (Exception e) {
                tvLastSeen.setVisibility(View.GONE);
            }
        } else {
             tvLastSeen.setVisibility(View.GONE);
        }

        tvIntro.setText(info.getUserIntro());
        tvFollowing.setText(String.valueOf(info.getFollowing()));
        tvFollowers.setText(String.valueOf(info.getFollowers()));
        tvTopics.setText(String.valueOf(info.getTopics()));

        if (TextUtils.isEmpty(info.getBackground())) {
            imageContainer.setVisibility(View.GONE);
        } else {
            imageContainer.setVisibility(View.VISIBLE);
            Glide.with(this)
                 .load(info.getBackground())
                 .listener(new RequestListener<Drawable>() {
                     @Override
                     public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                         imageContainer.setVisibility(View.GONE);
                         return false;
                     }

                     @Override
                     public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                         return false;
                     }
                 })
                 .into(ivBackground);
        }

        String avatarUrl = "https://fimtale.com/upload/avatar/large/" + info.getId() + ".png";
        Glide.with(this)
             .load(avatarUrl)
             .placeholder(R.drawable.ic_person)
             .into(ivAvatar);

        chipGroupBadges.removeAllViews();
        if (info.getBadges() != null) {
            int bgColor = resolveThemeColor(com.google.android.material.R.attr.colorPrimaryContainer);
            int textColor = resolveThemeColor(com.google.android.material.R.attr.colorOnPrimaryContainer);
            for (String badge : info.getBadges()) {
                addChip(chipGroupBadges, badge, bgColor, textColor);
            }
        }

        chipGroupMedals.removeAllViews();
        if (info.getMedals() != null && !info.getMedals().isEmpty()) {
            tvMedalsTitle.setVisibility(View.VISIBLE);
            int bgColor = resolveThemeColor(com.google.android.material.R.attr.colorSecondaryContainer);
            int textColor = resolveThemeColor(com.google.android.material.R.attr.colorOnSecondaryContainer);
            for (String medal : info.getMedals()) {
                addChip(chipGroupMedals, medal, bgColor, textColor);
            }
        } else {
            tvMedalsTitle.setVisibility(View.GONE);
        }
    }

    private void addChip(ChipGroup group, String text, int bgColor, int textColor) {
        Chip chip = new Chip(this);
        chip.setText(text);
        chip.setChipBackgroundColor(ColorStateList.valueOf(bgColor));
        chip.setTextColor(textColor);
        chip.setEnsureMinTouchTargetSize(false);
        chip.setElevation(0);
        chip.setChipMinHeight(0);
        float padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
        chip.setChipStartPadding(padding);
        chip.setChipEndPadding(padding);
        chip.setTextStartPadding(padding);
        chip.setTextEndPadding(padding);
        chip.setCloseIconVisible(false);
        chip.setChipStrokeWidth(0);
        group.addView(chip);
    }

    private void loadUserTopics(String username, int page) {
        String apiKey = UserPreferences.getApiKey(this);
        String apiPass = UserPreferences.getApiPass(this);

        RetrofitClient.getInstance().getUserTopics(username, apiKey, apiPass, page).enqueue(new Callback<TopicListResponse>() {
            @Override
            public void onResponse(Call<TopicListResponse> call, Response<TopicListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    TopicListResponse data = response.body();
                    if (data.getStatus() == 1 && data.getTopicArray() != null) {
                        topicList.clear();
                        for (Topic topic : data.getTopicArray()) {
                            topicList.add(new TopicViewItem(topic));
                        }
                        currentPage = data.getPage();
                        topicAdapter.setPageInfo(currentPage, data.getTotalPage());
                        topicAdapter.notifyDataSetChanged();
                        
                        if (topicList.isEmpty() && currentPage == 1) {
                            tvUserTopicsTitle.setVisibility(View.GONE);
                            rvUserTopics.setVisibility(View.GONE);
                        } else {
                            tvUserTopicsTitle.setVisibility(View.VISIBLE);
                            rvUserTopics.setVisibility(View.VISIBLE);
                            if (page > 1 || currentPage > 1) {
                                rvUserTopics.post(() -> rvUserTopics.scrollToPosition(0));
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<TopicListResponse> call, Throwable t) {
                // 不做处理
            }
        });
    }

    private int resolveThemeColor(int attrRes) {
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(attrRes, typedValue, true);
        return typedValue.data;
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
