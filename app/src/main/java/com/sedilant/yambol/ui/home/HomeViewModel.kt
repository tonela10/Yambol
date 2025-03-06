package com.sedilant.yambol.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sedilant.yambol.data.DataStoreManager
import com.sedilant.yambol.domain.GetPlayersUseCase
import com.sedilant.yambol.domain.GetTeamsUseCase
import com.sedilant.yambol.ui.home.models.PlayerUiModel
import com.sedilant.yambol.ui.home.models.TeamUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel @Inject constructor(
    private val getTeamsUseCase: GetTeamsUseCase,
    private val getPlayersUseCase: GetPlayersUseCase,
    private val dataStoreManager: DataStoreManager,
) : ViewModel() {
    //MeanWhile trigger
    private val trigger = MutableSharedFlow<Unit>(
        replay = 1,
        extraBufferCapacity = 0,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    private val currentTeamFlow = MutableStateFlow<Int>(1)

    // Home UI state
    // TODO the initial value of the uiState should be the last teams used

    val uiState = trigger.flatMapLatest { _ ->
        // get the list of teams
        val teamsFlow = getTeamsUseCase()
        val combinedFlow = combine(
            teamsFlow,
            currentTeamFlow,
        ) { teams, currentTeamId ->
            // get the players of the current team
            val listOfPlayer = getPlayersUseCase(currentTeamId)
            val listOfTeams = teams.map {
                TeamUiModel(
                    name = it.name,
                    id = it.id,
                )
            }
            val currentTeam =
                listOfTeams.find { it.id == currentTeamId } ?: listOfTeams.firstOrNull()

            // TODO get the task of the current team

            HomeUiState.Success(
                listOfTeams = listOfTeams,
                currentTeam = currentTeam,
                listOfPlayer = listOfPlayer.first()
            )
        }
        combinedFlow
    }

    init {
        viewModelScope.launch {
            // currentTeamFlow.update { dataStoreManager.currentTeam.firstOrNull() }
            trigger.emit(Unit)
        }
    }

    public fun onTeamChange(newTeamIndex: Int) {
        viewModelScope.launch {
            currentTeamFlow.update { newTeamIndex }
            //   dataStoreManager.saveCurrentTeam(newTeamIndex)
        }
    }
}

sealed interface HomeUiState {
    data class Success(
        val listOfTeams: List<TeamUiModel>,
        val listOfPlayer: List<PlayerUiModel>,
        val currentTeam: TeamUiModel?,
    ) : HomeUiState

    data object Loading : HomeUiState
}
