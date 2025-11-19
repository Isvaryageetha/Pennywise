package com.example.pennywise;

import android.content.IntentFilter;
import android.content.Intent;
import android.content.BroadcastReceiver;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.pennywise.fragments.AnalyticsFragment;
import com.example.pennywise.fragments.BillsFragment;
import com.example.pennywise.fragments.DashboardFragment;
import com.example.pennywise.fragments.ExpensesFragment;
import com.example.pennywise.fragments.SavingsFragment;
import com.example.pennywise.fragments.SettingsFragment;
import com.example.pennywise.interfaces.OnDataPassListener;
import com.example.pennywise.service.NotificationHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements OnDataPassListener {

    private BottomNavigationView bottomNav;
    private long backPressedTime = 0;
    private static final String TAG = "MainActivity";

    private double balanceThreshold = 1000.0;
    private double currentBalance = 1200.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "=== MAIN ACTIVITY STARTED ===");

        try {
            initializeViews();
            setupBottomNavigation();
            setupBackPressHandler();

            NotificationHelper.startNotificationService(this);

            Log.d(TAG, "=== MAIN ACTIVITY SETUP COMPLETE ===");

        } catch (Exception e) {
            Log.e(TAG, "Setup failed: " + e.getMessage(), e);
            Toast.makeText(this, "Setup error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    private NetworkChangeReceiver receiver = new NetworkChangeReceiver();

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    public void onExpenseAdded(String category, double amount, String date) {
        Log.d(TAG, "Expense added - Category: " + category + ", Amount: " + amount + ", Date: " + date);
       currentBalance -= amount;
       Toast.makeText(this, "Expense added: " + category + " - $" + amount, Toast.LENGTH_SHORT).show();
        updateDashboardFragment();
        checkBalanceAlert();
    }

    @Override
    public void onBillAdded(String billName, double amount, boolean isPaid) {
        Log.d(TAG, "Bill added - Name: " + billName + ", Amount: " + amount + ", Paid: " + isPaid);

        if (!isPaid) {
            currentBalance -= amount;
        }

        Toast.makeText(this, "Bill added: " + billName, Toast.LENGTH_SHORT).show();
        updateDashboardFragment();
        checkBalanceAlert();
    }

    @Override
    public void onSavingsGoalAdded(String purpose, double targetAmount) {
        Log.d(TAG, "Savings goal added - Purpose: " + purpose + ", Target: " + targetAmount);

        Toast.makeText(this, "Savings goal set: " + purpose, Toast.LENGTH_SHORT).show();
        updateDashboardFragment();
    }

    @Override
    public void onBalanceThresholdChanged(double newThreshold) {
        Log.d(TAG, "Balance threshold changed to: " + newThreshold);

        this.balanceThreshold = newThreshold;
        Toast.makeText(this, "Balance threshold updated to $" + newThreshold, Toast.LENGTH_SHORT).show();
        updateDashboardFragment();
    }

    @Override
    public void onDataUpdated() {
        Log.d(TAG, "General data update requested");
        updateDashboardFragment();
    }

    private void checkBalanceAlert() {
        if (currentBalance < balanceThreshold) {
            Toast.makeText(this,
                    "⚠️ Low Balance Alert! Current: $" + currentBalance + " | Threshold: $" + balanceThreshold,
                    Toast.LENGTH_LONG).show();
        }
    }

    private void updateDashboardFragment() {

        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (currentFragment instanceof DashboardFragment) {
            ((DashboardFragment) currentFragment).updateDashboardData(currentBalance, balanceThreshold);
        }
    }

    public double getBalanceThreshold() {
        return balanceThreshold;
    }

    public double getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(double balance) {
        this.currentBalance = balance;
        updateDashboardFragment();
    }
   private void initializeViews() {
        bottomNav = findViewById(R.id.bottom_navigation);

        if (bottomNav == null) {
            throw new RuntimeException("BottomNavigationView not found!");
        }

        Log.d(TAG, "Views initialized successfully");
    }

    private void setupBottomNavigation() {
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_dashboard) {
                loadFragment(new DashboardFragment(), "Dashboard");
            } else if (itemId == R.id.nav_expenses) {
                loadFragment(new ExpensesFragment(), "Expenses");
            } else if (itemId == R.id.nav_savings) {
                loadFragment(new SavingsFragment(), "Savings");
            } else if (itemId == R.id.nav_bills) {
                loadFragment(new BillsFragment(), "Bills");
            } else if (itemId == R.id.nav_analytics) {
                loadFragment(new AnalyticsFragment(), "Analytics");
            } else if (itemId == R.id.nav_settings) {
                loadFragment(new SettingsFragment(), "Settings");
            } else {
                showComingSoonFragment(item.getTitle().toString());
            }
            return true;
        });
       bottomNav.setSelectedItemId(R.id.nav_dashboard);
    }

    private void loadFragment(Fragment fragment, String fragmentName) {
        try {
            Log.d(TAG, "Loading fragment: " + fragmentName);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, fragment);
            transaction.commit();

            Log.d(TAG, "Fragment loaded: " + fragmentName);

        } catch (Exception e) {
            Log.e(TAG, "Error loading fragment " + fragmentName + ": " + e.getMessage(), e);
            showErrorFragment("Failed to load " + fragmentName);
        }
    }

    private void showComingSoonFragment(String sectionName) {
        Fragment comingSoonFragment = new Fragment() {
            @Override
            public android.view.View onCreateView(android.view.LayoutInflater inflater,
                                                  android.view.ViewGroup container,
                                                  Bundle savedInstanceState) {
                android.widget.TextView textView = new android.widget.TextView(getContext());
                textView.setText(sectionName + "\n\nComing Soon!");
                textView.setTextSize(18);
                textView.setGravity(android.view.Gravity.CENTER);
                textView.setPadding(50, 50, 50, 50);
                return textView;
            }
        };

        loadFragment(comingSoonFragment, "ComingSoon");
    }

    private void showErrorFragment(String message) {
        Fragment errorFragment = new Fragment() {
            @Override
            public android.view.View onCreateView(android.view.LayoutInflater inflater,
                                                  android.view.ViewGroup container,
                                                  Bundle savedInstanceState) {
                android.widget.TextView textView = new android.widget.TextView(getContext());
                textView.setText("Error\n\n" + message);
                textView.setTextColor(android.graphics.Color.RED);
                textView.setTextSize(16);
                textView.setPadding(50, 50, 50, 50);
                textView.setGravity(android.view.Gravity.CENTER);
                return textView;
            }
        };

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, errorFragment)
                .commit();
    }

    private void setupBackPressHandler() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (backPressedTime + 2000 > System.currentTimeMillis()) {
                    finish();
                } else {
                    Toast.makeText(MainActivity.this, "Press back again to exit", Toast.LENGTH_SHORT).show();
                }
                backPressedTime = System.currentTimeMillis();
            }
        });
    }
}