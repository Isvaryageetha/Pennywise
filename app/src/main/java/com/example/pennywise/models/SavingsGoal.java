package com.example.pennywise.models;

public class SavingsGoal {
    private String purpose;      // goal name (e.g., "New Phone")
    private float targetAmount;  // total goal
    private float savedAmount;   // amount actually saved

    // Constructor now stores purpose too
    public SavingsGoal(String purpose, float targetAmount, float savedAmount) {
        this.purpose = purpose;
        this.targetAmount = targetAmount;
        this.savedAmount = savedAmount;
    }

    public String getPurpose() {
        return purpose;
    }

    public float getTargetAmount() {
        return targetAmount;
    }

    public float getSavedAmount() {
        return savedAmount;
    }

    public void addToSavedAmount(float amount) {
        this.savedAmount += amount;
    }

    // kept for compatibility — returns current saved amount
    public float getAmount() {
        return savedAmount;
    }
}
