package com.sedilant.yambol.ui.home

import androidx.lifecycle.ViewModel
import com.sedilant.yambol.ui.home.models.TeamUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel : ViewModel() {

    // Home UI state
    // TODO the initial value of the uiState should be the last teams used
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    public fun getCurrentTeam(): Int {
        return uiState.value.currentTeam
    }

    public fun onTeamChange(newTeamIndex: Int) {
        _uiState.value = _uiState.value.copy(currentTeam = newTeamIndex)
    }
}


data class HomeUiState(
    val listOfTeams: List<TeamUiModel> = listOf(TeamUiModel("Team 1"), TeamUiModel("Team 2")),
    val currentTeam: Int = 0
)