package com.mealrecorder.app.ui

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mealrecorder.app.data.MealDao
import com.mealrecorder.app.data.MealDatabase
import com.mealrecorder.app.data.MealRecord
import com.mealrecorder.app.data.PhotoStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class TodayMeals(
    val breakfast: MealRecord? = null,
    val lunch: MealRecord? = null,
    val dinner: MealRecord? = null
)

class MealViewModel(application: Application) : AndroidViewModel(application) {

    private val dao: MealDao = MealDatabase.getInstance(application).mealDao()

    private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    // 今天三餐状态
    private val _todayMeals = MutableStateFlow(TodayMeals())
    val todayMeals: StateFlow<TodayMeals> = _todayMeals

    // 历史某一天三餐状态
    private val _historyMeals = MutableStateFlow(TodayMeals())
    val historyMeals: StateFlow<TodayMeals> = _historyMeals

    // 有记录的所有日期（用于日历标记）
    private val _recordedDates = MutableStateFlow<Set<String>>(emptySet())
    val recordedDates: StateFlow<Set<String>> = _recordedDates

    init {
        refreshToday()
        refreshRecordedDates()
    }

    fun todayDateString(): String = LocalDate.now().format(dateFormatter)

    fun refreshToday() {
        val date = todayDateString()
        viewModelScope.launch {
            _todayMeals.value = loadMeals(date)
        }
    }

    fun loadHistory(date: String) {
        viewModelScope.launch {
            _historyMeals.value = loadMeals(date)
        }
    }

    fun refreshRecordedDates() {
        viewModelScope.launch {
            _recordedDates.value = dao.getAllDates().toSet()
        }
    }

    private suspend fun loadMeals(date: String): TodayMeals {
        return TodayMeals(
            breakfast = dao.getMeal(date, MealRecord.BREAKFAST),
            lunch = dao.getMeal(date, MealRecord.LUNCH),
            dinner = dao.getMeal(date, MealRecord.DINNER)
        )
    }

    /**
     * 保存一餐照片（从相册或拍照 Uri 拷贝到私有目录后入库）
     */
    fun saveMeal(date: String, mealType: String, sourceUri: Uri) {
        viewModelScope.launch {
            try {
                val path = PhotoStorage.copyToStorage(
                    getApplication(),
                    sourceUri,
                    date,
                    mealType
                )
                // 若已有旧记录，删除旧照片
                val existing = dao.getMeal(date, mealType)
                existing?.let { PhotoStorage.deletePhoto(it.photoPath) }

                dao.insert(
                    MealRecord(
                        date = date,
                        mealType = mealType,
                        photoPath = path
                    )
                )
                refreshRecordedDates()
                if (date == todayDateString()) {
                    refreshToday()
                } else {
                    loadHistory(date)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /** 删除某一餐的记录 */
    fun deleteMeal(date: String, mealType: String) {
        viewModelScope.launch {
            val existing = dao.getMeal(date, mealType)
            existing?.let { PhotoStorage.deletePhoto(it.photoPath) }
            dao.deleteMeal(date, mealType)
            refreshRecordedDates()
            if (date == todayDateString()) {
                refreshToday()
            } else {
                loadHistory(date)
            }
        }
    }
}
