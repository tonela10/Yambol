package com.sedilant.yambol.ui.training

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sedilant.yambol.domain.models.TrainDomainModel
import com.sedilant.yambol.ui.home.SectionHeader
import com.sedilant.yambol.ui.home.TeamTabs
import com.sedilant.yambol.ui.home.models.TeamUiModel
import com.sedilant.yambol.ui.theme.YambolTheme
import com.sedilant.yambol.ui.training.composables.TrainingList
import java.util.Date

@Composable
fun TrainingScreen(
    modifier: Modifier = Modifier,
    onTrainClicked: (Int) -> Unit,
    trainingViewModel: TrainingViewModel = hiltViewModel<TrainingViewModel>()
) {

    val uiState = trainingViewModel.uiState.collectAsState().value

    TrainingScreenStateless(
        uiState = uiState,
        onTeamChange = { trainingViewModel.onTeamChange(it) },
        onTrainClicked = onTrainClicked,
    )
}

@Composable
private fun TrainingScreenStateless(
    modifier: Modifier = Modifier,
    uiState: TrainingUiState,
    onTeamChange: (Int) -> Unit,
    onTrainClicked: (Int) -> Unit,
) {
    when (uiState) {
        is TrainingUiState.Error -> {}
        TrainingUiState.Loading -> {
        }

        is TrainingUiState.Success -> {
            Box(
                modifier = modifier.fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    SectionHeader(title = "Teams")
                    TeamTabs(
                        currentTeamId = uiState.currentTeamId,
                        listOfTeams = uiState.teamList,
                        onTeamChange = onTeamChange,
                        onCreateTeam = {},
                        isAddTeamShow = false,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Filtering()

                    SectionHeader(
                        title = "Trains",
                        modifier = Modifier
                    )
                    TrainingList(
                        trainings = uiState.trainList,
                        onTrainingClick = onTrainClicked,
                        modifier = Modifier,
                    )
                }

                FloatingActionButton(
                    onClick = {}, // TODO Navigate to add train
                    modifier = modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 32.dp)
                        .navigationBarsPadding()
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null
                    )
                }
            }
        }
    }
}

@Composable
private fun Filtering(
    modifier: Modifier = Modifier
) {

}

@Preview(showBackground = true)
@Composable
private fun TrainingScreenPreview() {
    YambolTheme {
        TrainingScreenStateless(
            uiState = TrainingUiState.Success(
                trainList = listOf(
                    TrainDomainModel(
                        id = 1,
                        date = Date(),
                        time = 1.5f,
                        concepts = listOf("Dribbling", "Ball handling", "Basic moves", "Footwork"),
                        teamId = 1
                    ),
                    TrainDomainModel(
                        id = 2,
                        date = Date(System.currentTimeMillis() + 86400000),
                        time = 2f,
                        concepts = listOf("Shooting", "Free throws"),
                        teamId = 1
                    ),
                    TrainDomainModel(
                        id = 3,
                        date = Date(System.currentTimeMillis() + 172800000),
                        time = 1f,
                        concepts = listOf("Defense", "Man-to-man", "Zone defense"),
                        teamId = 1
                    )
                ),
                teamList = listOf(
                    TeamUiModel(
                        name = "Cachos",
                        id = 1
                    ),
                    TeamUiModel(
                        name = "Adidas",
                        id = 1
                    )
                ),
                currentTeamId = 1
            ),
            modifier = Modifier,
            onTeamChange = {},
            onTrainClicked = {}
        )
    }
}
