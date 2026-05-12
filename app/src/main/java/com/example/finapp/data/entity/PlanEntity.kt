package com.example.finapp.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "plans")
data class PlanEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val amount: Double,
    val currency: String = "UAH",
    val categoryName: String,
    val period: String, // "MONTHLY" or "WEEKLY"
    val note: String
)
