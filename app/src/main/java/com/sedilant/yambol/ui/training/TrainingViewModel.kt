package com.sedilant.yambol.ui.training

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sedilant.yambol.data.DataStoreManager
import com.sedilant.yambol.domain.get.GetAllTrainsByTeamIdUseCase
import com.sedilant.yambol.domain.models.TrainDomainModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class TrainingViewModel @Inject constructor(
    private val getAllTrainsByTeamIdUseCase: GetAllTrainsByTeamIdUseCase,
    dataStoreManager: DataStoreManager,
) : ViewModel() {

    val uiState: StateFlow<TrainingUiState> = dataStoreManager.currentTeam
        .flatMapLatest { currentTeamId ->
            if (currentTeamId == null) {
                flowOf(TrainingUiState.Error(""))
            } else {
                val trainList = getAllTrainsByTeamIdUseCase(currentTeamId)
                flowOf(
                    TrainingUiState.Success(
                        trainList = trainList
                    )
                )
            }
        }
        .catch { exception ->
            emit(TrainingUiState.Error(exception.message ?: "Unknown error"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TrainingUiState.Loading
        )
}

sealed interface TrainingUiState {
    data object Loading : TrainingUiState
    data class Success(val trainList: List<TrainDomainModel>) : TrainingUiState
    data class Error(val message: String) : TrainingUiState
}
