package com.app.fimtale.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreferenceCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.annotation.NonNull;
import com.app.fimtale.R;
import com.app.fimtale.SettingsActivity;
import com.app.fimtale.utils.UserPreferences;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    private final Preference.OnPreferenceChangeListener gravityChangeListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
            boolean enabled = (boolean) newValue;
            if (enabled) {
                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle("警告")
                        .setMessage("该模式仅为娱乐使用，可能会导致一系列意想不到的BUG！确定开启吗？")
                        .setPositiveButton("确定", (dialog, which) -> {
                            preference.setOnPreferenceChangeListener(null);
                            ((SwitchPreferenceCompat) preference).setChecked(true);
                            preference.setOnPreferenceChangeListener(this);
                        })
                        .setNegativeButton("取消", null)
                        .show();
                return false;
            }
            return true;
        }
    };

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = getListView();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (getActivity() instanceof SettingsActivity) {
                    ((SettingsActivity) getActivity()).onScroll(recyclerView.computeVerticalScrollOffset());
                }
            }
        });
    }

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        Preference apiCredsPref = findPreference("api_credentials");
        if (apiCredsPref != null) {
            apiCredsPref.setOnPreferenceClickListener(preference -> {
                showApiCredentialsDialog();
                return true;
            });
            updateApiSummary(apiCredsPref);
        }

        SwitchPreferenceCompat gravityPref = findPreference("gravity_mode");
        if (gravityPref != null) {
            gravityPref.setOnPreferenceChangeListener(gravityChangeListener);
        }
    }

    private void showApiCredentialsDialog() {
        View view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_api_credentials, null);
        TextInputEditText etApiKey = view.findViewById(R.id.etApiKey);
        TextInputEditText etApiPass = view.findViewById(R.id.etApiPass);

        etApiKey.setText(UserPreferences.getApiKey(requireContext()));
        etApiPass.setText(UserPreferences.getApiPass(requireContext()));

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("设置 API 凭据")
                .setView(view)
                .setPositiveButton("保存", (dialog, which) -> {
                    String apiKey = etApiKey.getText() != null ? etApiKey.getText().toString().trim() : "";
                    String apiPass = etApiPass.getText() != null ? etApiPass.getText().toString().trim() : "";
                    UserPreferences.saveCredentials(requireContext(), apiKey, apiPass);
                    
                    Preference apiCredsPref = findPreference("api_credentials");
                    if (apiCredsPref != null) {
                        updateApiSummary(apiCredsPref);
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }
    
    private void updateApiSummary(Preference preference) {
        boolean isLoggedIn = UserPreferences.isLoggedIn(requireContext());
        if (isLoggedIn) {
            preference.setSummary("已配置");
        } else {
            preference.setSummary("未配置");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if ("theme_follow_system".equals(key)) {
            boolean followSystem = sharedPreferences.getBoolean(key, true);
            if (followSystem) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            } else {
                boolean manualDarkMode = sharedPreferences.getBoolean("manual_dark_mode", false);
                if (manualDarkMode) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
            }
        }
    }
}
