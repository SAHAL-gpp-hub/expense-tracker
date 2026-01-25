package com.example.expensetracker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensetracker.R;
import com.example.expensetracker.adapters.TransactionAdapter;
import com.example.expensetracker.database.AppDatabase;
import com.example.expensetracker.models.TransactionEntity;
import com.example.expensetracker.utils.AddTransactionDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TransactionAdapter adapter;
    private AppDatabase database;

    // SUMMARY TEXTVIEWS
    private TextView txtTotalIncome, txtTotalExpense, txtBalance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Summary TextViews
        txtTotalIncome = findViewById(R.id.txtTotalIncome);
        txtTotalExpense = findViewById(R.id.txtTotalExpense);
        txtBalance = findViewById(R.id.txtBalance);

        // See All → SummaryActivity
        TextView seeAll = findViewById(R.id.txtSeeAll);
        seeAll.setOnClickListener(v ->
                startActivity(new Intent(this, SummaryActivity.class))
        );

        database = AppDatabase.getInstance(this);

        // RecyclerView
        recyclerView = findViewById(R.id.recyclerTransactions);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // FAB
        FloatingActionButton fab = findViewById(R.id.fabAdd);
        fab.setOnClickListener(v -> showAddDialog());

        // Initial load
        loadTransactions();
        loadSummary();
    }

    // MENU
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_summary) {
            startActivity(new Intent(this, SummaryActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // LOAD TRANSACTIONS
    private void loadTransactions() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<TransactionEntity> list =
                    database.transactionDao().getAllTransactions();

            runOnUiThread(() -> {
                adapter = new TransactionAdapter(
                        list,
                        database,
                        () -> {
                            loadTransactions();
                            loadSummary(); // 🔥 refresh summary after delete
                        }
                );
                recyclerView.setAdapter(adapter);
            });
        });
    }

    // LOAD SUMMARY (IMPORTANT)
    private void loadSummary() {
        Executors.newSingleThreadExecutor().execute(() -> {

            double income = database.transactionDao().getTotalIncome();
            double expense = database.transactionDao().getTotalExpense();
            double balance = income - expense;

            runOnUiThread(() -> {
                txtTotalIncome.setText("Total Income\n$" + income);
                txtTotalExpense.setText("Total Expense\n$" + expense);
                txtBalance.setText("Current Balance\n$" + balance);
            });
        });
    }

    // ADD TRANSACTION DIALOG
    private void showAddDialog() {
        AddTransactionDialog dialog =
                new AddTransactionDialog(
                        this,
                        database,
                        () -> {
                            loadTransactions();
                            loadSummary(); // 🔥 refresh summary after add
                        }
                );
        dialog.show();
    }
}