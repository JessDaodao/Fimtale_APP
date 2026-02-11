package com.app.fimtale;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

import com.app.fimtale.utils.UserPreferences;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    public static final String EXTRA_SHOW_LOGIN = "show_login";

    private TextInputEditText apiKeyInput, apiPassInput;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean followSystem = prefs.getBoolean("theme_follow_system", true);
        if (followSystem) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        } else {
            boolean manualDarkMode = prefs.getBoolean("manual_dark_mode", false);
            if (manualDarkMode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        }

        boolean showLogin = getIntent().getBooleanExtra(EXTRA_SHOW_LOGIN, false);

        if (!showLogin) {
            startMainActivity();
            return;
        }

        setContentView(R.layout.activity_login);

        apiKeyInput = findViewById(R.id.apiKeyInput);
        apiPassInput = findViewById(R.id.apiPassInput);
        loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(v -> attemptLogin());
        
        if (UserPreferences.isLoggedIn(this)) {
            apiKeyInput.setText(UserPreferences.getApiKey(this));
            apiPassInput.setText(UserPreferences.getApiPass(this));
        }
    }

    private void attemptLogin() {
        String apiKey = apiKeyInput.getText().toString().trim();
        String apiPass = apiPassInput.getText().toString().trim();

        if (TextUtils.isEmpty(apiKey)) {
            apiKeyInput.setError("请输入 API Key");
            return;
        }

        if (TextUtils.isEmpty(apiPass)) {
            apiPassInput.setError("请输入 API Pass");
            return;
        }

        UserPreferences.saveCredentials(this, apiKey, apiPass);
        Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
        
        finish();
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
