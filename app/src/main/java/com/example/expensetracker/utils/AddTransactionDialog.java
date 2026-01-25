package com.example.expensetracker.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.expensetracker.R;
import com.example.expensetracker.database.AppDatabase;
import com.example.expensetracker.models.TransactionEntity;

import java.util.concurrent.Executors;

public class AddTransactionDialog extends Dialog {

    public AddTransactionDialog(Context context,
                                AppDatabase database,
                                Runnable refreshCallback) {
        super(context);
        setContentView(R.layout.dialog_add_transaction);

        // 🔥 FIX: FORCE DIALOG WIDTH (MOST IMPORTANT)
        if (getWindow() != null) {
            getWindow().setLayout(
                    (int) (context.getResources().getDisplayMetrics().widthPixels * 0.95),
                    WindowManager.LayoutParams.WRAP_CONTENT
            );
        }

        // Views
        EditText edtAmount = findViewById(R.id.edtAmount);
        Spinner spinner = findViewById(R.id.spinnerCategory);
        RadioButton radioIncome = findViewById(R.id.radioIncome);
        RadioButton radioExpense = findViewById(R.id.radioExpense);
        Button btnSave = findViewById(R.id.btnSave);
        Button btnCancel = findViewById(R.id.btnCancel);

        // Spinner adapter
        ArrayAdapter<CharSequence> adapter =
                ArrayAdapter.createFromResource(
                        context,
                        R.array.categories,
                        android.R.layout.simple_spinner_item
                );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        btnCancel.setOnClickListener(v -> dismiss());

        btnSave.setOnClickListener(v -> {

            String amountText = edtAmount.getText().toString().trim();

            if (amountText.isEmpty()) {
                Toast.makeText(context, "Enter amount", Toast.LENGTH_SHORT).show();
                return;
            }

            double amount = Double.parseDouble(amountText);
            if (amount <= 0) {
                Toast.makeText(context, "Amount must be positive", Toast.LENGTH_SHORT).show();
                return;
            }

            if (spinner.getSelectedItem() == null) {
                Toast.makeText(context, "Select category", Toast.LENGTH_SHORT).show();
                return;
            }

            String category = spinner.getSelectedItem().toString();
            String type = radioIncome.isChecked() ? "INCOME" : "EXPENSE";

            TransactionEntity transaction =
                    new TransactionEntity(
                            amount,
                            category,
                            type,
                            System.currentTimeMillis()
                    );

            Executors.newSingleThreadExecutor().execute(() -> {
                database.transactionDao().insertTransaction(transaction);
                refreshCallback.run();
            });

            dismiss();
        });
    }
}