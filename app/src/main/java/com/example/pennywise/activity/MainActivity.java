package com.example.pennywise.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.pennywise.R;
import com.example.pennywise.fragments.AnalyticsFragment;
import com.example.pennywise.fragments.BillsFragment;
import com.example.pennywise.fragments.DashboardFragment;
import com.example.pennywise.fragments.ExpensesFragment;
import com.example.pennywise.fragments.SavingsFragment;
import com.example.pennywise.fragments.SettingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            int id = item.getItemId();
            if (id == R.id.nav_dashboard) {
                selectedFragment = new DashboardFragment();
            } else if (id == R.id.nav_expenses) {
                selectedFragment = new ExpensesFragment();
            } else if (id == R.id.nav_savings) {
                selectedFragment = new SavingsFragment();
            } else if (id == R.id.nav_bills) {
                selectedFragment = new BillsFragment();
            } else if (id == R.id.nav_analytics) {
                selectedFragment = new AnalyticsFragment();
            } else if (id == R.id.nav_settings) {
                selectedFragment = new SettingsFragment();
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
            }
            return true;
        });


        // Load default fragment
        if (savedInstanceState == null) {
            loadFragment(new DashboardFragment());
        }
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
