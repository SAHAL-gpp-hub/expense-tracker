package com.example.expensetracker.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.expensetracker.models.TransactionEntity;

import java.util.List;

@Dao
public interface TransactionDao {

    // Insert transaction
    @Insert
    void insertTransaction(TransactionEntity transaction);

    // Get all transactions ordered by date (latest first)
    @Query("SELECT * FROM transactions ORDER BY date DESC")
    List<TransactionEntity> getAllTransactions();

    // Calculate total income


    // Delete transaction
    @Delete
    void deleteTransaction(TransactionEntity transaction);

    @Query("SELECT IFNULL(SUM(amount),0) FROM transactions WHERE type='INCOME'")
    double getTotalIncome();

    @Query("SELECT IFNULL(SUM(amount),0) FROM transactions WHERE type='EXPENSE'")
    double getTotalExpense();

    @Query("SELECT COUNT(*) FROM transactions WHERE type = 'INCOME'")
    int getIncomeCount();

    @Query("SELECT COUNT(*) FROM transactions WHERE type = 'EXPENSE'")
    int getExpenseCount();


}