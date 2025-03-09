package com.sedilant.yambol.ui.createTeam

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sedilant.yambol.ui.theme.YambolTheme


@Composable
fun CreateTeamScreen(
    createTeamViewModel: CreateTeamViewModel = hiltViewModel<CreateTeamViewModel>(),
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
        updateTeam = { teamName -> createTeamViewModel.updateTeamName(teamName) },
        onFinish = createTeamViewModel::onFinish,
        updatePlayerName = createTeamViewModel::updatePlayerName,
        updatePlayerNumber = createTeamViewModel::updatePlayerNumber,
    )
}

@Composable
private fun CreateTeamScreenStateless(
    uiState: CreateTeamUiState,
    onCreateTeam: () -> Unit,
    onNavigateHome: () -> Unit,
    onNextPlayer: () -> Unit,
    onFinish: () -> Unit,
    updateTeam: (String) -> Boolean,
    updatePlayerName: (String) -> Unit,
    updatePlayerNumber: (String) -> Unit,
) {

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            when (uiState) {
                is CreateTeamUiState.AddTeamName -> {
                    AddNameForm(
                        onNext = onCreateTeam,
                        onCancel = onNavigateHome,
                        teamName = uiState.teamName,
                        updateTeam = updateTeam
                    )
                }

                is CreateTeamUiState.AddPlayer -> {
                    AddPlayer(
                        playerName = uiState.playerName,
                        playerNumber = uiState.playerNumber,
                        onNextPlayer = onNextPlayer,
                        onCancel = onNavigateHome,
                        onFinish = onFinish,
                        updatePlayerName = updatePlayerName,
                        updatePlayerNumber = updatePlayerNumber,
                    )
                }

                CreateTeamUiState.Loading ->
                    Text(text = "Loading")
            }
        }
    }
}

@Composable
private fun AddPlayer(
    playerName: String,
    playerNumber: String,
    onNextPlayer: () -> Unit,
    onCancel: () -> Unit,
    onFinish: () -> Unit,
    updatePlayerName: (String) -> Unit,
    updatePlayerNumber: (String) -> Unit,
) {
    var count by rememberSaveable { mutableIntStateOf(0) }

    Text(
        text = "ADD A PLAYER",
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Monospace,
        modifier = Modifier.padding(8.dp),
    )
    TextField(
        value = playerName,
        onValueChange = { updatePlayerName(it) },
        label = { Text("Name") },
        modifier = Modifier.padding(8.dp)
    )
    TextField(
        value = playerNumber,
        onValueChange = { updatePlayerNumber(it) },
        label = { Text("Number") },
        modifier = Modifier.padding(8.dp)
    )
    // TODO add more textFiels for position

    FormButtons(
        count = count,
        onNext = {
            onNextPlayer()
            count++
        },
        onCancel = onCancel,
        onFinish = onFinish,
    )
}

@Composable
private fun FormButtons(
    count: Int = 0,
    onNext: () -> Unit,
    onCancel: () -> Unit,
    onFinish: () -> Unit = {},
    onNextEnabled: Boolean = true
) {
    Column(
    ) {
        Button(
            onClick = {
                onNext()
            },
            enabled = onNextEnabled,
            modifier = Modifier.width(width = 250.dp)
        ) {
            Text(text = "Next")

            Spacer(modifier = Modifier.padding(5.dp))

            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                contentDescription = "Next Arrow"
            )
        }
        Row(
            modifier = Modifier.width(width = 250.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.width((if (count <= 0) 250.dp else 100.dp))
            ) {
                Text(text = "Cancel")
                Spacer(modifier = Modifier.padding(5.dp))
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Cancel"
                )
            }
            // count could be get it from DB with a flow in the future
            if (count > 0) {
                Button(
                    onClick = {
                        onFinish()
                        onCancel()
                    }
                ) {
                    Text(text = "Finish")
                }
            }
        }
    }
}

@Composable
private fun AddNameForm(
    onNext: () -> Unit,
    onCancel: () -> Unit,
    updateTeam: (String) -> Boolean,
    teamName: String,
) {
    Text(
        text = "GIVE A NAME TO YOUR TEAM",
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Monospace,
        modifier = Modifier.padding(8.dp),
    )

    TextField(
        value = teamName,
        onValueChange = { updateTeam(it) },
        label = { Text("Name") },
        modifier = Modifier.padding(8.dp)
    )

    FormButtons(
        onCancel = onCancel,
        onNext = { onNext() },
        onNextEnabled = updateTeam(teamName),
    )
}

@Preview(showBackground = true)
@Composable
private fun CreateTeamScreenAddTeamNamePreview() {
    YambolTheme {
        CreateTeamScreenStateless(
            uiState = CreateTeamUiState.AddTeamName(""),
            onCreateTeam = {},
            onNavigateHome = {},
            onNextPlayer = {},
            onFinish = {},
            updateTeam = { name -> true },
            updatePlayerName = {},
            updatePlayerNumber = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CreateTeamScreenAddPlayerNamePreview() {
    YambolTheme {
        CreateTeamScreenStateless(
            uiState = CreateTeamUiState.AddPlayer("", ""),
            onCreateTeam = {},
            onNavigateHome = {},
            onNextPlayer = {},
            onFinish = {},
            updateTeam = { name -> true },
            updatePlayerName = {},
            updatePlayerNumber = {},
        )
    }
}
