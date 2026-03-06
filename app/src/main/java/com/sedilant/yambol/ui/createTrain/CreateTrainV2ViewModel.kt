package com.sedilant.yambol.ui.createTrain

import androidx.lifecycle.ViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@HiltViewModel(assistedFactory = CreateTrainV2ViewModelFactory::class)
class CreateTrainV2ViewModel @AssistedInject constructor(
    @Assisted private val teamId: String
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

data class CreateTrainUiStateV2(
    val currentStep: Step = Step.BASIC_INFO,
    val teamId: String,
)

enum class Step {
    BASIC_INFO,
    CONCEPTS,
    TASKS,
    TRAIN_DETAIL
}

@AssistedFactory
interface CreateTrainV2ViewModelFactory {
    fun create(teamId: String): CreateTrainV2ViewModel
}