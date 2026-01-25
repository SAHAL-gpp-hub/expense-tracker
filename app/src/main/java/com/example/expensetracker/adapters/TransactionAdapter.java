package com.example.expensetracker.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensetracker.R;
import com.example.expensetracker.database.AppDatabase;
import com.example.expensetracker.models.TransactionEntity;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

public class TransactionAdapter
        extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

    private final List<TransactionEntity> list;
    private final AppDatabase database;
    private final Runnable refreshCallback;

    public TransactionAdapter(List<TransactionEntity> list,
                              AppDatabase database,
                              Runnable refreshCallback) {
        this.list = list;
        this.database = database;
        this.refreshCallback = refreshCallback;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtAmount, txtCategory, txtDate;
        ImageView imgCategory;

        ViewHolder(View view) {
            super(view);
            txtAmount = view.findViewById(R.id.txtAmount);
            txtCategory = view.findViewById(R.id.txtCategory);
            txtDate = view.findViewById(R.id.txtDate);
            imgCategory = view.findViewById(R.id.imgCategory); // ✅ FIXED ID
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transaction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        TransactionEntity t = list.get(position);

        holder.txtCategory.setText(t.getCategory());

        // Format date
        SimpleDateFormat sdf =
                new SimpleDateFormat("MMM d, yyyy", Locale.getDefault());
        holder.txtDate.setText(sdf.format(t.getDate()));

        // CATEGORY ICON (Material Icons)
        switch (t.getCategory()) {
            case "Food":
                holder.imgCategory.setImageResource(R.drawable.ic_food);
                break;
            case "Shopping":
                holder.imgCategory.setImageResource(R.drawable.ic_shopping);
                break;
            case "Transport":
                holder.imgCategory.setImageResource(R.drawable.ic_transport);
                break;
            case "Bills":
                holder.imgCategory.setImageResource(R.drawable.ic_bills);
                break;
            case "Salary":
            default:
                holder.imgCategory.setImageResource(R.drawable.ic_income_wallet);
                break;
        }

        // AMOUNT + COLOR
        if ("INCOME".equals(t.getType())) {
            holder.txtAmount.setText("+$" + t.getAmount());
            holder.txtAmount.setTextColor(Color.parseColor("#4ADE80"));
            holder.imgCategory.setColorFilter(Color.parseColor("#4ADE80"));
        } else {
            holder.txtAmount.setText("-$" + t.getAmount());
            holder.txtAmount.setTextColor(Color.parseColor("#FB7185"));
            holder.imgCategory.setColorFilter(Color.parseColor("#FB7185"));
        }

        // LONG PRESS → DELETE CONFIRMATION
        holder.itemView.setOnLongClickListener(v -> {
            new AlertDialog.Builder(v.getContext())
                    .setTitle("Delete Transaction")
                    .setMessage("Are you sure you want to delete this transaction?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        Executors.newSingleThreadExecutor().execute(() -> {
                            database.transactionDao().deleteTransaction(t);
                            refreshCallback.run();
                        });
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}