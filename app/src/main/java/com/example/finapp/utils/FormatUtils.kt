package com.example.finapp.utils

import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

object FormatUtils {
    private val currencyFormat = DecimalFormat("#,##0.00 ₴")
    private val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

    fun formatCurrency(amount: Double): String {
        return currencyFormat.format(amount)
    }

    fun formatDate(timestamp: Long): String {
        return dateFormat.format(Date(timestamp))
    }
}
