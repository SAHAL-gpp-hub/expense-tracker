package com.example.expensetracker.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
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
        ImageView imgType;

        ViewHolder(View view) {
            super(view);
            txtAmount = view.findViewById(R.id.txtAmount);
            txtCategory = view.findViewById(R.id.txtCategory);
            txtDate = view.findViewById(R.id.txtDate);
            imgType = view.findViewById(R.id.imgType);
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
        holder.txtAmount.setText("$" + t.getAmount());

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        holder.txtDate.setText(sdf.format(t.getDate()));

        if ("INCOME".equals(t.getType())) {
            holder.imgType.setColorFilter(Color.GREEN);
        } else {
            holder.imgType.setColorFilter(Color.RED);
        }

        // Delete on long press
        holder.itemView.setOnLongClickListener(v -> {
            Executors.newSingleThreadExecutor().execute(() -> {
                database.transactionDao().deleteTransaction(t);
                refreshCallback.run();
            });
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}