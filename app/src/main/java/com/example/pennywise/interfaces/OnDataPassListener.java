package com.example.pennywise.interfaces;

public interface OnDataPassListener {
    void onExpenseAdded(String category, double amount, String date);
    void onBillAdded(String billName, double amount, boolean isPaid);
    void onSavingsGoalAdded(String purpose, double targetAmount);
    void onBalanceThresholdChanged(double newThreshold);
    void onDataUpdated(); // General update notification
}