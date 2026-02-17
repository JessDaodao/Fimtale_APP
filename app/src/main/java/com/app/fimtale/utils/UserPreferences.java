package com.app.fimtale.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class UserPreferences {
    private static final String PREF_NAME = "fimtale_prefs";
    private static final String KEY_API_KEY = "api_key";
    private static final String KEY_API_PASS = "api_pass";
    private static final String KEY_COOKIES = "cookies";
    private static final String KEY_USER_ID = "user_id";

    static {
        System.loadLibrary("fimtale");
    }

    private static native String getDefaultApiKey();
    private static native String getDefaultApiPass();

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static void saveCredentials(Context context, String apiKey, String apiPass) {
        getPrefs(context).edit()
                .putString(KEY_API_KEY, apiKey)
                .putString(KEY_API_PASS, apiPass)
                .apply();
    }

    public static void saveCookies(Context context, String cookies) {
        getPrefs(context).edit()
                .putString(KEY_COOKIES, cookies)
                .apply();
    }

    public static String getCookies(Context context) {
        return getPrefs(context).getString(KEY_COOKIES, "");
    }

    public static void saveUserId(Context context, String userId) {
        getPrefs(context).edit()
                .putString(KEY_USER_ID, userId)
                .apply();
    }

    public static String getUserId(Context context) {
        return getPrefs(context).getString(KEY_USER_ID, "");
    }

    public static String getUserApiKey(Context context) {
        return getPrefs(context).getString(KEY_API_KEY, "");
    }

    public static String getUserApiPass(Context context) {
        return getPrefs(context).getString(KEY_API_PASS, "");
    }

    public static String getApiKey(Context context) {
        String key = getUserApiKey(context);
        return key.isEmpty() ? getDefaultApiKey() : key;
    }

    public static String getApiPass(Context context) {
        String pass = getUserApiPass(context);
        return pass.isEmpty() ? getDefaultApiPass() : pass;
    }

    // 检查是否已经登录（是否有数据）
    public static boolean isLoggedIn(Context context) {
        String key = getApiKey(context);
        String pass = getApiPass(context);
        return !key.isEmpty() && !pass.isEmpty();
    }

    // 检查用户是否配置了API凭据
    public static boolean isUserConfigured(Context context) {
        String key = getUserApiKey(context);
        String pass = getUserApiPass(context);
        return !key.isEmpty() && !pass.isEmpty();
    }

    // 清除数据（用于注销）
    public static void clear(Context context) {
        getPrefs(context).edit().clear().apply();
    }
}