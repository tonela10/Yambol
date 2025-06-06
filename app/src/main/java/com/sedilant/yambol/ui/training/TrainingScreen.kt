package com.sedilant.yambol.ui.training

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sedilant.yambol.ui.home.SectionHeader
import com.sedilant.yambol.ui.home.TeamTabs
import com.sedilant.yambol.ui.theme.YambolTheme

@Composable
fun TrainingScreen(
    modifier: Modifier = Modifier,
    trainingViewModel: TrainingViewModel = hiltViewModel<TrainingViewModel>()
) {

    val uiState = trainingViewModel.uiState.collectAsState().value

    TrainingScreenStateless(
        uiState = uiState
    )
}

@Composable
private fun TrainingScreenStateless(
    modifier: Modifier = Modifier,
    uiState: TrainingUiState
) {
    when (uiState) {
        is TrainingUiState.Error -> {}
        TrainingUiState.Loading -> {
        }

        is TrainingUiState.Success -> {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                SectionHeader(title = "Teams")
                TeamTabs(
                    currentTeamId = 1,
                    listOfTeams = listOf(),
                    onTeamChange = {},
                    onCreateTeam = {},
                    isAddTeamShow = false,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Filtering()
            }
        }
    }
}

@Composable
private fun Filtering() {

}

@Preview
@Composable
private fun TrainingScreenPreview() {
    YambolTheme {
        TrainingScreenStateless(
            uiState = TrainingUiState.Success(
                trainList = listOf()
            )
        )
    }
}