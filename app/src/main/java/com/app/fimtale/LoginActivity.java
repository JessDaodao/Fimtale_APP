package com.app.fimtale;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.app.fimtale.network.FimTaleApiService;
import com.app.fimtale.network.RetrofitClient;
import com.app.fimtale.utils.UserPreferences;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etAccount;
    private TextInputEditText etPassword;
    private Button btnLogin;
    private Dialog captchaDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        etAccount = findViewById(R.id.et_account);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);

        btnLogin.setOnClickListener(v -> {
            String account = etAccount.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (account.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "请输入账号和密码", Toast.LENGTH_SHORT).show();
                return;
            }

            showCaptcha();
        });
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void showCaptcha() {
        captchaDialog = new Dialog(this); 
        WebView webView = new WebView(this);
        webView.setBackgroundColor(0);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccess(true);

        webView.addJavascriptInterface(new CaptchaInterface(), "Android");
        webView.setWebViewClient(new WebViewClient());

        webView.loadUrl("file:///android_asset/captcha.html");

        captchaDialog.setContentView(webView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 800));
        
        if (captchaDialog.getWindow() != null) {
            captchaDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            captchaDialog.getWindow().setDimAmount(0f);
            captchaDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        
        captchaDialog.show();
    }

    private class CaptchaInterface {
        @JavascriptInterface
        public void onVerify(String ticket, String randstr) {
            runOnUiThread(() -> {
                if (captchaDialog != null && captchaDialog.isShowing()) {
                    captchaDialog.dismiss();
                }
                performLogin(ticket, randstr);
            });
        }

        @JavascriptInterface
        public void onCancel() {
            runOnUiThread(() -> {
                if (captchaDialog != null && captchaDialog.isShowing()) {
                    captchaDialog.dismiss();
                }
                Toast.makeText(LoginActivity.this, "验证取消", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void performLogin(String ticket, String randstr) {
        String account = etAccount.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        FimTaleApiService apiService = RetrofitClient.getInstance();
        RequestBody accountBody = RequestBody.create(MediaType.parse("text/plain"), account);
        RequestBody passwordBody = RequestBody.create(MediaType.parse("text/plain"), password);
        RequestBody codeBody = RequestBody.create(MediaType.parse("text/plain"), ticket);
        RequestBody randBody = RequestBody.create(MediaType.parse("text/plain"), randstr);

        Call<ResponseBody> call = apiService.login("https://fimtale.com/login", accountBody, passwordBody, codeBody, randBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    boolean loginSuccess = false;
                    String userId = "";
                    for (String header : response.headers().values("Set-Cookie")) {
                        if (header.contains("CarbonBBS_UserID")) {
                            loginSuccess = true;
                            String[] parts = header.split(";");
                            for (String part : parts) {
                                part = part.trim();
                                if (part.startsWith("CarbonBBS_UserID=")) {
                                    userId = part.substring("CarbonBBS_UserID=".length());
                                    break;
                                }
                            }
                            break;
                        }
                    }

                    if (loginSuccess) {
                        if (!userId.isEmpty()) {
                            UserPreferences.saveUserId(LoginActivity.this, userId);
                        }
                        Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                        checkLoginStatus();
                    } else {
                        Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "登录请求失败: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "网络错误: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkLoginStatus() {
        FimTaleApiService apiService = RetrofitClient.getInstance();
        apiService.checkLogin("https://fimtale.com/").enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "登录已过期", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "验证登录状态出错", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
