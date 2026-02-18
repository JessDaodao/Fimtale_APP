package com.app.fimtale;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import android.util.TypedValue;
import android.content.res.ColorStateList;

import com.app.fimtale.model.UserDetailResponse;
import com.app.fimtale.network.RetrofitClient;
import com.app.fimtale.utils.UserPreferences;
import com.bumptech.glide.Glide;
import com.google.android.material.appbar.CollapsingToolbarLayout;
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
    private ProgressBar progressBar;
    private CollapsingToolbarLayout collapsingToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);

        initView();
        
        String username = getIntent().getStringExtra(EXTRA_USERNAME);
        if (TextUtils.isEmpty(username)) {
            finish();
            return;
        }

        loadData(username);
    }

    private void initView() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }

        collapsingToolbar = findViewById(R.id.collapsingToolbar);
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
        progressBar = findViewById(R.id.progressBar);
    }

    private void loadData(String username) {
        progressBar.setVisibility(View.VISIBLE);
        String apiKey = UserPreferences.getApiKey(this);
        String apiPass = UserPreferences.getApiPass(this);

        RetrofitClient.getInstance().getUserDetail(username, apiKey, apiPass).enqueue(new Callback<UserDetailResponse>() {
            @Override
            public void onResponse(Call<UserDetailResponse> call, Response<UserDetailResponse> response) {
                progressBar.setVisibility(View.GONE);
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
                progressBar.setVisibility(View.GONE);
                Toast.makeText(UserDetailActivity.this, "网络错误: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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

        Glide.with(this)
             .load(info.getBackground())
             .placeholder(R.drawable.ic_default_article_cover)
             .into(ivBackground);

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
