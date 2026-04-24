package com.sedilant.yambol.feature.createTeam

sealed interface CreateTeamUiState {
    data class AddTeamName(
        val teamName: String,
        val isErrorMessageShow: Boolean = false
    ) : CreateTeamUiState

    data class AddPlayer(
        val playerName: String,
        val playerNumber: String,
        val isNextButtonEnabled: Boolean = false,
        val isFinishButtonEnabled: Boolean = false
    ) : CreateTeamUiState

    data object Loading : CreateTeamUiState
}
