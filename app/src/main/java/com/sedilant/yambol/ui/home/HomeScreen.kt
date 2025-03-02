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
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SecondaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sedilant.yambol.R
import com.sedilant.yambol.ui.home.models.FakeDataTeamsList
import com.sedilant.yambol.ui.home.models.PlayerUiModel
import com.sedilant.yambol.ui.home.models.TaskUiModel
import com.sedilant.yambol.ui.theme.YambolTheme

@Composable
fun HomeScreen(
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
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            TeamTabs()
            Spacer(modifier.padding(10.dp))

            PlayersRow(listOfPlayer)

            Spacer(modifier.padding(10.dp))

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TeamTabs() {
    val teams = FakeDataTeamsList()
    val teamsList = teams.getTeams()

    SecondaryScrollableTabRow(
        selectedTabIndex = 0,
        modifier = Modifier.fillMaxWidth()
    ) {
        teamsList.forEachIndexed() { index, team ->
            Tab(
                selected =
                /** state */
                index == 0,
                onClick = { /** state == index*/ },
                text = { Text(text = team.name, maxLines = 2, overflow = TextOverflow.Ellipsis) }
            )
        }
    }
}

@Composable
private fun TaskList(
    listOfTask: List<TaskUiModel>
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Objectives",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
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