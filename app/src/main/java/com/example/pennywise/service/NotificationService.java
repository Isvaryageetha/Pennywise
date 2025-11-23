package com.example.pennywise.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import com.example.pennywise.MainActivity;
import com.example.pennywise.R;
import com.example.pennywise.models.Bill;
import com.example.pennywise.models.Expense;
import com.example.pennywise.models.SavingsGoal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationService extends Service {
    private static final String TAG = "NotificationService";
    private static final String CHANNEL_ID = "pennywise_reminders";
    private static final int NOTIFICATION_ID = 1;

    private SharedPreferences sharedPreferences;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "NotificationService created");
        sharedPreferences = getSharedPreferences("PennywisePrefs", Context.MODE_PRIVATE);
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "NotificationService started");

        showOngoingNotification();
        checkAndSendReminders();

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Pennywise Reminders",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("Balance and savings reminders");
            channel.setShowBadge(false);

            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    private void showOngoingNotification() {
        Intent intent = new Intent(this, MainActivity.class);
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags |= PendingIntent.FLAG_IMMUTABLE;
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, flags);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Pennywise")
                .setContentText("Tracking your finances")
                .setSmallIcon(R.drawable.ic_attach_money)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .setAutoCancel(false)
                .setPriority(NotificationCompat.PRIORITY_LOW) // Set low priority
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .build();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
        {
            startForeground(NOTIFICATION_ID, notification);
        } else
        {
            startForeground(NOTIFICATION_ID, notification);
        }
    }

    private void checkAndSendReminders() {
        if (!isNotificationEnabled()) {
            Log.d(TAG, "Notifications are disabled");
            return;
        }

        if (!shouldSendNotificationToday()) {
            Log.d(TAG, "Already sent notification today");
            return;
        }

        FinancialData data = getFinancialData();
        checkLowBalance(data);
        checkSavingsProgress(data);
        checkUnpaidBills(data);
        checkDailySpending(data);
        markNotificationSent();
    }
    private FinancialData getFinancialData() {
        FinancialData data = new FinancialData();
        data.expenses = new ArrayList<>();
        data.expenses.add(new Expense("Food", 150f, new com.google.firebase.Timestamp(new Date())));
        data.expenses.add(new Expense("Transport", 80f, new com.google.firebase.Timestamp(new Date())));

        data.savings = new ArrayList<>();
        data.savings.add(new SavingsGoal("New Phone", 500f, 200f));

        data.bills = new ArrayList<>();
        data.bills.add(new Bill("Electricity", 75.0, false, null));

        data.balance = 1200.0;
        data.threshold = 1000.0;
        return data;
    }


    private void checkLowBalance(FinancialData data) {
        double remaining = calculateRemainingBalance(data);
        if (remaining < data.threshold) {
            String message = String.format(Locale.getDefault(), "Low balance alert! You have $%.2f remaining", remaining);
            sendNotification("💰 Low Balance", message);
        }
    }

    private void checkSavingsProgress(FinancialData data) {
        for (SavingsGoal goal : data.savings) {
            if (goal.getTargetAmount() > 0) {
                float progress = (goal.getSavedAmount() / goal.getTargetAmount()) * 100;
                if (progress >= 50 && progress < 100) {
                    String message = String.format(Locale.getDefault(), "You're %d%% towards your %s goal!", (int)progress, goal.getPurpose());
                    sendNotification("🎯 Savings Progress", message);
                } else if (progress >= 100) {
                    String message = String.format("Congratulations! You reached your %s goal! 🎉", goal.getPurpose());
                    sendNotification("🎉 Goal Achieved", message);
                }
            }
        }
    }

    private void checkUnpaidBills(FinancialData data) {
        int unpaidCount = 0;
        double totalUnpaid = 0;
        for (Bill bill : data.bills) {
            if (!bill.isPaid()) {
                unpaidCount++;
                totalUnpaid += bill.getAmount();
            }
        }
        if (unpaidCount > 0) {
            String message = String.format(Locale.getDefault(), "You have %d unpaid bills totaling $%.2f", unpaidCount, totalUnpaid);
            sendNotification("📅 Unpaid Bills", message);
        }
    }

    private void checkDailySpending(FinancialData data) {

        double dailyTotal = 0;
        String today = getCurrentDate();   // format: yyyy-MM-dd

        for (Expense expense : data.expenses) {

            if (expense.getCreatedAt() != null) {

                // Convert timestamp to yyyy-MM-dd
                String expenseDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        .format(expense.getCreatedAt().toDate());

                if (today.equals(expenseDate)) {
                    dailyTotal += expense.getAmount();
                }
            }
        }

        if (dailyTotal > 100) {
            String msg = String.format(Locale.getDefault(),
                    "You've spent ₹%.2f today", dailyTotal);
            sendNotification("💸 Daily Spending Alert", msg);
        }
    }


    private double calculateRemainingBalance(FinancialData data) {
        double totalExpenses = 0;
        for (Expense e : data.expenses) totalExpenses += e.getAmount();

        double totalSavings = 0;
        for (SavingsGoal s : data.savings) totalSavings += s.getSavedAmount();

        double totalBills = 0;
        for (Bill b : data.bills) if (!b.isPaid()) totalBills += b.getAmount();

        return data.balance - (totalExpenses + totalSavings + totalBills);
    }

    private void sendNotification(String title, String message) {
        Intent intent = new Intent(this, MainActivity.class);


        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags |= PendingIntent.FLAG_IMMUTABLE;
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, flags);

        String reminderChannelId = "pennywise_reminder_notifications";
        createReminderChannel(reminderChannelId);

        Notification notification = new NotificationCompat.Builder(this, reminderChannelId)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_attach_money)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build();

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify((int) System.currentTimeMillis(), notification);
        }

        Log.d(TAG, "Notification sent: " + title + " - " + message);
    }

    private void createReminderChannel(String channelId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Pennywise Alerts",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Balance alerts and savings reminders");
            channel.setShowBadge(true);

            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    private boolean isNotificationEnabled() {
        return sharedPreferences.getBoolean("notifications_enabled", true);
    }

    private boolean shouldSendNotificationToday() {
        String lastDate = sharedPreferences.getString("last_notification_date", "");
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        return !lastDate.equals(today);
    }

    private void markNotificationSent() {
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        sharedPreferences.edit().putString("last_notification_date", today).apply();
    }

    private String getCurrentDate() {
        return new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
    }

    private static class FinancialData {
        List<Expense> expenses;
        List<SavingsGoal> savings;
        List<Bill> bills;
        double balance;
        double threshold;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "NotificationService destroyed");
    }
}