package com.sedilant.yambol.feature.createTrain.stepsScreens.concepts

sealed interface ConceptsUiState {
    data class Success(val concepts: List<Concept>) : ConceptsUiState
    data class Error(val message: String) : ConceptsUiState
    data object Loading : ConceptsUiState
}
