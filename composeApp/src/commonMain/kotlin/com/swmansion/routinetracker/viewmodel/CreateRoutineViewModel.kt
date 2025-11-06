package com.swmansion.routinetracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swmansion.routinetracker.model.DayOfWeek
import com.swmansion.routinetracker.model.Routine
import com.swmansion.routinetracker.model.RoutineRecurrence
import kotlin.collections.emptySet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CreateRoutineViewModel() : ViewModel() {
    private val _uiState = MutableStateFlow(CreateRoutineUiState())
    val uiState: StateFlow<CreateRoutineUiState> = _uiState.asStateFlow()

    fun updateRoutineName(name: String) = _uiState.updateState { copy(routineName = name) }

    fun updateVisibilityTimePicker(visibility: Boolean) =
        _uiState.updateState { copy(showTimePicker = visibility) }

    fun updateSelectedDaysOfWeek(days: Set<DayOfWeek>) =
        _uiState.updateState { copy(selectedDaysOfWeek = days) }

    fun updateIntervalWeeks(weeks: Float) = _uiState.updateState { copy(intervalWeeks = weeks) }

    fun updateLoading(isLoading: Boolean) = _uiState.updateState { copy(isLoading = isLoading) }

    fun updateErrorMessage(errorMessage: String?) =
        _uiState.updateState { copy(errorMessage = errorMessage) }

    fun updateSuccessMessage(successMessage: String?) =
        _uiState.updateState { copy(successMessage = successMessage) }

    fun setTime(hour: Int, minute: Int) =
        _uiState.updateState {
            copy(
                selectedHour = hour,
                selectedMinute = minute,
                isTimeSet = true,
                showTimePicker = false,
            )
        }

    fun clearMessages() = _uiState.updateState { copy(errorMessage = null, successMessage = null) }

    fun formatTime(hour: Int, minute: Int): String {
        return "${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}"
    }

    fun getFormattedTime(): String? {
        return if (_uiState.value.isTimeSet)
            formatTime(_uiState.value.selectedHour, _uiState.value.selectedMinute)
        else null
    }

    fun createRoutine(
        onCreateCallback: suspend (Routine, List<RoutineRecurrence>) -> Long,
        onSuccess: () -> Unit,
    ) {
        if (_uiState.value.routineName.isBlank()) {
            updateErrorMessage("Routine name is required")
            return
        }

        updateLoading(true)
        updateErrorMessage(null)
        updateSuccessMessage(null)

        viewModelScope.launch {
            try {
                val timeString =
                    if (_uiState.value.isTimeSet)
                        formatTime(_uiState.value.selectedHour, _uiState.value.selectedMinute)
                    else null
                val routine = Routine(name = _uiState.value.routineName.trim(), time = timeString)

                val recurrences =
                    uiState.value.selectedDaysOfWeek.map { dayOfWeek ->
                        RoutineRecurrence(
                            routineId = 0,
                            dayOfWeek = dayOfWeek.value,
                            intervalWeeks = uiState.value.intervalWeeks.toInt(),
                        )
                    }

                val routineId = onCreateCallback(routine, recurrences)
                updateSuccessMessage(
                    "Routine '${routine.name}' with ID: $routineId created successfully!"
                )

                resetForm()

                onSuccess()
            } catch (e: Exception) {
                updateErrorMessage("Failed to create routine: ${e.message}")
            } finally {
                updateLoading(false)
            }
        }
    }

    private fun resetForm() = _uiState.updateState {
        copy(
            routineName = "",
            isTimeSet = false,
            selectedDaysOfWeek = emptySet(),
            intervalWeeks = 0f,
        )
    }
}

data class CreateRoutineUiState(
    var routineName: String = "",
    var isTimeSet: Boolean = false,
    var showTimePicker: Boolean = false,
    var selectedDaysOfWeek: Set<DayOfWeek> = emptySet(),
    var intervalWeeks: Float = 0f,
    var selectedHour: Int = 0,
    var selectedMinute: Int = 0,
    var isLoading: Boolean = false,
    var errorMessage: String? = null,
    var successMessage: String? = null,
)
