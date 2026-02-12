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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(item -> {
            int newItemId = item.getItemId();
            if (currentItemId == newItemId) {
                return true;
            }

            Fragment selectedFragment = null;
            if (newItemId == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (newItemId == R.id.nav_article) {
                selectedFragment = new ArticleFragment();
            } else if (newItemId == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
            }

            if (selectedFragment != null) {
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

                transaction.replace(R.id.fragment_container, selectedFragment)
                        .commit();
                
                currentItemId = newItemId;
                return true;
            }
            return false;
        });

        if (savedInstanceState == null) {
            bottomNav.setSelectedItemId(R.id.nav_home);
        } else {
            currentItemId = savedInstanceState.getInt("currentItemId", R.id.nav_home);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("currentItemId", currentItemId);
    }
}
