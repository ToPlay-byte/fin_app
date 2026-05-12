package com.example.finapp.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.finapp.data.dao.CategoryDao
import com.example.finapp.data.dao.PlanDao
import com.example.finapp.data.dao.TransactionDao
import com.example.finapp.data.entity.CategoryEntity
import com.example.finapp.data.entity.PlanEntity
import com.example.finapp.data.entity.TransactionEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [TransactionEntity::class, CategoryEntity::class, PlanEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao
    abstract fun planDao(): PlanDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "finance_database"
                )
                .fallbackToDestructiveMigration()
                .addCallback(DatabaseCallback())
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        val categoryDao = database.categoryDao()
                        val categories = listOf(
                            CategoryEntity(name = "Продукти", type = "EXPENSE"),
                            CategoryEntity(name = "Транспорт", type = "EXPENSE"),
                            CategoryEntity(name = "Навчання", type = "EXPENSE"),
                            CategoryEntity(name = "Розваги", type = "EXPENSE"),
                            CategoryEntity(name = "Здоров’я", type = "EXPENSE"),
                            CategoryEntity(name = "Комунальні послуги", type = "EXPENSE"),
                            CategoryEntity(name = "Інше", type = "EXPENSE"),
                            CategoryEntity(name = "Зарплата", type = "INCOME"),
                            CategoryEntity(name = "Стипендія", type = "INCOME"),
                            CategoryEntity(name = "Підробіток", type = "INCOME"),
                            CategoryEntity(name = "Подарунок", type = "INCOME"),
                            CategoryEntity(name = "Інше", type = "INCOME")
                        )
                        categoryDao.insertAll(categories)
                    }
                }
            }
        }
    }
}
