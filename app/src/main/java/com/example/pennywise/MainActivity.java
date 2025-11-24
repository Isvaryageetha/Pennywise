package com.example.pennywise;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
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

    private GeofencingClient geofencingClient;

    // HashMap to store geofence coordinates
    private HashMap<String, double[]> locationMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        setupBottomNavigation();
        setupBackPressHandler();
        NotificationHelper.startNotificationService(this);

        geofencingClient = LocationServices.getGeofencingClient(this);

        initializeLocations();
        requestLocationPermissions();

        // Create notification channel for geofences
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Geofence Alerts";
            String description = "Notifications when entering or leaving geofences";
            int importance = android.app.NotificationManager.IMPORTANCE_HIGH;
            android.app.NotificationChannel channel = new android.app.NotificationChannel("geofence_channel", name, importance);
            channel.setDescription(description);
            android.app.NotificationManager notificationManager = getSystemService(android.app.NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    // Initialize location coordinates (can be home, college, shop, etc.)
    private void initializeLocations() {
        locationMap.put("Home", new double[]{11.955690, 79.823588});
        locationMap.put("College", new double[]{11.953369, 79.819440});
        locationMap.put("Shop", new double[]{11.896678, 79.803101});
        // Add more locations as needed
    }

    private void requestLocationPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED)) {

            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    }, 1001);
        } else {
            addGeofences();
        }
    }

    private void addGeofences() {
        for (String name : locationMap.keySet()) {
            double[] coords = locationMap.get(name);
            double lat = coords[0];
            double lng = coords[1];

            // Use lat,lng as requestId for reverse geocoding
            String requestId = lat + "," + lng;

            Geofence geofence = new Geofence.Builder()
                    .setRequestId(requestId)
                    .setCircularRegion(lat, lng, 100)
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build();

            GeofencingRequest request = new GeofencingRequest.Builder()
                    .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                    .addGeofence(geofence)
                    .build();

            PendingIntent geofencePendingIntent = PendingIntent.getBroadcast(
                    this, requestId.hashCode(), new Intent(this, GeofenceBroadcastReceiver.class),
                    PendingIntent.FLAG_UPDATE_CURRENT |
                            (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ? PendingIntent.FLAG_MUTABLE : 0)
            );

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Location permission not granted", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                geofencingClient.addGeofences(request, geofencePendingIntent)
                        .addOnSuccessListener(aVoid -> Toast.makeText(this, name + " geofence added!", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(this, "Failed to add geofence: " + e.getMessage(), Toast.LENGTH_LONG).show());
            } catch (SecurityException e) {
                e.printStackTrace();
                Toast.makeText(this, "SecurityException: Permission missing", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1001) {
            boolean granted = true;
            for (int result : grantResults) if (result != PackageManager.PERMISSION_GRANTED) granted = false;
            if (granted) addGeofences();
            else Toast.makeText(this, "Location permissions are required for geofencing", Toast.LENGTH_LONG).show();
        }
    }

    // ---------------- Expense, Bill, Savings, Balance ----------------
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

    // ---------------- Bottom Navigation ----------------
    private void initializeViews() {
        bottomNav = findViewById(R.id.bottom_navigation);
    }

    private void setupBottomNavigation() {
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Fragment fragment = null;

            if (itemId == R.id.nav_dashboard) fragment = new DashboardFragment();
            else if (itemId == R.id.nav_expenses) fragment = new ExpenseFragment();
            else if (itemId == R.id.nav_savings) fragment = new SavingsFragment();
            else if (itemId == R.id.nav_bills) fragment = new BillsFragment();
            else if (itemId == R.id.nav_analytics) fragment = new AnalyticsFragment();
            else if (itemId == R.id.nav_settings) fragment = new SettingsFragment();

            if (fragment != null) loadFragment(fragment);
            return true;
        });

        bottomNav.setSelectedItemId(R.id.nav_dashboard);
    }

    private void loadFragment(Fragment fragment) {
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
