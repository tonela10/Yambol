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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sedilant.yambol.R
import com.sedilant.yambol.ui.home.models.PlayerUiModel
import com.sedilant.yambol.ui.home.models.TaskUiModel
import com.sedilant.yambol.ui.home.models.TeamUiModel
import com.sedilant.yambol.ui.theme.YambolTheme

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = hiltViewModel<HomeViewModel>(),
) {
    val homeUiState = homeViewModel.uiState.collectAsState(
        initial = HomeUiState.Loading
    ).value
    HomeScreenStateless(
        currentTeam = 1, // homeViewModel.getCurrentTeam(),
        listOfTeams = emptyList(),//homeUiState.listOfTeams,
        onTeamChange = { homeViewModel.onTeamChange(it) }
    )
}

@Composable
private fun HomeScreenStateless(
    currentTeam: Int,
    listOfTeams: List<TeamUiModel>,
    onTeamChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    // TODO ask if you can create a composable height depending of the height of the screen
    // TODO remove this list. Should be into the UiState
    val listOfPlayer = listOf(
        PlayerUiModel("Antonio", "10", "Point guard"),
        PlayerUiModel("Luis", "44", "Strong forward"),
        PlayerUiModel("Dorrinha", "2", "Center"),
        PlayerUiModel("Dorrinha", "2", "Center"),
        PlayerUiModel("Dorrinha", "2", "Center"),
        PlayerUiModel("Dorrinha", "2", "Center"),
        PlayerUiModel("Dorrinha", "2", "Center"),
        PlayerUiModel("Dorrinha", "2", "Center"),
    )

    val listOfTask = listOf(
        TaskUiModel("Correr 10 minutos", false),
        TaskUiModel("Ejercicio de bote", false),
        TaskUiModel("Que todos metan dos libre", true),
    )
    Box(modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            TeamTabs(
                currentTeam = currentTeam,
                listOfTeams = listOfTeams,
                onTeamChange = onTeamChange,
            )

            PlayersRow(listOfPlayer)

            TaskList(listOfTask)

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

@Composable
private fun TeamTabs(
    currentTeam: Int,
    listOfTeams: List<TeamUiModel>,
    onTeamChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.padding(8.dp)
    ) {

        item {
            AssistChip(
                onClick = { /*TODO add a team*/ },
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
                selected = team == listOfTeams[currentTeam],
                onClick = { onTeamChange(listOfTeams.indexOf(team)) },
                label = { Text(text = team.name) },
            )
        }
    }
}

@Composable
private fun TaskList(
    listOfTask: List<TaskUiModel>
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = "Objectives",
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center)
            )

            IconButton(
                onClick = {/*Add note*/ },
                modifier = Modifier.align(Alignment.CenterEnd),

                ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "add note"
                )
            }
        }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            items(listOfTask) { task ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = task.isComplete,
                        onCheckedChange = {},
                    )

                    Text(
                        text = task.description
                    )
                }
            }
        }
    }
}

@Composable
private fun PlayersRow(listOfPlayer: List<PlayerUiModel>) { // TODO make the row widther than what it is
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
                            text = player.position,
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
fun TaskListPreview() {
    YambolTheme {
        TaskList(
            listOf(
                TaskUiModel("Correr 10 minutos", false),
                TaskUiModel("Ejercicio de bote", false),
                TaskUiModel("Que todos metan dos libre", true),
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PlayersRowPreview() {
    YambolTheme {
        PlayersRow(
            listOf(
                PlayerUiModel("Antonio", "10", "Point guard"),
                PlayerUiModel("Luis", "44", "Strong forward")
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    YambolTheme {
        HomeScreen()
    }
}
