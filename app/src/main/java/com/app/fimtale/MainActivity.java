package com.app.fimtale;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.app.fimtale.ui.ArticleFragment;
import com.app.fimtale.ui.HomeFragment;
import com.app.fimtale.ui.ProfileFragment;
import com.app.fimtale.utils.UserPreferences;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private int currentItemId = 0;
    private HomeFragment homeFragment;
    private ArticleFragment articleFragment;
    private ProfileFragment profileFragment;
    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        if (savedInstanceState != null) {
            currentItemId = savedInstanceState.getInt("currentItemId", R.id.nav_home);
            homeFragment = (HomeFragment) getSupportFragmentManager().findFragmentByTag("HOME");
            articleFragment = (ArticleFragment) getSupportFragmentManager().findFragmentByTag("ARTICLE");
            profileFragment = (ProfileFragment) getSupportFragmentManager().findFragmentByTag("PROFILE");

            if (currentItemId == R.id.nav_home) currentFragment = homeFragment;
            else if (currentItemId == R.id.nav_article) currentFragment = articleFragment;
            else if (currentItemId == R.id.nav_profile) currentFragment = profileFragment;
        }

        bottomNav.setOnItemSelectedListener(item -> {
            int newItemId = item.getItemId();
            if (currentItemId == newItemId && currentFragment != null) {
                return true;
            }

            Fragment targetFragment = null;
            String tag = "";

            if (newItemId == R.id.nav_home) {
                if (homeFragment == null) homeFragment = new HomeFragment();
                targetFragment = homeFragment;
                tag = "HOME";
            } else if (newItemId == R.id.nav_article) {
                if (articleFragment == null) articleFragment = new ArticleFragment();
                targetFragment = articleFragment;
                tag = "ARTICLE";
            } else if (newItemId == R.id.nav_profile) {
                if (profileFragment == null) profileFragment = new ProfileFragment();
                targetFragment = profileFragment;
                tag = "PROFILE";
            }

            if (targetFragment != null) {
                androidx.fragment.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                Map<Integer, Integer> menuOrder = new HashMap<>();
                menuOrder.put(R.id.nav_home, 0);
                menuOrder.put(R.id.nav_article, 1);
                menuOrder.put(R.id.nav_profile, 2);

                Integer currentOrder = menuOrder.get(currentItemId);
                Integer newOrder = menuOrder.get(newItemId);

                if (currentOrder != null && newOrder != null) {
                    if (newOrder > currentOrder) {
                        transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
                    } else {
                        transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
                    }
                }

                if (!targetFragment.isAdded()) {
                    transaction.add(R.id.fragment_container, targetFragment, tag);
                    if (currentFragment != null) {
                        transaction.hide(currentFragment);
                    }
                } else {
                    if (currentFragment != null) {
                        transaction.hide(currentFragment);
                    }
                    transaction.show(targetFragment);
                }
                
                transaction.commit();
                
                currentFragment = targetFragment;
                currentItemId = newItemId;
                return true;
            }
            return false;
        });

        if (savedInstanceState == null) {
            bottomNav.setSelectedItemId(R.id.nav_home);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("currentItemId", currentItemId);
    }
}
