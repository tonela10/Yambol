package com.sedilant.yambol.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sedilant.yambol.domain.GetTeamsUseCase
import com.sedilant.yambol.ui.home.models.TeamUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel @Inject constructor(
    private val getTeamsUseCase: GetTeamsUseCase,
) : ViewModel() {
    //MeanWhile trigger
    private val trigger = MutableSharedFlow<Unit>(
        replay = 1,
        extraBufferCapacity = 0,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    private val currentTeamFlow = MutableStateFlow(0)

    // Home UI state
    // TODO the initial value of the uiState should be the last teams used

    val uiState = trigger.flatMapLatest { _ ->
        val teamsFlow = getTeamsUseCase()
        val combinedFlow = combine(
            teamsFlow,
            currentTeamFlow,
        ) { teams, currentTeam ->
            HomeUiState.Success(
                listOfTeams = teams,
                currentTeam = currentTeam
            )
        }
        combinedFlow
    }

    init {
        viewModelScope.launch {
            trigger.emit(Unit)
        }
    }

    public fun onTeamChange(newTeamIndex: Int) {
        viewModelScope.launch {
            currentTeamFlow.update { newTeamIndex }
        }
    }
}

sealed interface HomeUiState {
    data class Success(
        val listOfTeams: List<TeamUiModel>,
        val currentTeam: Int,
    )

    data object Loading : HomeUiState
}
