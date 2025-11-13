package com.swmansion.routinetracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.swmansion.routinetracker.DataRepository
import com.swmansion.routinetracker.model.Task
import kotlin.collections.plus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalTime

class CreateTaskViewModel(private val repository: DataRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(CreateTaskUiState())
    val uiState: StateFlow<CreateTaskUiState> = _uiState.asStateFlow()

    fun updateTaskName(name: String) {
        _uiState.updateState { copy(name = name) }
    }

    fun updateVisibilityTimePicker(visibility: Boolean) {
        _uiState.updateState { copy(showTimePicker = visibility) }
    }

    fun updateLoading(isLoading: Boolean) {
        _uiState.updateState { copy(isLoading = isLoading) }
    }

    fun updateErrorMessage(errorMessage: String?) {
        _uiState.updateState { copy(errorMessage = errorMessage) }
    }

    fun updateSuccessMessage(successMessage: String?) {
        _uiState.updateState { copy(successMessage = successMessage) }
    }

    fun setTime(hour: Int, minute: Int) {
        _uiState.updateState {
            copy(
                selectedHour = hour,
                selectedMinute = minute,
                isTimeSet = true,
                showTimePicker = false,
            )
        }
    }

    fun clearMessages() {
        _uiState.updateState { copy(errorMessage = null, successMessage = null) }
    }

    fun getFormattedTime() =
        if (_uiState.value.isTimeSet) {
            LocalTime(hour = _uiState.value.selectedHour, minute = _uiState.value.selectedMinute)
                .toString()
        } else {
            null
        }

    fun getTimeInSeconds() =
        if (_uiState.value.isTimeSet) {
            LocalTime(hour = _uiState.value.selectedHour, minute = _uiState.value.selectedMinute)
                .toSecondOfDay()
        } else {
            null
        }

    fun createTask(onSuccess: (name: String, durationSeconds: Int?) -> Unit) {
        if (_uiState.value.name.isBlank()) {
            updateErrorMessage("Task name is required")
            return
        }

        updateLoading(true)
        updateErrorMessage(null)
        updateSuccessMessage(null)

        viewModelScope.launch {
            try {
                val durationSeconds = getTimeInSeconds()
                val taskPreview =
                    Task(
                        routineId = -1,
                        name = _uiState.value.name.trim(),
                        duration = durationSeconds,
                        order = 0,
                    )

                updateSuccessMessage("Task '${taskPreview.name}' prepared")
                resetForm()

                onSuccess(taskPreview.name, durationSeconds)
            } catch (e: Exception) {
                updateErrorMessage("Failed to create task: ${e.message}")
            } finally {
                updateLoading(false)
            }
        }
    }

    private fun resetForm() {
        _uiState.updateState { copy(name = "", isTimeSet = false) }
    }

    companion object {
        val DATA_REPOSITORY_KEY = object : CreationExtras.Key<DataRepository> {}
        var Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val dataRepository =
                    this[DATA_REPOSITORY_KEY]
                        ?: throw IllegalArgumentException("DataRepository not provided")

                CreateTaskViewModel(dataRepository)
            }
        }
    }
}
