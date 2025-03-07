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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sedilant.yambol.domain.Position
import com.sedilant.yambol.ui.theme.YambolTheme


@Composable
fun CreateTeamScreen(
    createTeamViewModel: CreateTeamViewModel = hiltViewModel<CreateTeamViewModel>(),
    onNavigateHome: () -> Unit
) {

    val createTeamUiState = createTeamViewModel.uiState.collectAsState(
        initial = CreateTeamUiState.AddTeamName
    ).value

    CreateTeamScreenStateless(
        uiState = createTeamUiState,
        onCreateTeam = { createTeamViewModel.onCreateTeam(it) },
        onNavigateHome = onNavigateHome,
        onNextPlayer = { name, number, position ->
            createTeamViewModel.onNextPlayer(
                name,
                number,
                position
            )
        },
    )
}

@Composable
private fun CreateTeamScreenStateless(
    uiState: CreateTeamUiState,
    onCreateTeam: (String) -> Unit,
    onNavigateHome: () -> Unit,
    onNextPlayer: (name: String, number: String, position: Position) -> Unit
) {

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // IconButton -> Something to cancel only if u have already one team
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            when (uiState) {
                CreateTeamUiState.AddTeamName -> {
                    AddNameForm(
                        onNext = onCreateTeam,
                        onCancel = onNavigateHome
                    )
                }

                CreateTeamUiState.AddPlayer -> {
                    AddPlayer(
                        onNextPlayer = onNextPlayer,
                        onCancel = onNavigateHome
                    )
                }

                CreateTeamUiState.Loading -> {

                }
            }
        }
    }
}

@Composable
private fun AddPlayer(
    onNextPlayer: (name: String, number: String, position: Position) -> Unit,
    onCancel: () -> Unit
) {
    var count by rememberSaveable { mutableIntStateOf(0) }
    var name by remember { mutableStateOf("") }
    var number by remember { mutableStateOf("") }
    Text(
        text = "ADD A PLAYER",
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Monospace,
        modifier = Modifier.padding(8.dp),
    )
    TextField(
        value = name,
        onValueChange = { name = it },
        label = { Text("Name") },
        modifier = Modifier.padding(8.dp)
    )
    TextField(
        value = number,
        onValueChange = { number = it },
        label = { Text("Number") },
        modifier = Modifier.padding(8.dp)
    )
    // TODO add more textFiels for position

    FormButtons(
        count = count,
        onNext = {
            if (name.isNotEmpty() || number.isNotEmpty()) {
                onNextPlayer(name, number, Position.POINT_GUARD)
                count++
                name = ""
                number = ""
            }
        },
        onCancel = onCancel
    )
}

@Composable
private fun FormButtons(
    count: Int = 0,
    onNext: () -> Unit,
    onCancel: () -> Unit,
) {
    Column(
    ) {
        Button(
            onClick = {
                onNext()
            },
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
                        onNext()
                        onCancel()
                    },
                ) {
                    Text(text = "Finish")
                }
            }
        }
    }
}

@Composable
private fun AddNameForm(
    onNext: (String) -> Unit,
    onCancel: () -> Unit
) {
    var name by remember { mutableStateOf("") }

    Text(
        text = "GIVE A NAME TO YOUR TEAM",
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Monospace,
        modifier = Modifier.padding(8.dp),
    )

    TextField(
        value = name,
        onValueChange = { name = it },
        label = { Text("Name") },
        modifier = Modifier.padding(8.dp)
    )

    FormButtons(
        onCancel = onCancel,
        onNext = { onNext(name) }
    )
}

@Preview(showBackground = true)
@Composable
private fun CreateTeamScreenAddTeamNamePreview() {
    YambolTheme {
        CreateTeamScreenStateless(
            uiState = CreateTeamUiState.AddTeamName,
            onCreateTeam = {},
            onNavigateHome = {},
            onNextPlayer = { name, number, position -> },
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CreateTeamScreenAddPlayerNamePreview() {
    YambolTheme {
        CreateTeamScreenStateless(
            uiState = CreateTeamUiState.AddPlayer,
            onCreateTeam = {},
            onNavigateHome = {},
            onNextPlayer = { name, number, position -> },
        )
    }
}
