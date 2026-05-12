package com.example.finapp.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.finapp.data.entity.TransactionEntity

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAllTransactions(): LiveData<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE date BETWEEN :start AND :end ORDER BY date DESC")
    fun getTransactionsByPeriod(start: Long, end: Long): LiveData<List<TransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: TransactionEntity)

    @Update
    suspend fun update(transaction: TransactionEntity)

    @Delete
    suspend fun delete(transaction: TransactionEntity)

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getTransactionById(id: Int): TransactionEntity?

    @Query("SELECT SUM(amountInBaseCurrency) FROM transactions WHERE type = 'INCOME'")
    fun getTotalIncome(): LiveData<Double?>

    @Query("SELECT SUM(amountInBaseCurrency) FROM transactions WHERE type = 'EXPENSE'")
    fun getTotalExpense(): LiveData<Double?>
    
    @Query("DELETE FROM transactions")
    suspend fun deleteAll()
}
