package com.mealrecorder.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 一条用餐记录：某一天某一餐的一张照片
 */
@Entity(tableName = "meal_records")
data class MealRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    /** 日期，格式 yyyy-MM-dd */
    val date: String,
    /** 餐别：早餐 / 午餐 / 晚餐 */
    val mealType: String,
    /** 照片在 App 私有目录下的路径 */
    val photoPath: String,
    /** 记录时间戳（毫秒） */
    val timestamp: Long = System.currentTimeMillis()
) {
    companion object {
        const val BREAKFAST = "早餐"
        const val LUNCH = "午餐"
        const val DINNER = "晚餐"
    }
}
