package com.example.pennywise.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

public class Expense {

    private String title;
    private double amount;
    private Timestamp createdAt;

    public Expense() {}   // Firestore needs empty constructor

    public Expense(String title, double amount, Timestamp createdAt) {
        this.title = title;
        this.amount = amount;
        this.createdAt = createdAt;
    }

    // Convert Firestore doc to Expense object
    public static Expense fromFirestore(DocumentSnapshot doc) {
        Expense e = new Expense();
        e.title = doc.getString("title");

        Double amt = doc.getDouble("amount");
        e.amount = (amt != null) ? amt : 0;

        e.createdAt = doc.getTimestamp("createdAt");
        return e;
    }

    // Getters
    public String getTitle() {
        return title;
    }

    public double getAmount() {
        return amount;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }
}
