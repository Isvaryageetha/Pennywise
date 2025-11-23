package com.example.pennywise.models;

import android.net.Uri;

public class Bill {
    private String name;
    private double amount;
    private boolean isPaid;
    private Uri imageUri;  // NEW

    public Bill(String name, double amount, boolean isPaid, Uri imageUri) {
        this.name = name;
        this.amount = amount;
        this.isPaid = isPaid;
        this.imageUri = imageUri;
    }
    public Bill(String name, double amount, boolean isPaid) {
        this(name, amount, isPaid, null);
    }

    public String getName() { return name; }
    public double getAmount() { return amount; }
    public boolean isPaid() { return isPaid; }
    public void setPaid(boolean paid) { isPaid = paid; }
    public Uri getImageUri() { return imageUri; }  // NEW
}
