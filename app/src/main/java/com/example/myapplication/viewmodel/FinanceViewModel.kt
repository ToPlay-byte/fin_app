package com.example.myapplication.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.database.AppDatabase
import com.example.myapplication.data.entity.CategoryEntity
import com.example.myapplication.data.entity.TransactionEntity
import com.example.myapplication.repository.FinanceRepository
import kotlinx.coroutines.launch

class FinanceViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: FinanceRepository
    val allTransactions: LiveData<List<TransactionEntity>>
    val totalIncome: LiveData<Double?>
    val totalExpense: LiveData<Double?>

    init {
        val db = AppDatabase.getDatabase(application)
        repository = FinanceRepository(db.transactionDao(), db.categoryDao())
        allTransactions = repository.allTransactions
        totalIncome = repository.totalIncome
        totalExpense = repository.totalExpense
    }

    fun getTransactionsByType(type: String): LiveData<List<TransactionEntity>> =
        repository.getTransactionsByType(type)

    fun getCategoriesByType(type: String): LiveData<List<CategoryEntity>> =
        repository.getCategoriesByType(type)

    fun insertTransaction(transaction: TransactionEntity) = viewModelScope.launch {
        repository.insertTransaction(transaction)
    }

    fun updateTransaction(transaction: TransactionEntity) = viewModelScope.launch {
        repository.updateTransaction(transaction)
    }

    fun deleteTransaction(transaction: TransactionEntity) = viewModelScope.launch {
        repository.deleteTransaction(transaction)
    }

    suspend fun getTransactionById(id: Int): TransactionEntity? {
        return repository.getTransactionById(id)
    }
}
