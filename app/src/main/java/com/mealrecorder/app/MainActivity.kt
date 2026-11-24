package com.mealrecorder.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mealrecorder.app.data.PhotoStorage
import com.mealrecorder.app.ui.HistoryScreen
import com.mealrecorder.app.ui.MealListContent
import com.mealrecorder.app.ui.MealViewModel
import com.mealrecorder.app.ui.PhotoSourceSheet
import com.mealrecorder.app.ui.theme.MealRecorderTheme
import com.mealrecorder.app.ui.theme.Primary
import java.io.File

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MealRecorderTheme {
                MealRecorderApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealRecorderApp(viewModel: MealViewModel = viewModel()) {
    val context = LocalContext.current
    var currentTab by remember { mutableStateOf(0) } // 0=今天, 1=历史

    // 当前正在操作的餐（弹窗状态）
    var showSheet by remember { mutableStateOf(false) }
    var targetMealType by remember { mutableStateOf("") }
    var targetDate by remember { mutableStateOf("") }

    // 拍照临时 URI
    var cameraUri by remember { mutableStateOf<android.net.Uri?>(null) }

    // 拍照结果
    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && cameraUri != null) {
            // 拍照成功，保存
            viewModel.saveMeal(targetDate, targetMealType, cameraUri!!)
        }
        cameraUri = null
    }

    // 相册选择结果（Android 13+ 用 PickVisualMedia，低版本用 GetContent）
    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            viewModel.saveMeal(targetDate, targetMealType, it)
        }
    }

    fun launchCamera(uri: android.net.Uri) {
        takePictureLauncher.launch(uri)
    }

    // 相机权限请求
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            cameraUri?.let { launchCamera(it) }
        }
    }

    fun handleTakePhoto() {
        val uri = PhotoStorage.createCameraTempFile(context)
        cameraUri = uri
        // 检查相机权限
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        ) {
            launchCamera(uri)
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    fun handlePickGallery() {
        pickImageLauncher.launch("image/*")
    }

    Scaffold(
        containerColor = Color(0xFFF7F7F8),
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                NavigationBarItem(
                    selected = currentTab == 0,
                    onClick = { currentTab = 0 },
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.Restaurant,
                            contentDescription = "今天"
                        )
                    },
                    label = { Text("今天") }
                )
                NavigationBarItem(
                    selected = currentTab == 1,
                    onClick = { currentTab = 1 },
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.History,
                            contentDescription = "历史"
                        )
                    },
                    label = { Text("历史") }
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (currentTab == 0) {
                val meals by viewModel.todayMeals.collectAsState()
                MealListContent(
                    dateTitle = "今天 · " + viewModel.todayDateString(),
                    meals = meals,
                    onMealClick = { mealType ->
                        targetDate = viewModel.todayDateString()
                        targetMealType = mealType
                        showSheet = true
                    },
                    onMealDelete = { mealType ->
                        viewModel.deleteMeal(viewModel.todayDateString(), mealType)
                    }
                )
            } else {
                HistoryScreen(
                    viewModel = viewModel,
                    onMealClick = { date, mealType ->
                        targetDate = date
                        targetMealType = mealType
                        showSheet = true
                    }
                )
            }
        }
    }

    // 拍照/相册选择弹窗
    if (showSheet) {
        PhotoSourceSheet(
            onTakePhoto = {
                showSheet = false
                handleTakePhoto()
            },
            onPickGallery = {
                showSheet = false
                handlePickGallery()
            },
            onDismiss = { showSheet = false }
        )
    }
}
