package com.sedilant.yambol.ui.createTrain

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class CreateTrainV2ViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(CreateTrainUiStateV2())
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

    fun onCancel() {
        _uiState.value = CreateTrainUiStateV2()
    }
}

data class CreateTrainUiStateV2(
    val currentStep: Step = Step.BASIC_INFO
)

enum class Step {
    BASIC_INFO,
    CONCEPTS,
    TASKS,
    TRAIN_DETAIL
}
