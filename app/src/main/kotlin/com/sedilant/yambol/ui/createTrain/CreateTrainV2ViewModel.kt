package com.sedilant.yambol.ui.createTrain

import androidx.lifecycle.ViewModel
import com.sedilant.yambol.feature.createTrain.CreateTrainUiStateV2
import com.sedilant.yambol.feature.createTrain.Step
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CreateTrainV2ViewModel(
    private val teamId: String
) : ViewModel() {
    private val _uiState = MutableStateFlow(CreateTrainUiStateV2(teamId = teamId))
    val uiState: StateFlow<CreateTrainUiStateV2> = _uiState.asStateFlow()

    fun onNextStep() {
        val currentStep = _uiState.value.currentStep
        val nextStepOrdinal = currentStep.ordinal + 1
        if (nextStepOrdinal < Step.entries.size) {
            val nextStep = Step.entries[nextStepOrdinal]
            _uiState.update { it.copy(currentStep = nextStep) }
        }
    }

    fun onBack() {
        val currentStep = _uiState.value.currentStep
        val previousStepOrdinal = currentStep.ordinal - 1
        if (previousStepOrdinal >= 0) {
            val previousStep = Step.entries[previousStepOrdinal]
            _uiState.update { it.copy(currentStep = previousStep) }
        }
    }

    fun onSave() {

    }
}
