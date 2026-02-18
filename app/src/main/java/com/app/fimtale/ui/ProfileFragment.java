package com.app.fimtale.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;

import com.app.fimtale.FavoritesActivity;
import com.app.fimtale.HistoryActivity;
import com.app.fimtale.LoginActivity;
import com.app.fimtale.R;
import com.app.fimtale.SettingsActivity;
import com.app.fimtale.model.MainPageResponse;
import com.app.fimtale.network.RetrofitClient;
import com.app.fimtale.utils.UserPreferences;
import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private View layoutUserHeader;
    private ShapeableImageView ivAvatar;
    private TextView tvUsername;
    private TextView tvBio;
    private View btnFavorites, btnHistory;
    private boolean isLoggedIn = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        layoutUserHeader = view.findViewById(R.id.layoutUserHeader);
        ivAvatar = view.findViewById(R.id.ivAvatar);
        tvUsername = view.findViewById(R.id.tvUsername);
        tvBio = view.findViewById(R.id.tvBio);

        btnFavorites = view.findViewById(R.id.btnFavorites);
        btnHistory = view.findViewById(R.id.btnHistory);

        setupButtons();

        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.profile_menu, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.action_settings) {
                    Intent intent = new Intent(getActivity(), SettingsActivity.class);
                    startActivity(intent);
                    return true;
                } else if (menuItem.getItemId() == R.id.action_toggle_theme) {
                    toggleTheme();
                    return true;
                }
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }

    private void setupButtons() {
        btnFavorites.setOnClickListener(v -> {
            if (!isLoggedIn) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                return;
            }
            Intent intent = new Intent(getActivity(), FavoritesActivity.class);
            startActivity(intent);
        });

        btnHistory.setOnClickListener(v -> {
            if (!isLoggedIn) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                return;
            }
            Intent intent = new Intent(getActivity(), HistoryActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadCachedUserInfo();
        checkLoginStatus();
    }

    private void loadCachedUserInfo() {
        String userIdStr = UserPreferences.getUserId(requireContext());
        String userName = UserPreferences.getUserName(requireContext());

        if (!userIdStr.isEmpty() && !userName.isEmpty()) {
            try {
                int userId = Integer.parseInt(userIdStr);
                isLoggedIn = true;
                updateUserInfo(userId, userName);
            } catch (NumberFormatException e) {
                // 不做处理
            }
        }
    }

    private void checkLoginStatus() {
        String apiKey = UserPreferences.getApiKey(requireContext());
        String apiPass = UserPreferences.getApiPass(requireContext());
        
        RetrofitClient.getInstance().getHomePage(apiKey, apiPass).enqueue(new Callback<MainPageResponse>() {
            @Override
            public void onResponse(Call<MainPageResponse> call, Response<MainPageResponse> response) {
                if (!isAdded()) return;
                if (response.isSuccessful() && response.body() != null) {
                    MainPageResponse.CurrentUser currentUser = response.body().getCurrentUser();
                    if (currentUser != null && currentUser.getId() != 0) {
                        isLoggedIn = true;
                        UserPreferences.saveUserId(requireContext(), String.valueOf(currentUser.getId()));
                        UserPreferences.saveUserName(requireContext(), currentUser.getUserName());
                        updateUserInfo(currentUser.getId(), currentUser.getUserName());
                    } else {
                        isLoggedIn = false;
                        UserPreferences.saveUserId(requireContext(), "");
                        UserPreferences.saveUserName(requireContext(), "");
                        updateUserInfo(0, null);
                    }
                } else {
                    if (!isLoggedIn) {
                        updateUserInfo(0, null);
                    }
                }
            }

            @Override
            public void onFailure(Call<MainPageResponse> call, Throwable t) {
                if (!isAdded()) return;
                if (!isLoggedIn) {
                    updateUserInfo(0, null);
                } else {
                     Toast.makeText(requireContext(), "网络连接失败，显示缓存信息", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateUserInfo(int userId, String userName) {
        if (!isAdded()) return;
        if (isLoggedIn && userId != 0 && userName != null) {
            tvUsername.setText(userName);
            tvBio.setText("欢迎回来");
            layoutUserHeader.setOnClickListener(null);

            ivAvatar.setImageTintList(null);

            String avatarUrl = "https://fimtale.com/upload/avatar/large/" + userId + ".png";

            Glide.with(this)
                    .load(avatarUrl)
                    .placeholder(R.drawable.ic_person)
                    .error(R.drawable.ic_person)
                    .into(ivAvatar);

        } else {
            tvUsername.setText("点击登录");
            tvBio.setText("登录以使用更多功能");
            int color = com.google.android.material.color.MaterialColors.getColor(ivAvatar, com.google.android.material.R.attr.colorOnSurfaceVariant);
            ivAvatar.setImageTintList(android.content.res.ColorStateList.valueOf(color));
            ivAvatar.setImageResource(R.drawable.ic_person);
            layoutUserHeader.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            });
        }
    }

    private void toggleTheme() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        
        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        boolean isNight = currentNightMode == Configuration.UI_MODE_NIGHT_YES;
        boolean newIsNight = !isNight;

        sharedPreferences.edit()
                .putBoolean("theme_follow_system", false)
                .putBoolean("manual_dark_mode", newIsNight)
                .apply();

        if (newIsNight) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}
