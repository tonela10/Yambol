package com.sedilant.yambol.ui.createTeam

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.tooling.preview.Preview
import com.sedilant.yambol.feature.createTeam.CreateTeamScreenStateless
import com.sedilant.yambol.feature.createTeam.CreateTeamUiState
import com.sedilant.yambol.ui.theme.YambolTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun CreateTeamScreen(
    createTeamViewModel: CreateTeamViewModel = koinViewModel(),
    isCancellable: Boolean = true,
    onNavigateHome: () -> Unit
) {
    val createTeamUiState = createTeamViewModel.uiState.collectAsState(
        initial = CreateTeamUiState.Loading
    ).value

    CreateTeamScreenStateless(
        uiState = createTeamUiState,
        onCreateTeam = createTeamViewModel::onCreateTeam,
        onNavigateHome = onNavigateHome,
        onNextPlayer = { createTeamViewModel.onNextPlayer() },
        updateTeam = createTeamViewModel::updateTeamName,
        onFinish = {
            createTeamViewModel.onFinish()
            onNavigateHome()
        },
        updatePlayerName = createTeamViewModel::updatePlayerName,
        updatePlayerNumber = createTeamViewModel::updatePlayerNumber,
        playersCount = createTeamViewModel.getPlayersCount(),
        isCancellable = isCancellable
    )
}

@Preview(showBackground = true)
@Composable
private fun CreateTeamScreenAddTeamNamePreview() {
    YambolTheme {
        CreateTeamScreenStateless(
            uiState = CreateTeamUiState.AddTeamName("", true),
            onCreateTeam = {},
            onNavigateHome = {},
            onNextPlayer = {},
            onFinish = {},
            updateTeam = {},
            updatePlayerName = {},
            updatePlayerNumber = {},
            playersCount = 0,
            isCancellable = true
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CreateTeamScreenAddPlayerNamePreview() {
    YambolTheme {
        CreateTeamScreenStateless(
            uiState = CreateTeamUiState.AddPlayer("Antonio", "10"),
            onCreateTeam = {},
            onNavigateHome = {},
            onNextPlayer = {},
            onFinish = {},
            updateTeam = {},
            updatePlayerName = {},
            updatePlayerNumber = {},
            playersCount = 3,
            isCancellable = true
        )
    }
}
