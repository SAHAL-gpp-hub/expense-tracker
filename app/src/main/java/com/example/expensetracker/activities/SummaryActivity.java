package com.example.expensetracker.activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.expensetracker.R;
import com.example.expensetracker.database.AppDatabase;

import java.util.concurrent.Executors;

public class SummaryActivity extends AppCompatActivity {

    private TextView txtSummaryBalance, txtSummaryIncome, txtSummaryExpense;
    private TextView txtIncomeCount, txtExpenseCount, txtMonthlyChange;

    private AppDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarSummary);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        toolbar.setNavigationOnClickListener(v -> finish());

        // Bind views
        txtSummaryBalance = findViewById(R.id.txtSummaryBalance);
        txtSummaryIncome = findViewById(R.id.txtSummaryIncome);
        txtSummaryExpense = findViewById(R.id.txtSummaryExpense);
        txtIncomeCount = findViewById(R.id.txtIncomeCount);
        txtExpenseCount = findViewById(R.id.txtExpenseCount);
        txtMonthlyChange = findViewById(R.id.txtMonthlyChange);

        database = AppDatabase.getInstance(this);

        loadSummary();
    }

    private void loadSummary() {
        Executors.newSingleThreadExecutor().execute(() -> {

            double income = database.transactionDao().getTotalIncome();
            double expense = database.transactionDao().getTotalExpense();
            double balance = income - expense;

            int incomeCount = database.transactionDao().getIncomeCount();
            int expenseCount = database.transactionDao().getExpenseCount();

            // Percentage calculation
            double percent = income > 0 ? (balance / income) * 100 : 0;

            runOnUiThread(() -> {
                txtSummaryIncome.setText("$" + income);
                txtSummaryExpense.setText("$" + expense);
                txtSummaryBalance.setText("$" + balance);

                txtIncomeCount.setText(incomeCount + " transactions");
                txtExpenseCount.setText(expenseCount + " transactions");

                txtMonthlyChange.setText(
                        String.format("+%.1f%%", percent)
                );
            });
        });
    }
}