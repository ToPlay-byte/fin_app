package com.example.finapp

import android.app.Application
import com.example.finapp.data.database.AppDatabase
import com.example.finapp.repository.FinanceRepository

class FinanceApplication : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { FinanceRepository(database.transactionDao(), database.categoryDao(), database.planDao()) }
}
