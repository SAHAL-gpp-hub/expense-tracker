package com.example.expensetracker.utils;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.expensetracker.R;
import com.example.expensetracker.database.AppDatabase;
import com.example.expensetracker.models.TransactionEntity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.Executors;

public class AddTransactionDialog extends Dialog {

    private long selectedDateMillis;

    public AddTransactionDialog(Context context,
                                AppDatabase database,
                                Runnable refreshCallback) {

        super(context);
        setContentView(R.layout.dialog_add_transaction);

        if (getWindow() != null) {
            getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            // Optional: control width
            getWindow().setLayout(
                    (int) (context.getResources().getDisplayMetrics().widthPixels * 0.95),
                    WindowManager.LayoutParams.WRAP_CONTENT
            );
        }

        /* 🔥 FORCE DIALOG WIDTH */
        if (getWindow() != null) {
            getWindow().setLayout(
                    (int) (context.getResources().getDisplayMetrics().widthPixels * 0.95),
                    WindowManager.LayoutParams.WRAP_CONTENT
            );
        }

        /* VIEWS */
        EditText edtAmount = findViewById(R.id.edtAmount);
        Spinner spinnerCategory = findViewById(R.id.spinnerCategory);
        RadioButton radioIncome = findViewById(R.id.radioIncome);
        RadioButton radioExpense = findViewById(R.id.radioExpense);
        TextView txtDate = findViewById(R.id.txtDate);
        Button btnSave = findViewById(R.id.btnSave);
        Button btnCancel = findViewById(R.id.btnCancel);

        /* ------------------ SPINNER FIX ------------------ */
        ArrayAdapter<CharSequence> adapter =
                ArrayAdapter.createFromResource(
                        context,
                        R.array.categories,
                        R.layout.spinner_item          // ✅ custom text color
                );
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        /* ------------------ DATE LOGIC ------------------ */
        Calendar calendar = Calendar.getInstance();
        selectedDateMillis = calendar.getTimeInMillis();

        SimpleDateFormat sdf =
                new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
        txtDate.setText(sdf.format(calendar.getTime()));

        txtDate.setOnClickListener(v -> {
            DatePickerDialog dialog = new DatePickerDialog(
                    context,
                    (view, year, month, dayOfMonth) -> {
                        calendar.set(year, month, dayOfMonth);
                        selectedDateMillis = calendar.getTimeInMillis();
                        txtDate.setText(sdf.format(calendar.getTime()));
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            dialog.show();
        });

        /* ------------------ BUTTONS ------------------ */
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

            if (spinnerCategory.getSelectedItem() == null) {
                Toast.makeText(context, "Select category", Toast.LENGTH_SHORT).show();
                return;
            }

            String category = spinnerCategory.getSelectedItem().toString();
            String type = radioIncome.isChecked() ? "INCOME" : "EXPENSE";

            TransactionEntity transaction =
                    new TransactionEntity(
                            amount,
                            category,
                            type,
                            selectedDateMillis      // ✅ selected date
                    );

            Executors.newSingleThreadExecutor().execute(() -> {
                database.transactionDao().insertTransaction(transaction);
                refreshCallback.run();
            });

            dismiss();
        });
    }
}