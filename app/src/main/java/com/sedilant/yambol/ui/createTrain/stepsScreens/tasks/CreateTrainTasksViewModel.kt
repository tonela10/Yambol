package com.sedilant.yambol.ui.createTrain.stepsScreens.tasks

import androidx.lifecycle.ViewModel
import com.sedilant.yambol.ui.createTrain.stepsScreens.concepts.Concept
import com.sedilant.yambol.ui.createTrain.stepsScreens.concepts.CreateTrainConceptsViewModel.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@HiltViewModel
class CreateTrainTasksViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    fun onTasksMove(from: Int, to: Int) {
        _uiState.value = _uiState.value.copy(
            tasksList = _uiState.value.tasksList.toMutableList().apply {
                add(to, removeAt(from))
            }
        )
    }

    data class UiState(
        val tasksList: List<Task> = emptyList(), // each concept is a string an a Boolean to indicate if it is selected or not
    )
}