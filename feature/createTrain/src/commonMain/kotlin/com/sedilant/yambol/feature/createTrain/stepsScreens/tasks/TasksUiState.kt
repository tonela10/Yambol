package com.sedilant.yambol.feature.createTrain.stepsScreens.tasks

import com.sedilant.yambol.feature.createTrain.stepsScreens.concepts.Concept

sealed interface TasksUiState {
    data class Success(
        val draftTasks: List<TaskUI>,
        val listOfConcept: List<Concept>,
        val existingTasks: List<TaskUI>,
        val taskConceptError: String? = null
    ) : TasksUiState

    data class Error(val message: String) : TasksUiState
    data object Loading : TasksUiState
}
