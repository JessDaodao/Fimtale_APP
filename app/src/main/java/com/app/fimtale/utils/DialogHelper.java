package com.app.fimtale.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import com.app.fimtale.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

public class DialogHelper {

    public interface OnCredentialsSavedListener {
        void onSaved();
    }

    public static void showApiCredentialsDialog(Context context, OnCredentialsSavedListener listener) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_api_credentials, null);
        TextInputEditText etApiKey = view.findViewById(R.id.etApiKey);
        TextInputEditText etApiPass = view.findViewById(R.id.etApiPass);

        etApiKey.setText(UserPreferences.getUserApiKey(context));
        etApiPass.setText(UserPreferences.getUserApiPass(context));

        new MaterialAlertDialogBuilder(context)
                .setTitle("配置 API 凭据")
                .setView(view)
                .setPositiveButton("保存", (dialog, which) -> {
                    String apiKey = etApiKey.getText() != null ? etApiKey.getText().toString().trim() : "";
                    String apiPass = etApiPass.getText() != null ? etApiPass.getText().toString().trim() : "";
                    UserPreferences.saveCredentials(context, apiKey, apiPass);
                    
                    if (listener != null) {
                        listener.onSaved();
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }
}
