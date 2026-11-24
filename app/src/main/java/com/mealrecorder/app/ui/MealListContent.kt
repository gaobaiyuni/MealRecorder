package com.mealrecorder.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mealrecorder.app.data.MealRecord
import com.mealrecorder.app.ui.theme.BreakfastColor
import com.mealrecorder.app.ui.theme.DinnerColor
import com.mealrecorder.app.ui.theme.LunchColor

/**
 * 展示某一天的三餐（今天页和历史页共用）
 */
@Composable
fun MealListContent(
    dateTitle: String,
    meals: TodayMeals,
    onMealClick: (String) -> Unit,
    onMealDelete: ((String) -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = dateTitle,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1A1A)
        )
        Spacer(modifier = Modifier.height(20.dp))

        MealCard(
            mealType = MealRecord.BREAKFAST,
            emoji = "🌅",
            accentColor = BreakfastColor,
            record = meals.breakfast,
            onClick = { onMealClick(MealRecord.BREAKFAST) },
            onDelete = onMealDelete?.let { { onMealDelete(MealRecord.BREAKFAST) } }
        )
        Spacer(modifier = Modifier.height(16.dp))

        MealCard(
            mealType = MealRecord.LUNCH,
            emoji = "☀️",
            accentColor = LunchColor,
            record = meals.lunch,
            onClick = { onMealClick(MealRecord.LUNCH) },
            onDelete = onMealDelete?.let { { onMealDelete(MealRecord.LUNCH) } }
        )
        Spacer(modifier = Modifier.height(16.dp))

        MealCard(
            mealType = MealRecord.DINNER,
            emoji = "🌙",
            accentColor = DinnerColor,
            record = meals.dinner,
            onClick = { onMealClick(MealRecord.DINNER) },
            onDelete = onMealDelete?.let { { onMealDelete(MealRecord.DINNER) } }
        )
        Spacer(modifier = Modifier.height(24.dp))
    }
}
