package com.example.finapp.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val amount: Double, // Original amount
    val currency: String = "UAH",
    val exchangeRate: Double = 1.0,
    val amountInBaseCurrency: Double, // Calculated amount in UAH
    val type: String, // "INCOME" or "EXPENSE"
    val categoryName: String,
    val date: Long,
    val note: String
)
