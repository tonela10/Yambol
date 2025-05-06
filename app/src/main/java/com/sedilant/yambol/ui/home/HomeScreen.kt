package com.sedilant.yambol.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sedilant.yambol.R
import com.sedilant.yambol.domain.Position
import com.sedilant.yambol.ui.home.models.PlayerUiModel
import com.sedilant.yambol.ui.home.models.TeamUiModel
import com.sedilant.yambol.ui.theme.YambolTheme

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = hiltViewModel<HomeViewModel>(),
    onCreateTeam: () -> Unit,
) {
    val homeUiState = homeViewModel.uiState.collectAsState(
        initial = HomeUiState.Loading
    ).value
    HomeScreenStateless(
        homeUiState = homeUiState,
        onTeamChange = { homeViewModel.onTeamChange(it) },
        onCreateTeam = onCreateTeam,
        onSaveNewObjective = homeViewModel::onSaveNewObjective,
        onToggleObjectiveStatus = homeViewModel::onToggleObjectiveStatus,
        onUpdateObjective = homeViewModel::onUpdateObjective,
        onDeleteTeamObjective = homeViewModel::onDeleteObjective,
    )
}

@Composable
private fun HomeScreenStateless(
    homeUiState: HomeUiState,
    onTeamChange: (Int) -> Unit,
    onCreateTeam: () -> Unit,
    onSaveNewObjective: (String) -> Unit,
    onToggleObjectiveStatus: (Int) -> Unit,
    onUpdateObjective: (Int, String) -> Unit,
    onDeleteTeamObjective: (Int, String, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {

    when (homeUiState) {
        is HomeUiState.Loading -> {
            // por ahora nada
        }

        HomeUiState.CreateTeam -> onCreateTeam()

        is HomeUiState.Success -> {

            Box(modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    TeamTabs(
                        currentTeam = homeUiState.currentTeam,
                        listOfTeams = homeUiState.listOfTeams,
                        onTeamChange = onTeamChange,
                        onCreateTeam = onCreateTeam,
                    )

                    PlayersRow(homeUiState.listOfPlayer)

                    TaskList(
                        homeUiState.listOfObjectives,
                        onToggleObjectiveStatus = onToggleObjectiveStatus,
                        onDeleteObjective = onDeleteTeamObjective,
                        onUpdateObjective = onUpdateObjective,
                        onSaveNewObjective = onSaveNewObjective,
                        currentTeamId = homeUiState.currentTeam?.id ?: 0,
                    )

                    Spacer(modifier.padding(10.dp))
                    /** Insert Row with two clickable images */

                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        BigButtonYambol(
                            drawable = R.drawable.healt_icon_filled,
                            description = "Register your train"
                        )

                        BigButtonYambol(
                            drawable = R.drawable.healt_icon_filled,
                            description = "Register your train"
                        )
                    }
                }
            }
        }
    }


}

@Composable
private fun TeamTabs(
    currentTeam: TeamUiModel?,
    listOfTeams: List<TeamUiModel>,
    onTeamChange: (Int) -> Unit,
    onCreateTeam: () -> Unit,
    modifier: Modifier = Modifier
) {

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.padding(8.dp)
    ) {

        item {
            AssistChip(
                onClick = onCreateTeam,
                label = { Text("Add team") },
                leadingIcon = {
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = "Add team",
                        Modifier.size(AssistChipDefaults.IconSize)
                    )
                },
            )
        }
        items(listOfTeams) { team ->

            FilterChip(
                selected = team == currentTeam,
                onClick = { onTeamChange(team.id) },
                label = { Text(text = team.name) },
            )
        }
    }
}


@Composable
private fun PlayersRow(listOfPlayer: List<PlayerUiModel>) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(20.dp)
    ) {
        items(listOfPlayer) { player ->
            Card(Modifier.height(100.dp)) {
                Row(
                    Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(R.drawable.default_profile_photo),
                        contentDescription = "Player name",
                        Modifier
                            .size(50.dp)
                            .padding(5.dp)
                    )
                    Column(Modifier.padding(end = 5.dp, start = 5.dp)) {
                        Text(
                            text = player.name,
                            fontSize = 15.sp,
                            textAlign = TextAlign.Left,
                        )
                        Text(
                            text = player.position.name,
                            fontSize = 8.sp,
                            textAlign = TextAlign.Left,
                        )
                    }
                    Text(
                        text = player.number,
                        fontSize = 50.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun BigButtonYambol(
    drawable: Int,
    description: String
) {
    Card(
        Modifier
            .height(150.dp)
            .width(150.dp),
    ) {
        Image(
            painter = painterResource(drawable),
            contentDescription = description,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PlayersRowPreview() {
    YambolTheme {
        PlayersRow(
            listOf(
                PlayerUiModel("Antonio", "10", Position.POINT_GUARD),
                PlayerUiModel("Luis", "44", Position.SHOOTING_GUARD)
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    YambolTheme {
        HomeScreenStateless(
            onCreateTeam = {},
            homeUiState = HomeUiState.Success(
                listOfTeams = listOf(),
                listOfPlayer = listOf(),
                currentTeam = TeamUiModel(
                    name = "",
                    id = 1
                ),
                listOfObjectives = listOf()
            ),
            onTeamChange = {},
            onSaveNewObjective = {},
            onToggleObjectiveStatus = {},
            onUpdateObjective = { _, _ -> },
            onDeleteTeamObjective = { _, _, _ -> }
        )
    }
}
