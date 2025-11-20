package com.swmansion.routinetracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.swmansion.routinetracker.DataRepository
import com.swmansion.routinetracker.model.DayOfWeek
import com.swmansion.routinetracker.model.Routine
import com.swmansion.routinetracker.model.RoutineRecurrence
import com.swmansion.routinetracker.model.Task
import kotlin.collections.emptySet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalTime

class CreateRoutineViewModel(private val repository: DataRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(CreateRoutineUiState())
    val uiState: StateFlow<CreateRoutineUiState> = _uiState.asStateFlow()

    fun updateRoutineName(name: String) {
        _uiState.updateState { copy(routineName = name) }
    }

    fun updateVisibilityTimePicker(visibility: Boolean) {
        _uiState.updateState { copy(showTimePicker = visibility) }
    }

    fun updateSelectedDaysOfWeek(days: Set<DayOfWeek>) {
        _uiState.updateState { copy(selectedDaysOfWeek = days) }
    }

    fun updateIntervalWeeks(weeks: Float) {
        _uiState.updateState { copy(intervalWeeks = weeks) }
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

    fun updateTaskName(name: String) {
        _uiState.updateState { copy(task = task.copy(name = name)) }
    }

    fun updateTaskVisibilityTimePicker(visible: Boolean) {
        _uiState.updateState { copy(task = task.copy(showTimePicker = visible)) }
    }

    private fun updateTaskLoading(isLoading: Boolean) {
        _uiState.updateState { copy(task = task.copy(isLoading = isLoading)) }
    }

    private fun updateTaskErrorMessage(message: String?) {
        _uiState.updateState { copy(task = task.copy(errorMessage = message)) }
    }

    private fun updateTaskSuccessMessage(message: String?) {
        _uiState.updateState { copy(task = task.copy(successMessage = message)) }
    }

    fun setTaskTime(hour: Int, minute: Int) {
        _uiState.updateState {
            copy(
                task =
                    task.copy(
                        selectedHour = hour,
                        selectedMinute = minute,
                        isTimeSet = true,
                        showTimePicker = false,
                    )
            )
        }
    }

    fun clearTaskMessages() {
        _uiState.updateState { copy(task = task.copy(errorMessage = null, successMessage = null)) }
    }

    fun getTaskFormattedTime(): String? =
        if (_uiState.value.task.isTimeSet) {
            LocalTime(_uiState.value.task.selectedHour, _uiState.value.task.selectedMinute)
                .toString()
        } else {
            null
        }

    private fun getTaskTimeInSeconds(): Int? =
        if (_uiState.value.task.isTimeSet) {
            LocalTime(_uiState.value.task.selectedHour, _uiState.value.task.selectedMinute)
                .toSecondOfDay()
        } else {
            null
        }

    private fun resetTaskForm() {
        _uiState.updateState { copy(task = CreateTaskUiState()) }
    }

    fun createTask(onSuccess: () -> Unit) {
        val form = _uiState.value.task
        if (form.name.isBlank()) {
            updateTaskErrorMessage("Task name is required")
            return
        }

        updateTaskLoading(true)
        updateTaskErrorMessage(null)
        updateTaskSuccessMessage(null)

        viewModelScope.launch {
            try {
                val duration = getTaskTimeInSeconds()
                val name = form.name.trim()
                addTask(name, duration)
                updateTaskSuccessMessage("Task '$name' prepared")
                resetTaskForm()
                onSuccess()
            } catch (e: Exception) {
                updateTaskErrorMessage("Failed to create task: ${e.message}")
            } finally {
                updateTaskLoading(false)
            }
        }
    }

    fun addTask(name: String, durationSeconds: Int?) {
        val order = _uiState.value.tasks.size
        val newTask = Task(routineId = -1, name = name, duration = durationSeconds, order = order)
        _uiState.updateState { copy(tasks = tasks + newTask) }
    }

    fun createRoutine(onSuccess: (Long, Boolean) -> Unit) {
        if (_uiState.value.routineName.isBlank()) {
            updateErrorMessage("Routine name is required")
            return
        }

        updateLoading(true)
        updateErrorMessage(null)
        updateSuccessMessage(null)

        viewModelScope.launch {
            try {
                val timeString = getFormattedTime()
                val routine = Routine(name = _uiState.value.routineName.trim(), time = timeString)

                val recurrences =
                    uiState.value.selectedDaysOfWeek.map { dayOfWeek ->
                        RoutineRecurrence(
                            routineId = 0,
                            dayOfWeek = dayOfWeek.value,
                            intervalWeeks = uiState.value.intervalWeeks.toInt(),
                        )
                    }

                val routineId = repository.createRoutineWithRecurrence(routine, recurrences)
                updateSuccessMessage(
                    "Routine '${routine.name}' with ID: $routineId created successfully!"
                )

                for (task in uiState.value.tasks) repository.addTaskToRoutine(routineId, task)

                val hasTime = timeString != null

                resetForm()

                onSuccess(routineId, hasTime)
            } catch (e: Exception) {
                updateErrorMessage("Failed to create routine: ${e.message}")
            } finally {
                updateLoading(false)
            }
        }
    }

    private fun resetForm() {
        _uiState.updateState {
            copy(
                routineName = "",
                isTimeSet = false,
                selectedDaysOfWeek = emptySet(),
                intervalWeeks = 0f,
                tasks = emptyList(),
            )
        }
    }

    suspend fun getRoutineById(routineId: Long): Routine? {
        return repository.getRoutineById(routineId)
    }

    suspend fun getRecurrencesForRoutine(routineId: Long): List<RoutineRecurrence> {
        return repository.getRecurrencesForRoutine(routineId)
    }

    companion object {
        val DATA_REPOSITORY_KEY = object : CreationExtras.Key<DataRepository> {}
        var Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val dataRepository =
                    this[DATA_REPOSITORY_KEY]
                        ?: throw IllegalArgumentException("DataRepository not provided")

                CreateRoutineViewModel(dataRepository)
            }
        }
    }
}

data class CreateRoutineUiState(
    val routineName: String = "",
    val isTimeSet: Boolean = false,
    val showTimePicker: Boolean = false,
    val selectedDaysOfWeek: Set<DayOfWeek> = emptySet(),
    val intervalWeeks: Float = 0f,
    val selectedHour: Int = 0,
    val selectedMinute: Int = 0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val tasks: List<Task> = emptyList(),
    val task: CreateTaskUiState = CreateTaskUiState(),
)

data class CreateTaskUiState(
    val name: String = "",
    val isTimeSet: Boolean = false,
    val showTimePicker: Boolean = false,
    val selectedHour: Int = 0,
    val selectedMinute: Int = 0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
)
