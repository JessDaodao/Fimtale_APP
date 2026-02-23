package com.app.fimtale.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.app.fimtale.R;
import com.app.fimtale.model.MainPageResponse;
import com.app.fimtale.network.RetrofitClient;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DialogHelper {

    public interface OnCredentialsSavedListener {
        void onSaved();
    }

    public static void showApiCredentialsDialog(Context context, OnCredentialsSavedListener listener) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_api_credentials, null);
        TextInputEditText etApiKey = view.findViewById(R.id.etApiKey);
        TextInputEditText etApiPass = view.findViewById(R.id.etApiPass);
        LinearLayout loadingLayout = view.findViewById(R.id.loadingLayout);
        TextView tvError = view.findViewById(R.id.tvError);

        etApiKey.setText(UserPreferences.getUserApiKey(context));
        etApiPass.setText(UserPreferences.getUserApiPass(context));

        AlertDialog dialog = new MaterialAlertDialogBuilder(context)
                .setTitle("配置 API 凭据")
                .setView(view)
                .setPositiveButton("保存", null)
                .setNegativeButton("取消", null)
                .create();

        dialog.show();

        View btnPositive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        btnPositive.setOnClickListener(v -> {
            String apiKey = etApiKey.getText() != null ? etApiKey.getText().toString().trim() : "";
            String apiPass = etApiPass.getText() != null ? etApiPass.getText().toString().trim() : "";

            if (apiKey.isEmpty() || apiPass.isEmpty()) {
                tvError.setText("请输入完整的凭据");
                tvError.setVisibility(View.VISIBLE);
                return;
            }

            loadingLayout.setVisibility(View.VISIBLE);
            tvError.setVisibility(View.GONE);
            btnPositive.setEnabled(false);

            RetrofitClient.getInstance().getHomePage(apiKey, apiPass).enqueue(new Callback<MainPageResponse>() {
                @Override
                public void onResponse(@NonNull Call<MainPageResponse> call,
                        @NonNull Response<MainPageResponse> response) {
                    if (response.isSuccessful()) {
                        UserPreferences.saveCredentials(context, apiKey, apiPass);
                        Toast.makeText(context, "凭据已保存", Toast.LENGTH_SHORT).show();
                        if (listener != null) {
                            listener.onSaved();
                        }
                        dialog.dismiss();
                    } else {
                        loadingLayout.setVisibility(View.GONE);
                        btnPositive.setEnabled(true);
                        if (response.code() == 403) {
                            tvError.setText("无效的 API 凭据");
                        } else {
                            tvError.setText("验证失败: " + response.code());
                        }
                        tvError.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<MainPageResponse> call, @NonNull Throwable t) {
                    loadingLayout.setVisibility(View.GONE);
                    btnPositive.setEnabled(true);
                    tvError.setText("网络错误: " + t.getMessage());
                    tvError.setVisibility(View.VISIBLE);
                }
            });
        });
    }
}
