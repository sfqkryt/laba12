package com.example.eyetracker_app.model

enum class AppTab(val displayTitle: String, val displayIcon: String) {
    Gestures("Жесты", "◉"),
    Focus("Фокус", "〜"),
    Training("Тренировка", "◎"),
    Custom("Свои", "✕")
}

data class CustomGesture(
    val gestureName: String,
    val eyeMovement: String
)

data class EyeTrackerUiState(
    val selectedTab: AppTab = AppTab.Gestures,
    val isSettingsDialogVisible: Boolean = false,
    val areEyeGesturesEnabled: Boolean = true,
    val isRestNotificationEnabled: Boolean = true,
    val eyeStrainPercentage: Int = 34,
    val selectedGestureName: String? = null,
    val remainingTrainingSeconds: Int = TRAINING_DURATION_SECONDS,
    val isTrainingRunning: Boolean = false,
    val customGestureList: List<CustomGesture> = emptyList()
)

const val TRAINING_DURATION_SECONDS = 20
