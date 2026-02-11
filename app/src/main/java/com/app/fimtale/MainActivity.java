package com.app.fimtale;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.app.fimtale.ui.HomeFragment;
import com.app.fimtale.ui.ProfileFragment;
import com.app.fimtale.utils.UserPreferences;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private int currentItemId = 0;
    private HomeFragment homeFragment;
    private ProfileFragment profileFragment;

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
                if (homeFragment == null) homeFragment = new HomeFragment();
                selectedFragment = homeFragment;
            } else if (newItemId == R.id.nav_profile) {
                if (profileFragment == null) profileFragment = new ProfileFragment();
                selectedFragment = profileFragment;
            }

            if (selectedFragment != null) {
                androidx.fragment.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                if (currentItemId == R.id.nav_home && newItemId == R.id.nav_profile) {
                    transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
                } else if (currentItemId == R.id.nav_profile && newItemId == R.id.nav_home) {
                    transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
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
            currentItemId = bottomNav.getSelectedItemId();
        }
    }
}
