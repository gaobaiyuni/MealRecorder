package com.mealrecorder.app.data

import android.content.Context
import android.net.Uri
import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 负责把照片保存到 App 私有目录，并返回可长期使用的文件路径
 */
object PhotoStorage {

    private const val PHOTO_DIR = "meal_photos"

    /** 拍照用的临时文件 URI */
    fun createCameraTempFile(context: Context): Uri {
        val dir = File(context.cacheDir, "camera")
        if (!dir.exists()) dir.mkdirs()
        val file = File(dir, "camera_temp_${System.currentTimeMillis()}.jpg")
        return androidx.core.content.FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }

    /** 拍照用的临时文件（供读取） */
    fun getCameraTempFile(context: Context): File {
        val dir = File(context.cacheDir, "camera")
        if (!dir.exists()) dir.mkdirs()
        val files = dir.listFiles()?.sortedByDescending { it.lastModified() }
        return files?.firstOrNull() ?: File(dir, "camera_temp.jpg")
    }

    /**
     * 把从相册选中的 Uri 或拍照的临时文件拷贝到 App 私有目录，
     * 返回永久存储路径（绝对路径）。
     */
    fun copyToStorage(context: Context, sourceUri: Uri, date: String, mealType: String): String {
        val dir = File(context.filesDir, PHOTO_DIR)
        if (!dir.exists()) dir.mkdirs()

        val safeMealType = when (mealType) {
            MealRecord.BREAKFAST -> "breakfast"
            MealRecord.LUNCH -> "lunch"
            MealRecord.DINNER -> "dinner"
            else -> "meal"
        }
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val targetFile = File(dir, "${date}_${safeMealType}_$timeStamp.jpg")

        context.contentResolver.openInputStream(sourceUri)?.use { input ->
            FileOutputStream(targetFile).use { output ->
                input.copyTo(output)
            }
        } ?: throw IllegalStateException("无法读取所选图片")

        return targetFile.absolutePath
    }

    /** 删除照片文件 */
    fun deletePhoto(path: String) {
        try {
            val file = File(path)
            if (file.exists()) file.delete()
        } catch (_: Exception) {
        }
    }

    /** 清理相机临时缓存 */
    fun clearCameraCache(context: Context) {
        try {
            File(context.cacheDir, "camera").listFiles()?.forEach { it.delete() }
        } catch (_: Exception) {
        }
    }
}
