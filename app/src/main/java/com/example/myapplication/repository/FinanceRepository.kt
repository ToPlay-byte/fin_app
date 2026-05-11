package com.example.myapplication.repository

import androidx.lifecycle.LiveData
import com.example.myapplication.data.dao.CategoryDao
import com.example.myapplication.data.dao.TransactionDao
import com.example.myapplication.data.entity.CategoryEntity
import com.example.myapplication.data.entity.TransactionEntity

class FinanceRepository(
    private val transactionDao: TransactionDao,
    private val categoryDao: CategoryDao
) {
    val allTransactions: LiveData<List<TransactionEntity>> = transactionDao.getAllTransactions()
    val totalIncome: LiveData<Double?> = transactionDao.getTotalIncome()
    val totalExpense: LiveData<Double?> = transactionDao.getTotalExpense()
    val allCategories: LiveData<List<CategoryEntity>> = categoryDao.getAllCategories()

    fun getTransactionsByType(type: String): LiveData<List<TransactionEntity>> =
        transactionDao.getTransactionsByType(type)

    fun getCategoriesByType(type: String): LiveData<List<CategoryEntity>> =
        categoryDao.getCategoriesByType(type)

    suspend fun insertTransaction(transaction: TransactionEntity) {
        transactionDao.insert(transaction)
    }

    suspend fun updateTransaction(transaction: TransactionEntity) {
        transactionDao.update(transaction)
    }

    suspend fun deleteTransaction(transaction: TransactionEntity) {
        transactionDao.delete(transaction)
    }

    suspend fun getTransactionById(id: Int): TransactionEntity? {
        return transactionDao.getTransactionById(id)
    }
}
