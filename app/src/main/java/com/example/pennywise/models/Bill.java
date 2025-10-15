package com.example.pennywise.models;

public class Bill {
    private String name;
    private double amount;
    private boolean isPaid;

    public Bill(String name, double amount, boolean isPaid) {
        this.name = name;
        this.amount = amount;
        this.isPaid = isPaid;
    }

    public String getName() { return name; }
    public double getAmount() { return amount; }
    public boolean isPaid() { return isPaid; }
    public void setPaid(boolean paid) { isPaid = paid; }
}
