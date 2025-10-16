package com.example.pennywise.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class NotificationHelper {
    private static final String TAG = "NotificationHelper";
    private static final String PREFS_NAME = "PennywisePrefs";
    private static final String KEY_NOTIFICATION_ENABLED = "notifications_enabled";

    public static void startNotificationService(Context context) {
        if (!isNotificationEnabled(context)) {
            Log.d(TAG, "Notifications are disabled");
            return;
        }

        try {
            Intent serviceIntent = new Intent(context, NotificationService.class);
            context.startService(serviceIntent);
            Log.d(TAG, "Notification service started");
        } catch (Exception e) {
            Log.e(TAG, "Error starting notification service: " + e.getMessage());
        }
    }

    public static void stopNotificationService(Context context) {
        try {
            Intent serviceIntent = new Intent(context, NotificationService.class);
            context.stopService(serviceIntent);
            Log.d(TAG, "Notification service stopped");
        } catch (Exception e) {
            Log.e(TAG, "Error stopping notification service: " + e.getMessage());
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