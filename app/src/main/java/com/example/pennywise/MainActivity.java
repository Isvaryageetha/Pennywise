package com.example.pennywise;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.pennywise.fragments.*;
import com.example.pennywise.interfaces.OnDataPassListener;
import com.example.pennywise.service.NotificationHelper;
import com.example.pennywise.GeofenceBroadcastReceiver;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements OnDataPassListener {

    private BottomNavigationView bottomNav;
    private long backPressedTime = 0;

    private double balanceThreshold = 1000.0;
    private double currentBalance = 1200.0;

    // Geofencing
    private GeofencingClient geofencingClient;
    private PendingIntent geofencePendingIntent;

    // HashMap to store supermarkets
    private HashMap<String, double[]> supermarketMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        setupBottomNavigation();
        setupBackPressHandler();
        NotificationHelper.startNotificationService(this);

        // Initialize geofencing
        geofencingClient = LocationServices.getGeofencingClient(this);
        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
        geofencePendingIntent = PendingIntent.getBroadcast(
                this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        initializeSupermarkets();
        requestLocationPermissions();
    }

    // Initialize supermarket coordinates
    private void initializeSupermarkets() {
        supermarketMap.put("Supermarket A", new double[]{11.955690, 79.823588});
        supermarketMap.put("Supermarket B", new double[]{11.953369, 79.819440});
        supermarketMap.put("Supermarket C", new double[]{11.896678, 79.803101});
        supermarketMap.put("Supermarket D", new double[]{11.966436, 79.836055});
        supermarketMap.put("Supermarket E", new double[]{11.934959, 79.830927});
    }

    private void requestLocationPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    }, 1001);
        } else {
            addSupermarketGeofences();
        }
    }

    // Add geofences for all supermarkets
    private void addSupermarketGeofences() {
        for (String name : supermarketMap.keySet()) {
            double[] coords = supermarketMap.get(name);
            double lat = coords[0];
            double lng = coords[1];

            Geofence geofence = new Geofence.Builder()
                    .setRequestId(name)
                    .setCircularRegion(lat, lng, 100) // 100m radius
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                    .build();

            GeofencingRequest request = new GeofencingRequest.Builder()
                    .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                    .addGeofence(geofence)
                    .build();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            geofencingClient.addGeofences(request, geofencePendingIntent)
                    .addOnSuccessListener(aVoid ->
                            Toast.makeText(this, name + " geofence added!", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Failed to add geofence: " + e.getMessage(), Toast.LENGTH_LONG).show());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1001) {
            boolean granted = true;
            for (int result : grantResults) if (result != PackageManager.PERMISSION_GRANTED) granted = false;
            if (granted) addSupermarketGeofences();
            else Toast.makeText(this, "Location permissions are required for geofencing", Toast.LENGTH_LONG).show();
        }
    }

    // --- Expense, Bill, Savings, Balance --- //
    @Override
    public void onExpenseAdded(String category, double amount, String date) {
        currentBalance -= amount;
        Toast.makeText(this, "Expense added: " + category + " - $" + amount, Toast.LENGTH_SHORT).show();
        updateDashboardFragment();
        checkBalanceAlert();
    }

    @Override
    public void onBillAdded(String billName, double amount, boolean isPaid) {
        if (!isPaid) currentBalance -= amount;
        Toast.makeText(this, "Bill added: " + billName + " - $" + amount, Toast.LENGTH_SHORT).show();
        updateDashboardFragment();
        checkBalanceAlert();
    }

    @Override
    public void onSavingsGoalAdded(String purpose, double targetAmount) {
        Toast.makeText(this, "Savings goal set: " + purpose + " - $" + targetAmount, Toast.LENGTH_SHORT).show();
        updateDashboardFragment();
    }

    @Override
    public void onBalanceThresholdChanged(double newThreshold) {
        this.balanceThreshold = newThreshold;
        Toast.makeText(this, "Balance threshold updated to $" + newThreshold, Toast.LENGTH_SHORT).show();
        updateDashboardFragment();
    }

    @Override
    public void onDataUpdated() {
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
        if (currentFragment instanceof DashboardFragment) ((DashboardFragment) currentFragment).onResume();
    }

    // --- Bottom navigation --- //
    private void initializeViews() {
        bottomNav = findViewById(R.id.bottom_navigation);
    }

    private void setupBottomNavigation() {
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Fragment fragment = null;
            String name = "";

            if (itemId == R.id.nav_dashboard) {
                fragment = new DashboardFragment();
                name = "Dashboard";
            } else if (itemId == R.id.nav_expenses) {
                fragment = new ExpenseFragment();
                name = "Expenses";
            } else if (itemId == R.id.nav_savings) {
                fragment = new SavingsFragment();
                name = "Savings";
            } else if (itemId == R.id.nav_bills) {
                fragment = new BillsFragment();
                name = "Bills";
            } else if (itemId == R.id.nav_analytics) {
                fragment = new AnalyticsFragment();
                name = "Analytics";
            } else if (itemId == R.id.nav_settings) {
                fragment = new SettingsFragment();
                name = "Settings";
            }

            if (fragment != null) loadFragment(fragment, name);
            return true;
        });

        bottomNav.setSelectedItemId(R.id.nav_dashboard);
    }

    private void loadFragment(Fragment fragment, String fragmentName) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }

    private void setupBackPressHandler() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (backPressedTime + 2000 > System.currentTimeMillis()) finish();
                else Toast.makeText(MainActivity.this, "Press back again to exit", Toast.LENGTH_SHORT).show();
                backPressedTime = System.currentTimeMillis();
            }
        });
    }
}
