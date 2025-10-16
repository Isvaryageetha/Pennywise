package com.example.pennywise.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import com.example.pennywise.workers.NotificationWorker;
import java.util.concurrent.TimeUnit;

public class NotificationHelper {
    private static final String TAG = "NotificationHelper";
    private static final String PREFS_NAME = "PennywisePrefs";
    private static final String KEY_NOTIFICATION_ENABLED = "notifications_enabled";
    private static final String WORK_TAG = "pennywise_notification_work";

    public static void startNotificationService(Context context) {
        if (!isNotificationEnabled(context)) {
            Log.d(TAG, "Notifications are disabled");
            return;
        }

        try {
            // Create constraints (optional - you can remove this if you want)
            Constraints constraints = new Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                    .setRequiresCharging(false)
                    .setRequiresBatteryNotLow(true)
                    .build();

            // Create periodic work request - using TimeUnit instead of java.time.Duration
            PeriodicWorkRequest notificationWork =
                    new PeriodicWorkRequest.Builder(NotificationWorker.class,
                            6, TimeUnit.HOURS, // Repeat every 6 hours
                            15, TimeUnit.MINUTES) // Flexible interval
                            .setConstraints(constraints)
                            .build();

            // Enqueue the work with unique name (replace existing if any)
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                    WORK_TAG,
                    ExistingPeriodicWorkPolicy.REPLACE,
                    notificationWork
            );

            Log.d(TAG, "Notification work scheduled every 6 hours");

        } catch (Exception e) {
            Log.e(TAG, "Error scheduling notification work: " + e.getMessage(), e);
        }
    }

    public static void stopNotificationService(Context context) {
        try {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_TAG);
            Log.d(TAG, "Notification work cancelled");
        } catch (Exception e) {
            Log.e(TAG, "Error stopping notification service: " + e.getMessage(), e);
        }
    }

    public static void setNotificationEnabled(Context context, boolean enabled) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(KEY_NOTIFICATION_ENABLED, enabled).apply();

        if (enabled) {
            startNotificationService(context);
        } else {
            stopNotificationService(context);
        }
    }

    public static boolean isNotificationEnabled(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_NOTIFICATION_ENABLED, true);
    }
}