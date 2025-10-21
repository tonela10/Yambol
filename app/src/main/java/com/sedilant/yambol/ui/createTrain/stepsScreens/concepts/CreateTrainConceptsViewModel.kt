package com.sedilant.yambol.ui.createTrain.stepsScreens.concepts

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class CreateTrainConceptsViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    public fun onAddConcept(conceptName: String) {
        TODO()
    }

    public fun onConceptSelected(conceptName: String) {
        TODO()
    }

    public fun onSaveConcepts() {
        TODO()
    }

    data class UiState(
        val conceptsList: List<Concept> = emptyList(), // each concept is a string an a Boolean to indicate if it is selected or not
    )
}

data class Concept(
    val conceptName: String,
    val isSelected: Boolean
)