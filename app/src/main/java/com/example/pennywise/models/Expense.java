package com.example.pennywise.models;

public class Expense {
    private String name;     // e.g., "Food"
    private float amount;    // e.g., 150.0
    private String date;     // e.g., "15/10/2025"

    // Constructor
    public Expense(String name, float amount, String date) {
        this.name = name;
        this.amount = amount;
        this.date = date;
    }

    // Getters
    public String getName() { return name; }
    public float getAmount() { return amount; }
    public String getDate() { return date; }
}
