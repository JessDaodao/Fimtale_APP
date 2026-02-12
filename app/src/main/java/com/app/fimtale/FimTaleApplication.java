package com.app.fimtale;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;
import com.app.fimtale.utils.GravitySensorHelper;
import java.util.WeakHashMap;

public class FimTaleApplication extends Application implements Application.ActivityLifecycleCallbacks {

    private final WeakHashMap<Activity, GravitySensorHelper> gravityHelpers = new WeakHashMap<>();
    private boolean isGravityModeEnabled = false;
    private SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener;

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(this);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        isGravityModeEnabled = prefs.getBoolean("gravity_mode", false);

        preferenceChangeListener = (sharedPreferences, key) -> {
            if ("gravity_mode".equals(key)) {
                isGravityModeEnabled = sharedPreferences.getBoolean(key, false);
                updateAllHelpers();
            }
        };
        prefs.registerOnSharedPreferenceChangeListener(preferenceChangeListener);
    }

    private void updateAllHelpers() {
        for (Activity activity : gravityHelpers.keySet()) {
            GravitySensorHelper helper = gravityHelpers.get(activity);
            if (helper != null) {
                if (isGravityModeEnabled) {
                    helper.start();
                } else {
                    helper.stop();
                }
            }
        }
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
        GravitySensorHelper helper = new GravitySensorHelper(activity, activity.findViewById(android.R.id.content));
        gravityHelpers.put(activity, helper);
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        GravitySensorHelper helper = gravityHelpers.get(activity);
        if (helper != null && isGravityModeEnabled) {
            helper.start();
        }
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
        GravitySensorHelper helper = gravityHelpers.get(activity);
        if (helper != null) {
            helper.stop();
        }
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        gravityHelpers.remove(activity);
    }
}
