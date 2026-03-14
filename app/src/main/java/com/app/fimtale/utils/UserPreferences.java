package com.app.fimtale.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserPreferences {
    private static final String PREF_NAME = "fimtale_prefs";
    private static final String KEY_API_KEY = "api_key";
    private static final String KEY_API_PASS = "api_pass";
    private static final String KEY_COOKIES = "cookies";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_SEARCH_HISTORY = "search_history";
    private static final int MAX_SEARCH_HISTORY = 10;

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

    public static void setShowReaderProgress(Context context, boolean show) {
        getPrefs(context).edit()
                .putBoolean("show_reader_progress", show)
                .apply();
    }

    public static boolean isShowReaderProgress(Context context) {
        return getPrefs(context).getBoolean("show_reader_progress", false);
    }

    public static void setLineSpacing(Context context, float spacing) {
        getPrefs(context).edit()
                .putFloat("reader_line_spacing", spacing)
                .apply();
    }

    public static float getLineSpacing(Context context) {
        return getPrefs(context).getFloat("reader_line_spacing", 1.4f);
    }

    public static void setReaderTheme(Context context, int theme) {
        getPrefs(context).edit()
                .putInt("reader_theme_preset", theme)
                .apply();
    }

    public static int getReaderTheme(Context context) {
        return getPrefs(context).getInt("reader_theme_preset", 0);
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
        return true;
    }

    public static boolean isUserConfigured(Context context) {
        return true;
    }

    public static void saveSearchHistory(Context context, String query) {
        if (query == null || query.trim().isEmpty()) return;
        query = query.trim();
        
        List<String> history = getSearchHistory(context);
        history.remove(query);
        history.add(0, query);
        
        if (history.size() > MAX_SEARCH_HISTORY) {
            history = history.subList(0, MAX_SEARCH_HISTORY);
        }
        
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < history.size(); i++) {
            sb.append(history.get(i));
            if (i < history.size() - 1) {
                sb.append(",");
            }
        }
        
        getPrefs(context).edit().putString(KEY_SEARCH_HISTORY, sb.toString()).apply();
    }

    public static List<String> getSearchHistory(Context context) {
        String historyStr = getPrefs(context).getString(KEY_SEARCH_HISTORY, "");
        if (historyStr.isEmpty()) {
            return new ArrayList<>();
        }
        return new ArrayList<>(Arrays.asList(historyStr.split(",")));
    }

    public static void clearSearchHistory(Context context) {
        getPrefs(context).edit().remove(KEY_SEARCH_HISTORY).apply();
    }

    public static void removeSearchHistoryItem(Context context, String query) {
        List<String> history = getSearchHistory(context);
        history.remove(query);
        
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < history.size(); i++) {
            sb.append(history.get(i));
            if (i < history.size() - 1) {
                sb.append(",");
            }
        }
        getPrefs(context).edit().putString(KEY_SEARCH_HISTORY, sb.toString()).apply();
    }

    public static void clear(Context context) {
        getPrefs(context).edit().clear().apply();
    }
}
