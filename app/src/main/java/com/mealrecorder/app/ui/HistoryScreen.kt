package com.mealrecorder.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mealrecorder.app.data.MealRecord
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters

/**
 * 历史记录页：顶部日期选择器 + 当天三餐
 */
@Composable
fun HistoryScreen(
    viewModel: MealViewModel,
    onMealClick: (String, String) -> Unit  // (date, mealType)
) {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy年M月d日")
    val isoFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    val meals by viewModel.historyMeals.collectAsState()
    val recordedDates by viewModel.recordedDates.collectAsState()

    LaunchedEffect(selectedDate) {
        viewModel.loadHistory(selectedDate.format(isoFormatter))
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.height(12.dp))

        // 日期选择头部
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 星期 + 年月日
            Text(
                text = formatWeekDay(selectedDate),
                fontSize = 14.sp,
                color = Color(0xFF8A8A8E)
            )
            Spacer(modifier = Modifier.height(4.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                IconButton(
                    onClick = { selectedDate = selectedDate.minusDays(1) }
                ) {
                    Icon(
                        imageVector = Icons.Filled.ChevronLeft,
                        contentDescription = "前一天",
                        tint = Color(0xFF1A1A1A)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = selectedDate.format(dateFormatter),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A1A)
                    )
                    if (selectedDate == LocalDate.now()) {
                        Text(
                            text = "今天",
                            fontSize = 12.sp,
                            color = Color(0xFFFF6B35),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                IconButton(
                    onClick = {
                        if (selectedDate.isBefore(LocalDate.now())) {
                            selectedDate = selectedDate.plusDays(1)
                        }
                    },
                    enabled = selectedDate.isBefore(LocalDate.now())
                ) {
                    Icon(
                        imageVector = Icons.Filled.ChevronRight,
                        contentDescription = "后一天",
                        tint = if (selectedDate.isBefore(LocalDate.now())) Color(0xFF1A1A1A) else Color(0xFFCCCCCC)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 当天三餐列表
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            val dateStr = selectedDate.format(isoFormatter)
            MealCard(
                mealType = MealRecord.BREAKFAST,
                emoji = "🌅",
                accentColor = com.mealrecorder.app.ui.theme.BreakfastColor,
                record = meals.breakfast,
                onClick = { onMealClick(dateStr, MealRecord.BREAKFAST) },
                onDelete = { viewModel.deleteMeal(dateStr, MealRecord.BREAKFAST) }
            )
            Spacer(modifier = Modifier.height(16.dp))
            MealCard(
                mealType = MealRecord.LUNCH,
                emoji = "☀️",
                accentColor = com.mealrecorder.app.ui.theme.LunchColor,
                record = meals.lunch,
                onClick = { onMealClick(dateStr, MealRecord.LUNCH) },
                onDelete = { viewModel.deleteMeal(dateStr, MealRecord.LUNCH) }
            )
            Spacer(modifier = Modifier.height(16.dp))
            MealCard(
                mealType = MealRecord.DINNER,
                emoji = "🌙",
                accentColor = com.mealrecorder.app.ui.theme.DinnerColor,
                record = meals.dinner,
                onClick = { onMealClick(dateStr, MealRecord.DINNER) },
                onDelete = { viewModel.deleteMeal(dateStr, MealRecord.DINNER) }
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

private fun formatWeekDay(date: LocalDate): String {
    return when (date.dayOfWeek) {
        DayOfWeek.MONDAY -> "星期一"
        DayOfWeek.TUESDAY -> "星期二"
        DayOfWeek.WEDNESDAY -> "星期三"
        DayOfWeek.THURSDAY -> "星期四"
        DayOfWeek.FRIDAY -> "星期五"
        DayOfWeek.SATURDAY -> "星期六"
        DayOfWeek.SUNDAY -> "星期日"
    }
}
