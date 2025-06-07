package com.sedilant.yambol.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sedilant.yambol.R
import com.sedilant.yambol.domain.Position
import com.sedilant.yambol.ui.home.models.PlayerUiModel
import com.sedilant.yambol.ui.home.models.TeamUiModel
import com.sedilant.yambol.ui.theme.YambolTheme

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    homeViewModel: HomeViewModel = hiltViewModel<HomeViewModel>(),
    onCreateTeam: () -> Unit,
    onPlayerClicked: (Int) -> Unit,
    onRegisterTrain: (Int, List<Int>) -> Unit
) {
    val homeUiState = homeViewModel.uiState.collectAsState(
        initial = HomeUiState.Loading
    ).value
    HomeScreenStateless(
        modifier = modifier,
        homeUiState = homeUiState,
        onTeamChange = { homeViewModel.onTeamChange(it) },
        onCreateTeam = onCreateTeam,
        onPlayerClicked = { id -> onPlayerClicked(id) },
        onSaveNewObjective = homeViewModel::onSaveNewObjective,
        onToggleObjectiveStatus = homeViewModel::onToggleObjectiveStatus,
        onUpdateObjective = homeViewModel::onUpdateObjective,
        onDeleteTeamObjective = homeViewModel::onDeleteObjective,
        onRegisterTrain = { teamId, statsId -> onRegisterTrain(teamId, statsId) }
    )
}

@Composable
private fun HomeScreenStateless(
    modifier: Modifier = Modifier,
    homeUiState: HomeUiState,
    onTeamChange: (Int) -> Unit,
    onCreateTeam: () -> Unit,
    onPlayerClicked: (Int) -> Unit,
    onSaveNewObjective: (String) -> Unit,
    onToggleObjectiveStatus: (Int) -> Unit,
    onUpdateObjective: (Int, String) -> Unit,
    onDeleteTeamObjective: (Int, String, Boolean) -> Unit,
    onRegisterTrain: (Int, List<Int>) -> Unit,
) {

    when (homeUiState) {
        is HomeUiState.Loading -> {
            // to add thing to load in future
        }

        HomeUiState.CreateTeam -> onCreateTeam()

        is HomeUiState.Success -> {
            Column(
                modifier = modifier.fillMaxSize()
            ) {
                SectionHeader(title = "Teams")
                homeUiState.currentTeam?.let {
                    TeamTabs(
                        currentTeamId = it.id,
                        listOfTeams = homeUiState.listOfTeams,
                        onTeamChange = onTeamChange,
                        onCreateTeam = onCreateTeam,
                        modifier = Modifier.padding(bottom = 16.dp),
                    )
                }

                if (homeUiState.listOfPlayer.isNotEmpty()) {
                    SectionHeader(title = "Players")
                    PlayersRow(
                        onPlayerClicked = onPlayerClicked,
                        listOfPlayer = homeUiState.listOfPlayer,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                TaskList(
                    homeUiState.listOfObjectives,
                    onToggleObjectiveStatus = onToggleObjectiveStatus,
                    onDeleteObjective = onDeleteTeamObjective,
                    onUpdateObjective = onUpdateObjective,
                    onSaveNewObjective = onSaveNewObjective,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                ActionButtonsSection(
                    onLastTrainClick = { },
                    onRegisterTrainClick = {
                        homeUiState.currentTeam?.let {
                            onRegisterTrain(it.id, homeUiState.statIds)
                        }
                    }
                )
            }
        }
    }
}


@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = modifier.padding(horizontal = 20.dp, vertical = 8.dp)
    )
}

@Composable
fun TeamTabs(
    currentTeamId: Int,
    listOfTeams: List<TeamUiModel>,
    onTeamChange: (Int) -> Unit,
    onCreateTeam: () -> Unit,
    isAddTeamShow: Boolean = true,
    modifier: Modifier = Modifier
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 20.dp),
        modifier = modifier
    ) {
        if (isAddTeamShow) {
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
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        labelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        }
        items(listOfTeams) { team ->
            FilterChip(
                selected = team.id == currentTeamId,
                onClick = { onTeamChange(team.id) },
                label = {
                    Text(
                        text = team.name,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                shape = RoundedCornerShape(16.dp)
            )
        }
    }
}

@Composable
private fun PlayersRow(
    listOfPlayer: List<PlayerUiModel>,
    onPlayerClicked: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 20.dp),
        modifier = modifier
    ) {
        items(listOfPlayer) { player ->
            PlayerCard(
                player = player,
                onClick = { onPlayerClicked(player.id) }
            )
        }
    }
}

@Composable
private fun PlayerCard(
    player: PlayerUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .width(160.dp)
            .height(80.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = player.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = player.position.name,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Player Number
            Text(
                text = player.number,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun ActionButtonsSection(
    onLastTrainClick: () -> Unit,
    onRegisterTrainClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 20.dp)
    ) {
        Text(
            text = "Quick Actions",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(
                    IntrinsicSize.Max
                ),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            BigButtonYambol(
                drawable = R.drawable.healt_icon_filled,
                title = "Last Training",
                description = "View your recent session",
                onClick = onLastTrainClick,
                modifier = Modifier.weight(1f)
            )

            BigButtonYambol(
                drawable = R.drawable.fitness_center,
                title = "New Training",
                description = "Register a new session",
                onClick = onRegisterTrainClick,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun BigButtonYambol(
    modifier: Modifier = Modifier,
    drawable: Int,
    title: String,
    description: String,
    onClick: () -> Unit,
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Spacer(Modifier.weight(1f))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                painter = painterResource(drawable),
                contentDescription = description,
                modifier = Modifier
                    .size(48.dp)
                    .weight(1f),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Spacer(modifier = Modifier.height(12.dp))

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PlayersRowPreview() {
    YambolTheme {
        PlayersRow(
            listOf(
                PlayerUiModel("Antonio", "10", Position.POINT_GUARD, id = 0),
                PlayerUiModel("Luis", "44", Position.SHOOTING_GUARD, id = 0)
            ),
            onPlayerClicked = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun YambolButtonPreview() {
    YambolTheme {
        BigButtonYambol(
            drawable = R.drawable.fitness_center,
            title = "New Training",
            description = "Register a new session"
        ) { }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    YambolTheme {
        HomeScreenStateless(
            modifier = Modifier,
            onCreateTeam = {},
            homeUiState = HomeUiState.Success(
                listOfTeams = listOf(TeamUiModel("Utebo", 1), TeamUiModel("Olivar", 2)),
                listOfPlayer = listOf(
                    PlayerUiModel("Antonio", "10", Position.POINT_GUARD, id = 0),
                    PlayerUiModel("Luis", "44", Position.SHOOTING_GUARD, id = 1)
                ),
                currentTeam = TeamUiModel("Utebo", 1),
                listOfObjectives = listOf(),
                statIds = listOf()
            ),
            onTeamChange = {},
            onSaveNewObjective = {},
            onToggleObjectiveStatus = {},
            onUpdateObjective = { _, _ -> },
            onDeleteTeamObjective = { _, _, _ -> },
            onPlayerClicked = {},
            onRegisterTrain = { _, _ -> },
        )
    }
}