package com.swmansion.routinetracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.swmansion.routinetracker.DataRepository
import com.swmansion.routinetracker.model.Routine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(private val repository: DataRepository) : ViewModel() {
    private val _selectedDestination = MutableStateFlow(NavigationDestination.HOME)
    
    val uiState: StateFlow<HomeUiState> = combine(
        repository.getAllRoutines(),
        _selectedDestination
    ) { routines, selectedDestination ->
        HomeUiState(
            routines = routines,
            selectedDestination = selectedDestination
        )
    }.stateIn(
        scope = viewModelScope,
        started = kotlinx.coroutines.flow.SharingStarted.Eagerly,
        initialValue = HomeUiState()
    )

    fun updateSelectedDestination(destination: NavigationDestination) {
        _selectedDestination.value = destination
    }

    companion object {
        val DATA_REPOSITORY_KEY = object : CreationExtras.Key<DataRepository> {}
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

data class HomeUiState(
    val routines: List<Routine> = emptyList(),
    val selectedDestination: NavigationDestination = NavigationDestination.HOME
)

enum class NavigationDestination {
    HOME
}
