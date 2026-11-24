package com.mealrecorder.app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MealDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: MealRecord): Long

    /** 查询某一天某一餐的记录（理论上最多一条，取最新） */
    @Query("SELECT * FROM meal_records WHERE date = :date AND mealType = :mealType ORDER BY timestamp DESC LIMIT 1")
    suspend fun getMeal(date: String, mealType: String): MealRecord?

    /** 查询某一天的所有记录 */
    @Query("SELECT * FROM meal_records WHERE date = :date ORDER BY timestamp DESC")
    suspend fun getMealsByDate(date: String): List<MealRecord>

    /** 删除某一天某一餐的记录 */
    @Query("DELETE FROM meal_records WHERE date = :date AND mealType = :mealType")
    suspend fun deleteMeal(date: String, mealType: String)

    /** 所有有记录的日期（用于历史日历标记） */
    @Query("SELECT DISTINCT date FROM meal_records ORDER BY date DESC")
    suspend fun getAllDates(): List<String>
}
