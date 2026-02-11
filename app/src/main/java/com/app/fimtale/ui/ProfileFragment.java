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

import com.app.fimtale.LoginActivity;
import com.app.fimtale.R;
import com.app.fimtale.SettingsActivity;
import com.app.fimtale.utils.UserPreferences;
import com.google.android.material.imageview.ShapeableImageView;

public class ProfileFragment extends Fragment {

    private View layoutUserHeader;
    private ShapeableImageView ivAvatar;
    private TextView tvUsername;
    private TextView tvBio;

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

        layoutUserHeader.setOnClickListener(v -> {
            if (!UserPreferences.isLoggedIn(requireContext())) {
                Intent intent = new Intent(requireContext(), LoginActivity.class);
                intent.putExtra(LoginActivity.EXTRA_SHOW_LOGIN, true);
                startActivity(intent);
            } else {
                Toast.makeText(requireContext(), "已登录", Toast.LENGTH_SHORT).show();
            }
        });

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

    @Override
    public void onResume() {
        super.onResume();
        updateUserInfo();
    }

    private void updateUserInfo() {
        if (UserPreferences.isLoggedIn(requireContext())) {
            tvUsername.setText("已登录用户");
            tvBio.setText("欢迎回来！");
        } else {
            tvUsername.setText("点击登录");
            tvBio.setText("登录后同步书架和评论");
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
