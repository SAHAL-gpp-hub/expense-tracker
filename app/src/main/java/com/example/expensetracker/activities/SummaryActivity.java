package com.example.expensetracker.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
    private LinearLayout categoryContainer;

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
        categoryContainer = findViewById(R.id.categoryContainer);

        database = AppDatabase.getInstance(this);

        loadSummary();
        loadExpensePie();
        loadCategoryBreakdown(); // ✅ CALL ONCE
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
                for (CategorySum c : list) totalExpense += c.total;

                ArrayList<PieEntry> entries = new ArrayList<>();
                for (CategorySum c : list) {
                    float percent = (float) ((c.total / totalExpense) * 100);
                    entries.add(new PieEntry(percent, c.category));
                }

                PieDataSet dataSet = new PieDataSet(entries, "");
                dataSet.setColors(new int[]{
                        0xFFE45765,
                        0xFFD65DB1,
                        0xFF9D5CFF,
                        0xFF7B7DFF,
                        0xFF5E60CE
                });
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

    private void loadCategoryBreakdown() {
        Executors.newSingleThreadExecutor().execute(() -> {

            List<CategorySum> list =
                    database.transactionDao().getExpenseByCategory();
            double totalExpense =
                    database.transactionDao().getTotalExpense();

            runOnUiThread(() -> {
                categoryContainer.removeAllViews();

                int[] colors = {
                        0xFFE45765,
                        0xFFD65DB1,
                        0xFF9D5CFF,
                        0xFF7B7DFF,
                        0xFF5E60CE
                };

                int index = 0;

                for (CategorySum item : list) {

                    View row = getLayoutInflater()
                            .inflate(R.layout.item_category_progress,
                                    categoryContainer,
                                    false);

                    TextView txtCategory = row.findViewById(R.id.txtCategory);
                    TextView txtAmount = row.findViewById(R.id.txtAmount);
                    TextView txtPercent = row.findViewById(R.id.txtPercent);
                    ProgressBar progress = row.findViewById(R.id.progress);
                    View dot = row.findViewById(R.id.dot);

                    double percent = totalExpense == 0
                            ? 0
                            : (item.total / totalExpense) * 100;

                    txtCategory.setText(item.category);
                    txtAmount.setText("$" + (int) item.total);
                    txtPercent.setText(
                            String.format("%.1f%% of total expenses", percent)
                    );

                    progress.setProgress((int) percent);

                    int color = colors[index % colors.length];
                    progress.getProgressDrawable().setTint(color);
                    dot.getBackground().setTint(color);

                    categoryContainer.addView(row);
                    index++;
                }
            });
        });
    }
}