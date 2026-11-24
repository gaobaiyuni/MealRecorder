# 三餐记录 (MealRecorder)

一个简单的安卓 App，用于记录每天的早餐、午餐、晚餐照片。

## 功能

- 打开即显示今天的三餐卡片（早餐 / 午餐 / 晚餐）
- 点击任意一餐 → 拍照 或 从相册选择照片
- 照片仅保存在手机本地（App 私有目录）
- 可查看历史某一天的三餐记录
- 可删除或更换某一餐的照片

## 技术栈

- Kotlin
- Jetpack Compose
- Room 数据库
- Coil 图片加载

## 如何构建 APK

本项目使用 GitHub Actions 自动打包。推送代码到 `main` 分支后：

1. 打开 GitHub 仓库的 **Actions** 标签页
2. 点击最新的 **Build APK** 运行记录
3. 在底部 **Artifacts** 区域下载 `MealRecorder-debug`（可直接安装的 APK）

## 本地构建（可选）

需要 Android Studio 和 JDK 17：

```bash
./gradlew assembleDebug
```

产物位于 `app/build/outputs/apk/debug/app-debug.apk`
