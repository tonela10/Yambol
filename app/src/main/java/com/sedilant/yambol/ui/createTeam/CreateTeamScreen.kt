package com.sedilant.yambol.ui.createTeam

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
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
        updateTeam = createTeamViewModel::updateTeamName,
        onFinish = {
            createTeamViewModel.onFinish()
            onNavigateHome()
        },
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
    updateTeam: (String) -> Unit,
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
                    AddTeamName(
                        onNext = onCreateTeam,
                        onCancel = onNavigateHome,
                        teamName = uiState.teamName,
                        updateTeam = updateTeam,
                        isErrorShow = uiState.isErrorMessageShow,
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
                        nextButtonEnabled = uiState.isNextButtonEnabled,
                        finishButtonEnabled = uiState.isFinishButtonEnabled,
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
    nextButtonEnabled: Boolean,
    finishButtonEnabled: Boolean,
) {

    val focusRequester = remember { FocusRequester() }

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
        modifier = Modifier
            .padding(8.dp)
            .focusRequester(focusRequester),
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Next,
            capitalization = KeyboardCapitalization.Words
        ),
    )

    TextField(
        value = playerNumber,
        onValueChange = { updatePlayerNumber(it) },
        label = { Text("Number") },
        modifier = Modifier.padding(8.dp),
        keyboardActions = if (finishButtonEnabled) KeyboardActions(onDone = { onFinish() }) else KeyboardActions(
            onNext = {
                onNextPlayer()
                focusRequester.requestFocus()
            }),
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = if (finishButtonEnabled) ImeAction.Done else ImeAction.Next,
            keyboardType = KeyboardType.Number
        ),
    )

    FormButtons(
        onNext = {
            onNextPlayer()
            focusRequester.requestFocus()
        },
        onCancel = onCancel,
        onFinish = onFinish,
        onNextEnabled = nextButtonEnabled,
        onFinishEnabled = finishButtonEnabled,
    )
}

@Composable
private fun FormButtons(
    onNext: () -> Unit,
    onCancel: () -> Unit,
    onFinish: () -> Unit = {},
    onNextEnabled: Boolean = true,
    onFinishEnabled: Boolean = false
) {
    Column(
        modifier = Modifier.imePadding()
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
                modifier = Modifier.width((if (!onFinishEnabled) 250.dp else 100.dp))
            ) {
                Text(text = "Cancel")
                Spacer(modifier = Modifier.padding(5.dp))
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Cancel"
                )
            }

            if (onFinishEnabled) {
                Button(
                    onClick = {
                        onFinish()
                    }
                ) {
                    Text(text = "Finish")
                }
            }
        }
    }
}

@Composable
private fun AddTeamName(
    onNext: () -> Unit,
    onCancel: () -> Unit,
    updateTeam: (String) -> Unit,
    teamName: String,
    isErrorShow: Boolean,
) {
    val errorText = "The name is empty or already exists"

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
        modifier = Modifier.padding(8.dp),
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Next,
            capitalization = KeyboardCapitalization.Words
        ),
        keyboardActions = KeyboardActions(onNext = { onNext() }),
    )
    if (isErrorShow) {
        Text(
            text = errorText,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 4.dp)
        )
    }

    FormButtons(
        onNext = { onNext() },
        onCancel = onCancel,
        onNextEnabled = true,
    )
}

@Preview(showBackground = true)
@Composable
private fun CreateTeamScreenAddTeamNamePreview() {
    YambolTheme {
        CreateTeamScreenStateless(
            uiState = CreateTeamUiState.AddTeamName("",true),
            onCreateTeam = {},
            onNavigateHome = {},
            onNextPlayer = {},
            onFinish = {},
            updateTeam = {},
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
            updateTeam = {},
            updatePlayerName = {},
            updatePlayerNumber = {},
        )
    }
}
