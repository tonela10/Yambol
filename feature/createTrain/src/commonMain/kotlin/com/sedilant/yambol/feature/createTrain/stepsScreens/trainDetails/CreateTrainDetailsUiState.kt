package com.sedilant.yambol.feature.createTrain.stepsScreens.trainDetails

import com.sedilant.yambol.feature.createTrain.stepsScreens.tasks.TaskUI

sealed interface CreateTrainDetailsUiState {
    data object Loading : CreateTrainDetailsUiState
    data class Error(val throwable: Throwable) : CreateTrainDetailsUiState
    data class Success(
        val trainInfo: TrainInfo,
        val listOfTasks: List<TaskUI>,
        val listOfConcepts: List<String>,
        val isSaving: Boolean = false,
        val trainingSaved: Boolean = false
    ) : CreateTrainDetailsUiState
}

data class TrainInfo(
    val date: String = "",
    val hour: String = "",
    val duration: String = "",
    val team: String = ""
)
