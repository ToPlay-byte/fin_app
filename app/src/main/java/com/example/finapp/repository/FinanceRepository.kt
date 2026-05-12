package com.example.finapp.repository

import androidx.lifecycle.LiveData
import com.example.finapp.data.dao.CategoryDao
import com.example.finapp.data.dao.PlanDao
import com.example.finapp.data.dao.TransactionDao
import com.example.finapp.data.entity.CategoryEntity
import com.example.finapp.data.entity.PlanEntity
import com.example.finapp.data.entity.TransactionEntity

class FinanceRepository(
    private val transactionDao: TransactionDao,
    private val categoryDao: CategoryDao,
    private val planDao: PlanDao
) {
    val allTransactions: LiveData<List<TransactionEntity>> = transactionDao.getAllTransactions()
    val totalIncome: LiveData<Double?> = transactionDao.getTotalIncome()
    val totalExpense: LiveData<Double?> = transactionDao.getTotalExpense()
    val allCategories: LiveData<List<CategoryEntity>> = categoryDao.getAllCategories()
    val allPlans: LiveData<List<PlanEntity>> = planDao.getAllPlans()

    fun getTransactionsByPeriod(start: Long, end: Long): LiveData<List<TransactionEntity>> =
        transactionDao.getTransactionsByPeriod(start, end)

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
    
    suspend fun insertPlan(plan: PlanEntity) {
        planDao.insert(plan)
    }

    suspend fun deletePlan(plan: PlanEntity) {
        planDao.delete(plan)
    }

    suspend fun clearAllData() {
        transactionDao.deleteAll()
        planDao.deleteAll()
        // We don't delete categories to keep them as templates
    }
}
