package com.example.expensetracker.activities;
import com.example.expensetracker.utils.AddTransactionDialog;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensetracker.R;
import com.example.expensetracker.adapters.TransactionAdapter;
import com.example.expensetracker.database.AppDatabase;
import com.example.expensetracker.models.TransactionEntity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.concurrent.Executors;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TransactionAdapter adapter;
    private AppDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        database = AppDatabase.getInstance(this);

        recyclerView = findViewById(R.id.recyclerTransactions);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FloatingActionButton fab = findViewById(R.id.fabAdd);
        fab.setOnClickListener(v -> showAddDialog());

        loadTransactions();
    }
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
    private void loadTransactions() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<TransactionEntity> list =
                    database.transactionDao().getAllTransactions();

            runOnUiThread(() -> {
                adapter = new TransactionAdapter(list, database, this::loadTransactions);
                recyclerView.setAdapter(adapter);
            });
        });
    }

    private void showAddDialog() {
        AddTransactionDialog dialog = new AddTransactionDialog(this, database, this::loadTransactions);
        dialog.show();
    }
}