package com.example.expensetracker.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.expensetracker.models.TransactionEntity;
import com.example.expensetracker.models.CategorySum;

import java.util.List;

@Dao
public interface TransactionDao {

    // Insert transaction
    @Insert
    void insertTransaction(TransactionEntity transaction);

    // Get all transactions ordered by date (latest first)
    @Query("SELECT * FROM transactions ORDER BY date DESC")
    List<TransactionEntity> getAllTransactions();

    // Delete transaction
    @Delete
    void deleteTransaction(TransactionEntity transaction);

    // Total income
    @Query("SELECT IFNULL(SUM(amount), 0) FROM transactions WHERE type = 'INCOME'")
    double getTotalIncome();

    // Total expense
    @Query("SELECT IFNULL(SUM(amount), 0) FROM transactions WHERE type = 'EXPENSE'")
    double getTotalExpense();

    // Income count
    @Query("SELECT COUNT(*) FROM transactions WHERE type = 'INCOME'")
    int getIncomeCount();

    // Expense count
    @Query("SELECT COUNT(*) FROM transactions WHERE type = 'EXPENSE'")
    int getExpenseCount();

    // Expense grouped by category (for pie chart)
    @Query(
            "SELECT category, SUM(amount) AS total " +
                    "FROM transactions " +
                    "WHERE type = 'EXPENSE' " +
                    "GROUP BY category"
    )
    List<CategorySum> getExpenseByCategory();
}