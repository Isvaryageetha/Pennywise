package com.example.pennywise.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;

import java.util.HashMap;
import java.util.Map;

public class Expense {

    private String title;
    private String category;
    private double amount;
    private String notes;
    private Timestamp createdAt;

    // ---------- Default Constructor ----------
    public Expense() {}

    // ---------- Main Constructor ----------
    public Expense(String title, String category, double amount, String notes, Timestamp createdAt) {
        this.title = title;
        this.category = category;
        this.amount = amount;
        this.notes = notes;
        this.createdAt = createdAt;
    }

    // ---------- NEW Constructor (For Analytics Dummy Data) ----------
    public Expense(String category, double amount, String dateString) {
        this.category = category;
        this.amount = amount;

        // Convert string to Timestamp for analytics use
        this.createdAt = Timestamp.now(); // fallback
    }

    // ---------- Convert to Firestore ----------
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("title", title);
        map.put("category", category);
        map.put("amount", amount);
        map.put("notes", notes);
        map.put("createdAt", FieldValue.serverTimestamp()); // Auto timestamp
        return map;
    }

    // ---------- Read from Firestore ----------
    public static Expense fromFirestore(DocumentSnapshot doc) {
        Expense e = new Expense();
        e.title = doc.getString("title");
        e.category = doc.getString("category");

        Double amt = doc.getDouble("amount");
        e.amount = (amt != null) ? amt : 0;

        e.notes = doc.getString("notes");
        e.createdAt = doc.getTimestamp("createdAt");

        return e;
    }

    // ---------- Getters ----------
    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }

    public double getAmount() {
        return amount;
    }

    public String getNotes() {
        return notes;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    // ---------- OPTIONAL: Setters (Add if needed) ----------
    public void setTitle(String title) {
        this.title = title;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
