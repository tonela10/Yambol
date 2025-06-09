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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sedilant.yambol.R
import com.sedilant.yambol.domain.models.TrainDomainModel
import com.sedilant.yambol.ui.home.SectionHeader
import com.sedilant.yambol.ui.home.TeamTabs
import com.sedilant.yambol.ui.home.models.TeamUiModel
import com.sedilant.yambol.ui.theme.YambolTheme
import com.sedilant.yambol.ui.training.composables.DateFilter
import com.sedilant.yambol.ui.training.composables.DateFilterSection
import com.sedilant.yambol.ui.training.composables.TrainingList
import com.sedilant.yambol.ui.training.composables.filterTrainingsByDate
import java.util.Date

@Composable
fun TrainingScreen(
    modifier: Modifier = Modifier,
    onTrainClicked: (Int) -> Unit,
    onNavigateToCreateTraining: (Int) -> Unit,
    trainingViewModel: TrainingViewModel = hiltViewModel<TrainingViewModel>()
) {

    val uiState = trainingViewModel.uiState.collectAsState().value

    TrainingScreenStateless(
        modifier = modifier,
        uiState = uiState,
        onTeamChange = { trainingViewModel.onTeamChange(it) },
        onTrainClicked = onTrainClicked,
        onNavigateToCreateTraining = onNavigateToCreateTraining,
    )
}

@Composable
private fun TrainingScreenStateless(
    modifier: Modifier = Modifier,
    uiState: TrainingUiState,
    onTeamChange: (Int) -> Unit,
    onTrainClicked: (Int) -> Unit,
    onNavigateToCreateTraining: (Int) -> Unit,
) {
    var selectedDateFilter by remember { mutableStateOf(DateFilter.ALL) }

    when (uiState) {
        is TrainingUiState.Error -> {
            // TODO: Add error state UI similar to other screens
        }

        TrainingUiState.Loading -> {
            // TODO: Add loading state UI similar to other screens
        }

        is TrainingUiState.Success -> {
            val filteredTrainings = filterTrainingsByDate(uiState.trainList, selectedDateFilter)

            Box(
                modifier = modifier.fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    SectionHeader(
                        title = "Teams",
                        modifier = Modifier.padding(horizontal = 0.dp)
                    )
                    TeamTabs(
                        currentTeamId = uiState.currentTeamId,
                        listOfTeams = uiState.teamList,
                        onTeamChange = onTeamChange,
                        onCreateTeam = {},
                        isAddTeamShow = false,
                        modifier = Modifier.padding(bottom = 16.dp, start = 0.dp, end = 0.dp),
                    )

                    SectionHeader(
                        title = "Filter by Date",
                        modifier = Modifier.padding(horizontal = 0.dp)
                    )
                    DateFilterSection(
                        selectedFilter = selectedDateFilter,
                        onFilterSelected = { selectedDateFilter = it },
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    SectionHeader(
                        title = stringResource(R.string.sessions),
                        modifier = Modifier.padding(horizontal = 0.dp)
                    )
                    TrainingList(
                        trainings = filteredTrainings,
                        onTrainingClick = onTrainClicked,
                        modifier = Modifier.weight(1f),
                    )
                }

                FloatingActionButton(
                    onClick = { onNavigateToCreateTraining(uiState.currentTeamId) },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 32.dp)
                        .navigationBarsPadding()
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add New Training"
                    )
                }
            }
        }
    }
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
            onTrainClicked = {},
            onNavigateToCreateTraining = {},
        )
    }
}
