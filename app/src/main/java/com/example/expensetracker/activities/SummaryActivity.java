package com.example.expensetracker.activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.expensetracker.R;
import com.example.expensetracker.database.AppDatabase;
import com.example.expensetracker.models.CategorySum;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class SummaryActivity extends AppCompatActivity {

    private TextView txtSummaryBalance, txtSummaryIncome, txtSummaryExpense;
    private TextView txtIncomeCount, txtExpenseCount, txtMonthlyChange;
    private PieChart pieChart;

    private AppDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarSummary);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Financial Summary");
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
        pieChart = findViewById(R.id.pieChart);

        database = AppDatabase.getInstance(this);

        loadSummary();
        loadExpensePie();
    }

    private void loadSummary() {
        Executors.newSingleThreadExecutor().execute(() -> {

            double income = database.transactionDao().getTotalIncome();
            double expense = database.transactionDao().getTotalExpense();
            double balance = income - expense;

            int incomeCount = database.transactionDao().getIncomeCount();
            int expenseCount = database.transactionDao().getExpenseCount();

            double percent = income > 0 ? (balance / income) * 100 : 0;

            runOnUiThread(() -> {
                txtSummaryIncome.setText("$" + income);
                txtSummaryExpense.setText("$" + expense);
                txtSummaryBalance.setText("$" + balance);

                txtIncomeCount.setText(incomeCount + " transactions");
                txtExpenseCount.setText(expenseCount + " transactions");
                txtMonthlyChange.setText(String.format("+%.1f%%", percent));
            });
        });
    }

    private void loadExpensePie() {
        Executors.newSingleThreadExecutor().execute(() -> {

            List<CategorySum> list =
                    database.transactionDao().getExpenseByCategory();

            runOnUiThread(() -> {

                if (list == null || list.isEmpty()) {
                    pieChart.clear();
                    return;
                }

                double totalExpense = 0;
                for (CategorySum c : list) {
                    totalExpense += c.total;
                }

                ArrayList<PieEntry> entries = new ArrayList<>();

                for (CategorySum c : list) {
                    float percent =
                            (float) ((c.total / totalExpense) * 100);
                    entries.add(new PieEntry(percent, c.category));
                }

                PieDataSet dataSet = new PieDataSet(entries, "");
                dataSet.setColors(new int[]{
                        0xFFE45765, // Shopping
                        0xFFD65DB1, // Bills
                        0xFF9D5CFF, // Food
                        0xFF7B7DFF, // Transport
                        0xFF5E60CE  // Coffee
                });
                dataSet.setSliceSpace(2f);
                dataSet.setValueTextSize(14f);
                dataSet.setValueTextColor(android.graphics.Color.WHITE);

                PieData data = new PieData(dataSet);

                pieChart.setData(data);
                pieChart.setUsePercentValues(true);
                pieChart.setDrawHoleEnabled(false);
                pieChart.getDescription().setEnabled(false);
                pieChart.getLegend().setTextColor(android.graphics.Color.WHITE);
                pieChart.invalidate();
            });
        });
    }
}