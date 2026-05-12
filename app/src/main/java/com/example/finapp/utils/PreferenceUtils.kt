package com.example.finapp.utils

import android.content.Context
import android.content.SharedPreferences

object PreferenceUtils {
    private const val PREFS_NAME = "fin_app_prefs"
    private const val RATE_USD = "rate_usd"
    private const val RATE_EUR = "rate_eur"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun getUsdRate(context: Context): Double {
        return getPrefs(context).getFloat(RATE_USD, 40.0f).toDouble()
    }

    fun getEurRate(context: Context): Double {
        return getPrefs(context).getFloat(RATE_EUR, 43.0f).toDouble()
    }

    fun setUsdRate(context: Context, rate: Double) {
        getPrefs(context).edit().putFloat(RATE_USD, rate.toFloat()).apply()
    }

    fun setEurRate(context: Context, rate: Double) {
        getPrefs(context).edit().putFloat(RATE_EUR, rate.toFloat()).apply()
    }
}
