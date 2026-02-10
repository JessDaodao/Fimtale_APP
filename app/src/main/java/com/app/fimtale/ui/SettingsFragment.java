package com.app.fimtale.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import com.app.fimtale.R;
import com.app.fimtale.utils.UserPreferences;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
        
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());
        EditTextPreference apiKeyPref = findPreference("api_key");
        EditTextPreference apiPassPref = findPreference("api_pass");
        
        if (apiKeyPref != null) {
            apiKeyPref.setText(UserPreferences.getApiKey(requireContext()));
        }
        if (apiPassPref != null) {
            apiPassPref.setText(UserPreferences.getApiPass(requireContext()));
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
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        } else if ("api_key".equals(key) || "api_pass".equals(key)) {
            String apiKey = sharedPreferences.getString("api_key", "");
            String apiPass = sharedPreferences.getString("api_pass", "");
            UserPreferences.saveCredentials(requireContext(), apiKey, apiPass);
        }
    }
}
