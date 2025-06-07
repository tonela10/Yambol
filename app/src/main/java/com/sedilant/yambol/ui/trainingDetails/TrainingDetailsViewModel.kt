package com.sedilant.yambol.ui.trainingDetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sedilant.yambol.domain.get.GetTrainWithTrainTaskByTrainIdUseCase
import com.sedilant.yambol.domain.models.TrainDomainModel
import com.sedilant.yambol.domain.models.TrainTaskDomainModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = TrainingDetailsViewModelFactory::class)
class TrainingDetailsViewModel @AssistedInject constructor(
    @Assisted private val trainId: Int,
    private val getTrainWithTrainTaskByTrainIdUseCase: GetTrainWithTrainTaskByTrainIdUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<TrainingDetailsUiState>(TrainingDetailsUiState.Loading)
    val uiState: StateFlow<TrainingDetailsUiState> = _uiState

    init {
        viewModelScope.launch {
            _uiState.update {
                val trainInfo = getTrainWithTrainTaskByTrainIdUseCase(trainId)
                TrainingDetailsUiState.Success(
                    train = TrainDomainModel(
                        id = trainInfo.trainId,
                        date = trainInfo.date,
                        time = trainInfo.time,
                        concepts = trainInfo.concepts,
                        teamId = trainInfo.teamId
                    ),
                    taskList = trainInfo.trains
                )
            }
        }
    }
}

sealed interface TrainingDetailsUiState {
    data object Loading : TrainingDetailsUiState
    data class Success(
        val train: TrainDomainModel,
        val taskList: List<TrainTaskDomainModel>
    ) : TrainingDetailsUiState
}

@AssistedFactory
interface TrainingDetailsViewModelFactory {
    fun create(trainId: Int): TrainingDetailsViewModel
}
