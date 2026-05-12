package com.example.finapp.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.finapp.data.entity.PlanEntity

@Dao
interface PlanDao {
    @Query("SELECT * FROM plans")
    fun getAllPlans(): LiveData<List<PlanEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(plan: PlanEntity)

    @Update
    suspend fun update(plan: PlanEntity)

    @Delete
    suspend fun delete(plan: PlanEntity)
    
    @Query("DELETE FROM plans")
    suspend fun deleteAll()
}
