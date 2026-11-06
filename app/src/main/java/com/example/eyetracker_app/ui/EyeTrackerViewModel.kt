package com.example.eyetracker_app.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.eyetracker_app.data.EyeTrackerRepository
import com.example.eyetracker_app.model.AppTab
import com.example.eyetracker_app.model.CustomGesture
import com.example.eyetracker_app.model.EyeTrackerUiState
import com.example.eyetracker_app.model.TRAINING_DURATION_SECONDS
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EyeTrackerViewModel(
    private val repository: EyeTrackerRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(repository.loadInitialState())
    val uiState: StateFlow<EyeTrackerUiState> = _uiState.asStateFlow()

    private var trainingJob: Job? = null

    fun selectTab(tab: AppTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun showSettings() {
        _uiState.update { it.copy(isSettingsDialogVisible = true) }
    }

    fun hideSettings() {
        _uiState.update { it.copy(isSettingsDialogVisible = false) }
    }

    fun setEyeGesturesEnabled(enabled: Boolean) {
        _uiState.update { it.copy(areEyeGesturesEnabled = enabled) }
        repository.saveEyeGesturesEnabled(enabled)
    }

    fun setRestNotificationEnabled(enabled: Boolean) {
        _uiState.update { it.copy(isRestNotificationEnabled = enabled) }
        repository.saveRestNotificationEnabled(enabled)
    }

    fun setEyeStrainPercentage(value: Int) {
        val normalizedValue = value.coerceIn(0, 100)
        _uiState.update { it.copy(eyeStrainPercentage = normalizedValue) }
        repository.saveEyeStrainPercentage(normalizedValue)
    }

    fun selectGesture(gestureName: String) {
        _uiState.update { it.copy(selectedGestureName = gestureName) }
    }

    fun startTraining() {
        if (_uiState.value.isTrainingRunning) return
        if (_uiState.value.remainingTrainingSeconds == 0) {
            _uiState.update {
                it.copy(
                    remainingTrainingSeconds = TRAINING_DURATION_SECONDS,
                    isTrainingRunning = true
                )
            }
        } else {
            _uiState.update { it.copy(isTrainingRunning = true) }
        }

        trainingJob?.cancel()
        trainingJob = viewModelScope.launch {
            while (_uiState.value.isTrainingRunning &&
                _uiState.value.remainingTrainingSeconds > 0
            ) {
                delay(1_000)
                val nextValue = (_uiState.value.remainingTrainingSeconds - 1).coerceAtLeast(0)
                _uiState.update {
                    it.copy(
                        remainingTrainingSeconds = nextValue,
                        isTrainingRunning = nextValue > 0
                    )
                }
            }
        }
    }

    fun pauseTraining() {
        trainingJob?.cancel()
        trainingJob = null
        _uiState.update { it.copy(isTrainingRunning = false) }
    }

    fun addCustomGesture(gesture: CustomGesture) {
        val updatedList = _uiState.value.customGestureList + gesture
        _uiState.update { it.copy(customGestureList = updatedList) }
        repository.saveCustomGestures(updatedList)
    }

    fun deleteCustomGesture(index: Int) {
        val updatedList = _uiState.value.customGestureList.filterIndexed { itemIndex, _ ->
            itemIndex != index
        }
        _uiState.update { it.copy(customGestureList = updatedList) }
        repository.saveCustomGestures(updatedList)
    }

    override fun onCleared() {
        trainingJob?.cancel()
        super.onCleared()
    }

    class Factory(private val context: Context) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(EyeTrackerViewModel::class.java)) {
                return EyeTrackerViewModel(
                    repository = EyeTrackerRepository(context.applicationContext)
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
