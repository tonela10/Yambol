package com.sedilant.yambol.ui.training

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sedilant.yambol.data.DataStoreManager
import com.sedilant.yambol.domain.get.GetAllTrainsByTeamIdUseCase
import com.sedilant.yambol.domain.get.GetTeamsUseCase
import com.sedilant.yambol.domain.models.TrainDomainModel
import com.sedilant.yambol.ui.home.models.TeamUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class TrainingViewModel @Inject constructor(
    private val getAllTrainsByTeamIdUseCase: GetAllTrainsByTeamIdUseCase,
    private val dataStoreManager: DataStoreManager,
    private val getTeamsUseCase: GetTeamsUseCase,
) : ViewModel() {

    private val teamsFlow = flow {
        emit(getTeamsUseCase())
    }.flatMapLatest { it }

    val uiState: StateFlow<TrainingUiState> = combine(
        dataStoreManager.currentTeam,
        teamsFlow,
    ) { currentTeamId, teamDomainList ->

        val teamUiList = teamDomainList.map { team ->
            TeamUiModel(
                name = team.name.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString()
                },
                id = team.id,
            )
        }

        currentTeamId to teamUiList
    }.flatMapLatest { (currentTeamId, teamList) ->
        if (currentTeamId == null) {
            flowOf(
                TrainingUiState.Success(
                    trainList = emptyList(),
                    teamList = teamList
                )
            )
        } else {
            try {
                val trainList = getAllTrainsByTeamIdUseCase(currentTeamId) // TODO change with a flow
                flowOf(
                    TrainingUiState.Success(
                        trainList = trainList,
                        teamList = teamList
                    )
                )
            } catch (e: Exception) {
                flowOf(TrainingUiState.Error(e.message ?: "Unknown error"))
            }
        }
    }.catch { exception ->
        emit(TrainingUiState.Error(exception.message ?: "Unknown error"))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TrainingUiState.Loading
    )

    fun onTeamChange(newTeamId: Int) {
        viewModelScope.launch {
            dataStoreManager.saveCurrentTeam(newTeamId)
        }
    }
}


sealed interface TrainingUiState {
    data object Loading : TrainingUiState
    data class Success(
        val trainList: List<TrainDomainModel>,
        val teamList: List<TeamUiModel>
    ) : TrainingUiState

    data class Error(val message: String) : TrainingUiState
}