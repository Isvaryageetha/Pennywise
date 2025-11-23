package com.example.pennywise.workers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.example.pennywise.MainActivity;
import com.example.pennywise.R;
import com.example.pennywise.models.Bill;
import com.example.pennywise.models.Expense;
import com.example.pennywise.models.SavingsGoal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationWorker extends Worker {
    private static final String TAG = "NotificationWorker";
    private SharedPreferences sharedPreferences;
    private Context context;

    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences("PennywisePrefs", Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "NotificationWorker running");

        if (!isNotificationEnabled()) {
            Log.d(TAG, "Notifications are disabled");
            return Result.success();
        }

        if (!shouldSendNotificationToday()) {
            Log.d(TAG, "Already sent notification today");
            return Result.success();
        }

        checkAndSendReminders();
        markNotificationSent();

        return Result.success();
    }

    private void checkAndSendReminders() {
        FinancialData data = getFinancialData();
        checkLowBalance(data);
        checkSavingsProgress(data);
        checkUnpaidBills(data);
        checkDailySpending(data);
    }

    private FinancialData getFinancialData() {
        FinancialData data = new FinancialData();

        // ----------------------------
        // Dummy Expenses with Timestamps
        // ----------------------------
        data.expenses = new ArrayList<>();
        data.expenses.add(new Expense("Food", 150f, new com.google.firebase.Timestamp(new Date())));
        data.expenses.add(new Expense("Transport", 80f, new com.google.firebase.Timestamp(new Date())));
        data.expenses.add(new Expense("Books", 120f, new com.google.firebase.Timestamp(new Date())));

        // ----------------------------
        // Dummy Savings Goals
        // ----------------------------
        data.savings = new ArrayList<>();
        data.savings.add(new SavingsGoal("New Phone", 500f, 200f));
        data.savings.add(new SavingsGoal("Laptop", 1000f, 100f));

        // ----------------------------
        // Dummy Bills
        // ----------------------------
        data.bills = new ArrayList<>();
        data.bills.add(new Bill("Electricity", 75.0, false));
        data.bills.add(new Bill("Internet", 50.0, false));

        // ----------------------------
        // Balance & Threshold
        // ----------------------------
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
        createNotificationChannel();

        Intent intent = new Intent(context, MainActivity.class);

        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags |= PendingIntent.FLAG_IMMUTABLE;
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, flags);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "pennywise_reminders")
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_attach_money)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify((int) System.currentTimeMillis(), builder.build());
        }

        Log.d(TAG, "Notification sent: " + title + " - " + message);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "pennywise_reminders",
                    "Pennywise Reminders",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Balance and savings reminders");

            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
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

    // Data holder class
    private static class FinancialData {
        List<Expense> expenses;
        List<SavingsGoal> savings;
        List<Bill> bills;
        double balance;
        double threshold;
    }
}