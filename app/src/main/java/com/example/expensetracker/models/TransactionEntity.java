package com.example.expensetracker.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "transactions")
public class TransactionEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private double amount;
    private String category;
    private String type;   // "INCOME" or "EXPENSE"
    private long date;     // timestamp

    // Constructor
    public TransactionEntity(double amount, String category, String type, long date) {
        this.amount = amount;
        this.category = category;
        this.type = type;
        this.date = date;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }

    public String getType() {
        return type;
    }

    public long getDate() {
        return date;
    }
}