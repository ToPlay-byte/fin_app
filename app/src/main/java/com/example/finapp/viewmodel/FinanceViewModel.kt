package com.example.finapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.finapp.FinanceApplication
import com.example.finapp.data.entity.CategoryEntity
import com.example.finapp.data.entity.PlanEntity
import com.example.finapp.data.entity.TransactionEntity
import com.example.finapp.repository.FinanceRepository
import kotlinx.coroutines.launch

class FinanceViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: FinanceRepository = (application as FinanceApplication).repository
    
    val allTransactions: LiveData<List<TransactionEntity>> = repository.allTransactions
    val totalIncome: LiveData<Double?> = repository.totalIncome
    val totalExpense: LiveData<Double?> = repository.totalExpense
    val allCategories: LiveData<List<CategoryEntity>> = repository.allCategories
    val allPlans: LiveData<List<PlanEntity>> = repository.allPlans

    fun getTransactionsByPeriod(start: Long, end: Long): LiveData<List<TransactionEntity>> =
        repository.getTransactionsByPeriod(start, end)

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
    
    fun insertPlan(plan: PlanEntity) = viewModelScope.launch {
        repository.insertPlan(plan)
    }

    fun deletePlan(plan: PlanEntity) = viewModelScope.launch {
        repository.deletePlan(plan)
    }

    fun clearAllData() = viewModelScope.launch {
        repository.clearAllData()
    }
}
