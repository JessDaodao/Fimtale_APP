package com.app.fimtale;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.app.fimtale.utils.UserPreferences;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText apiKeyInput, apiPassInput;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (UserPreferences.isLoggedIn(this)) {
            startMainActivity();
            return;
        }

        setContentView(R.layout.activity_login);

        apiKeyInput = findViewById(R.id.apiKeyInput);
        apiPassInput = findViewById(R.id.apiPassInput);
        loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(v -> {
            String key = apiKeyInput.getText().toString().trim();
            String pass = apiPassInput.getText().toString().trim();

            if (TextUtils.isEmpty(key) || TextUtils.isEmpty(pass)) {
                Toast.makeText(this, "请输入完整的 API Key 和 Pass", Toast.LENGTH_SHORT).show();
            } else {
                // 保存凭证
                UserPreferences.saveCredentials(this, key, pass);
                // 跳转
                startMainActivity();
            }
        });
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish(); // 关闭登录页，这样按返回键不会回到登录页
    }
}