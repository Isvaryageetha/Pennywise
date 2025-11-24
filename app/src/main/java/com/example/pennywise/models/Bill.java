package com.example.pennywise.models;

import android.net.Uri;

public class Bill {

    private int id;
    private String name;
    private double amount;
    private boolean isPaid;
    private Uri imageUri;

    // FULL constructor including ID (used when reading from DB)
    public Bill(int id, String name, double amount, boolean isPaid, Uri imageUri) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.isPaid = isPaid;
        this.imageUri = imageUri;
    }

    // Constructor used when inserting (ID auto-generated)
    public Bill(String name, double amount, boolean isPaid, Uri imageUri) {
        this.name = name;
        this.amount = amount;
        this.isPaid = isPaid;
        this.imageUri = imageUri;
    }

    // ---------------- GETTERS ----------------
    public int getId() { return id; }
    public String getName() { return name; }
    public double getAmount() { return amount; }
    public boolean isPaid() { return isPaid; }
    public Uri getImageUri() { return imageUri; }

    // ---------------- SETTERS ----------------
    public void setPaid(boolean paid) { this.isPaid = paid; }
    public void setId(int id) { this.id = id; }
}
