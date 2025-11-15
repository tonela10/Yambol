package com.sedilant.yambol.ui.createTrain.stepsScreens.trainDetails

import androidx.lifecycle.ViewModel
import com.sedilant.yambol.ui.createTrain.stepsScreens.tasks.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class CreateTrainDetailsViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(CreateTrainDetailsUiState.Loading)
    val uiState: StateFlow<CreateTrainDetailsUiState> = _uiState.asStateFlow()


}

sealed interface CreateTrainDetailsUiState {
    data object Loading : CreateTrainDetailsUiState
    data class Error(val throwable: Throwable) : CreateTrainDetailsUiState
    data class Success(
        val trainInfo: TrainInfo,
        val listOfTasks: List<Task>,
        val listOfConcepts: List<String>
    ) : CreateTrainDetailsUiState
}

data class TrainInfo(
    val trainName: String = "",
)