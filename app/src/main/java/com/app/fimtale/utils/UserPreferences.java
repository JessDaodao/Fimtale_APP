package com.app.fimtale.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class UserPreferences {
    private static final String PREF_NAME = "fimtale_prefs";
    private static final String KEY_API_KEY = "api_key";
    private static final String KEY_API_PASS = "api_pass";

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static void saveCredentials(Context context, String apiKey, String apiPass) {
        getPrefs(context).edit()
                .putString(KEY_API_KEY, apiKey)
                .putString(KEY_API_PASS, apiPass)
                .apply();
    }

    public static String getApiKey(Context context) {
        return getPrefs(context).getString(KEY_API_KEY, "");
    }

    public static String getApiPass(Context context) {
        return getPrefs(context).getString(KEY_API_PASS, "");
    }

    // 检查是否已经登录（是否有数据）
    public static boolean isLoggedIn(Context context) {
        String key = getApiKey(context);
        String pass = getApiPass(context);
        return !key.isEmpty() && !pass.isEmpty();
    }

    // 清除数据（用于注销）
    public static void clear(Context context) {
        getPrefs(context).edit().clear().apply();
    }
}