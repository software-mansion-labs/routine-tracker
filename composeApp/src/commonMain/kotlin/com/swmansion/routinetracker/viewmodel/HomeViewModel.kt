package com.swmansion.routinetracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.swmansion.routinetracker.IDataRepository
import com.swmansion.routinetracker.model.RoutineWithTasks
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(repository: IDataRepository) : ViewModel() {
    val uiState: StateFlow<HomeUiState> =
        repository
            .getAllRoutinesWithTasks()
            .map(::HomeUiState)
            .stateIn(
                scope = viewModelScope,
                started = kotlinx.coroutines.flow.SharingStarted.Eagerly,
                initialValue = HomeUiState(),
            )

    companion object {
        val DATA_REPOSITORY_KEY = object : CreationExtras.Key<IDataRepository> {}
        var Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val dataRepository =
                    this[DATA_REPOSITORY_KEY]
                        ?: throw IllegalArgumentException("DataRepository not provided")

                HomeViewModel(dataRepository)
            }
        }
    }
}

data class HomeUiState(val routinesWithTasks: List<RoutineWithTasks> = emptyList())
