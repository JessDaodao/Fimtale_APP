package com.app.fimtale.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import com.app.fimtale.model.UpdateResponse;
import com.app.fimtale.network.RetrofitClient;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.concurrent.Executors;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateChecker {

    private static final String UPDATE_URL = "https://ftapp.eqad.fun/update/";

    public static void checkUpdate(Context context, boolean manual) {
        RetrofitClient.getUpdateService().checkUpdate(UPDATE_URL).enqueue(new Callback<UpdateResponse>() {
            @Override
            public void onResponse(@NonNull Call<UpdateResponse> call, @NonNull Response<UpdateResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UpdateResponse update = response.body();
                    int currentVersion = getVersionCode(context);
                    if (update.getVersionCode() > currentVersion) {
                        if (update.isForceUpdate() || manual || UserPreferences.isAutoUpdateEnabled(context)) {
                            showUpdateDialog(context, update);
                        }
                    } else if (manual) {
                        Toast.makeText(context, "当前已是最新版本", Toast.LENGTH_SHORT).show();
                    }
                } else if (manual) {
                    Toast.makeText(context, "检查更新失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<UpdateResponse> call, @NonNull Throwable t) {
                if (manual) {
                    Toast.makeText(context, "网络错误", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private static int getVersionCode(Context context) {
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                return (int) pInfo.getLongVersionCode();
            } else {
                return pInfo.versionCode;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private static void showUpdateDialog(Context context, UpdateResponse update) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context)
                .setTitle("发现新版本: " + update.getVersionName())
                .setMessage(update.getUpdateLog())
                .setCancelable(!update.isForceUpdate())
                .setPositiveButton("立即更新", (dialog, which) -> {
                    downloadAndInstallApk(context, update.getDownloadUrl());
                });

        if (!update.isForceUpdate()) {
            builder.setNegativeButton("稍后再说", null);
        }

        builder.show();
    }

    private static void downloadAndInstallApk(Context context, String downloadUrl) {
        Toast.makeText(context, "开始下载更新...", Toast.LENGTH_SHORT).show();
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                TrustManager[] trustAllCerts = new TrustManager[]{
                        new X509TrustManager() {
                            public X509Certificate[] getAcceptedIssuers() { return null; }
                            public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                            public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                        }
                };

                SSLContext sc = SSLContext.getInstance("SSL");
                sc.init(null, trustAllCerts, new SecureRandom());
                HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
                HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);

                URL url = new URL(downloadUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                File apkFile = new File(context.getExternalFilesDir(null), "update.apk");
                InputStream inputStream = connection.getInputStream();
                FileOutputStream outputStream = new FileOutputStream(apkFile);

                byte[] buffer = new byte[4096];
                int len;
                while ((len = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, len);
                }
                outputStream.close();
                inputStream.close();

                ((Activity) context).runOnUiThread(() -> installApk(context, apkFile));
            } catch (Exception e) {
                e.printStackTrace();
                ((Activity) context).runOnUiThread(() -> 
                    Toast.makeText(context, "下载失败: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        });
    }

    private static void installApk(Context context, File apkFile) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri apkUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            apkUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", apkFile);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            apkUri = Uri.fromFile(apkFile);
        }
        intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        context.startActivity(intent);
    }
}
