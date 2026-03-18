package com.sedilant.yambol.ui.training

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.sedilant.yambol.R
import com.sedilant.yambol.domain.models.TrainDomainModel
import com.sedilant.yambol.ui.home.models.TeamUiModel
import com.sedilant.yambol.ui.theme.YambolTheme
import com.sedilant.yambol.ui.training.composables.DateFilter
import com.sedilant.yambol.ui.training.composables.DateFilterSection
import com.sedilant.yambol.ui.training.composables.TrainingList
import com.sedilant.yambol.ui.training.composables.TrainingTaskList
import com.sedilant.yambol.ui.training.composables.filterTrainingsByDate
import kotlinx.coroutines.launch
import java.util.Date

@Composable
fun TrainingScreen(
    modifier: Modifier = Modifier,
    onTrainClicked: (String) -> Unit,
    onNavigateToCreateTraining: (String) -> Unit,
    trainingViewModel: TrainingViewModel = hiltViewModel()
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
    onTeamChange: (String) -> Unit,
    onTrainClicked: (String) -> Unit,
    onNavigateToCreateTraining: (String) -> Unit,
) {
    var selectedDateFilter by remember { mutableStateOf(DateFilter.ALL) }
    val pagerState = rememberPagerState(pageCount = { 2 })
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(uiState) {
        if (uiState !is TrainingUiState.Success && pagerState.currentPage != 0) {
            pagerState.scrollToPage(0)
        }
    }

    when (uiState) {
        is TrainingUiState.Error -> {
            // TODO: Add error state UI similar to other screens
        }

        TrainingUiState.Loading -> {
            // TODO: Add loading state UI similar to other screens
        }

        is TrainingUiState.Success -> {
            val filteredTrainings = filterTrainingsByDate(uiState.trainList, selectedDateFilter)
            val tabTitles = listOf(
                stringResource(R.string.sessions),
                stringResource(R.string.tasks),
            )

            Box(
                modifier = modifier.fillMaxSize()
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    TabRow(selectedTabIndex = pagerState.currentPage) {
                        tabTitles.forEachIndexed { index, title ->
                            Tab(
                                selected = pagerState.currentPage == index,
                                onClick = {
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(index)
                                    }
                                },
                                text = { Text(text = title) }
                            )
                        }
                    }

                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 4.dp),
                    ) { page ->
                        when (page) {
                            0 -> {
                                Column(modifier = Modifier.fillMaxSize()) {
                                    TrainingSectionHeader(
                                        title = stringResource(R.string.filters),
                                        modifier = Modifier.padding(horizontal = 0.dp, vertical = 0.dp)
                                    )

                                    TrainingTeamFilterTabs(
                                        currentTeamId = uiState.currentTeamId,
                                        listOfTeams = uiState.teamList,
                                        onTeamChange = onTeamChange,
                                        modifier = Modifier.padding(horizontal = 20.dp)
                                    )

                                    Spacer(modifier = Modifier.height(4.dp))

                                    DateFilterSection(
                                        selectedFilter = selectedDateFilter,
                                        onFilterSelected = { selectedDateFilter = it },
                                        modifier = Modifier.padding(bottom = 6.dp)
                                    )

                                    TrainingSectionHeader(
                                        title = stringResource(R.string.sessions),
                                        modifier = Modifier.padding(horizontal = 0.dp, vertical = 0.dp)
                                    )
                                    TrainingList(
                                        trainings = filteredTrainings,
                                        onTrainingClick = onTrainClicked,
                                        modifier = Modifier.fillMaxSize(),
                                    )
                                }
                            }

                            else -> {
                                Column(modifier = Modifier.fillMaxSize()) {
                                    TrainingSectionHeader(
                                        title = stringResource(R.string.tasks),
                                        modifier = Modifier.padding(horizontal = 0.dp, vertical = 0.dp)
                                    )
                                    TrainingTaskList(
                                        tasks = uiState.taskList,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            }
                        }
                    }
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

@Composable
private fun TrainingSectionHeader(
    title: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = modifier.padding(horizontal = 20.dp, vertical = 6.dp)
    )
}

@Composable
private fun TrainingTeamFilterTabs(
    currentTeamId: String,
    listOfTeams: List<TeamUiModel>,
    onTeamChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 0.dp),
    ) {
        items(listOfTeams, key = { it.id }) { team ->
            FilterChip(
                selected = team.id == currentTeamId,
                onClick = { onTeamChange(team.id) },
                label = { Text(text = team.name) },
                modifier = Modifier.padding(end = 8.dp)
            )
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
                        id = "1",
                        date = Date(),
                        time = 1.5f,
                        concepts = listOf(),
                        teamId = "1"
                    ),
                    TrainDomainModel(
                        id = "2",
                        date = Date(System.currentTimeMillis() + 86400000),
                        time = 2f,
                        concepts = listOf(),
                        teamId = "1"
                    ),
                    TrainDomainModel(
                        id = "3",
                        date = Date(System.currentTimeMillis() + 172800000),
                        time = 1f,
                        concepts = listOf(),
                        teamId = "1"
                    )
                ),
                taskList = emptyList(),
                teamList = listOf(
                    TeamUiModel(
                        name = "Cachos",
                        id = "1"
                    ),
                    TeamUiModel(
                        name = "Adidas",
                        id = "2"
                    )
                ),
                currentTeamId = "1"
            ),
            modifier = Modifier,
            onTeamChange = {},
            onTrainClicked = {},
            onNavigateToCreateTraining = {},
        )
    }
}
