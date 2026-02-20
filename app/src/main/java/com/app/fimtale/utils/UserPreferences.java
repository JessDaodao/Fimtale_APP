package com.app.fimtale.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class UserPreferences {
    private static final String PREF_NAME = "fimtale_prefs";
    private static final String KEY_API_KEY = "api_key";
    private static final String KEY_API_PASS = "api_pass";
    private static final String KEY_COOKIES = "cookies";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_NAME = "user_name";

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
        if (!isSafeMode(context)) {
            cookies = cookies + "; CarbonBBS_SafeMode=0";
        }
        getPrefs(context).edit()
                .putString(KEY_COOKIES, cookies)
                .apply();
    }

    public static String getCookies(Context context) {
        String cookies = getPrefs(context).getString(KEY_COOKIES, "");
        if (!isSafeMode(context)) {
            if (!cookies.contains("CarbonBBS_SafeMode=0")) {
                 cookies = cookies + "; CarbonBBS_SafeMode=0";
            }
        } else {
             cookies = cookies.replace("; CarbonBBS_SafeMode=0", "");
             cookies = cookies.replace("CarbonBBS_SafeMode=0;", "");
             cookies = cookies.replace("CarbonBBS_SafeMode=0", "");
        }
        return cookies;
    }

    public static void setSafeMode(Context context, boolean safeMode) {
        getPrefs(context).edit()
                .putBoolean("safe_mode", safeMode)
                .apply();
    }

    public static boolean isSafeMode(Context context) {
        return getPrefs(context).getBoolean("safe_mode", true);
    }

    public static void setAutoUpdate(Context context, boolean autoUpdate) {
        getPrefs(context).edit()
                .putBoolean("auto_update", autoUpdate)
                .apply();
    }

    public static boolean isAutoUpdateEnabled(Context context) {
        return getPrefs(context).getBoolean("auto_update", true);
    }

    public static void saveUserId(Context context, String userId) {
        getPrefs(context).edit()
                .putString(KEY_USER_ID, userId)
                .apply();
    }

    public static String getUserId(Context context) {
        return getPrefs(context).getString(KEY_USER_ID, "");
    }

    public static void saveUserName(Context context, String userName) {
        getPrefs(context).edit()
                .putString(KEY_USER_NAME, userName)
                .apply();
    }

    public static String getUserName(Context context) {
        return getPrefs(context).getString(KEY_USER_NAME, "");
    }

    public static String getUserApiKey(Context context) {
        return getPrefs(context).getString(KEY_API_KEY, "");
    }

    public static String getUserApiPass(Context context) {
        return getPrefs(context).getString(KEY_API_PASS, "");
    }

    public static String getApiKey(Context context) {
        return getUserApiKey(context);
    }

    public static String getApiPass(Context context) {
        return getUserApiPass(context);
    }

    public static boolean isLoggedIn(Context context) {
        String key = getApiKey(context);
        String pass = getApiPass(context);
        return !key.isEmpty() && !pass.isEmpty();
    }

    public static boolean isUserConfigured(Context context) {
        String key = getUserApiKey(context);
        String pass = getUserApiPass(context);
        return !key.isEmpty() && !pass.isEmpty();
    }

    public static void clear(Context context) {
        getPrefs(context).edit().clear().apply();
    }
}