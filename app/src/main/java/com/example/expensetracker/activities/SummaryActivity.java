package com.example.expensetracker.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.expensetracker.R;
import com.example.expensetracker.database.AppDatabase;

import java.util.concurrent.Executors;

public class SummaryActivity extends AppCompatActivity {

    private TextView txtIncome, txtExpense, txtBalance;
    private AppDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        txtIncome = findViewById(R.id.txtTotalIncome);
        txtExpense = findViewById(R.id.txtTotalExpense);
        txtBalance = findViewById(R.id.txtBalance);

        database = AppDatabase.getInstance(this);

        loadSummary();
    }

    private void loadSummary() {
        Executors.newSingleThreadExecutor().execute(() -> {

            double income = database.transactionDao().getTotalIncome();
            double expense = database.transactionDao().getTotalExpense();
            double balance = income - expense;

            SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
            String currency = prefs.getString("currency", "$");

            runOnUiThread(() -> {
                txtIncome.setText("Total Income: " + currency + income);
                txtExpense.setText("Total Expense: " + currency + expense);
                txtBalance.setText("Balance: " + currency + balance);
            });
        });
    }
}