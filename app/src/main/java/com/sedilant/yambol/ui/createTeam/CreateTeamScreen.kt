package com.sedilant.yambol.ui.createTeam

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sedilant.yambol.R
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
        playersCount = createTeamViewModel.getPlayersCount()
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
    playersCount: Int = 0
) {

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
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
                            playersCount = playersCount
                        )
                    }

                    CreateTeamUiState.Loading ->
                        Text(text = "Loading...")
                }
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
    playersCount: Int
) {

    val focusRequester = remember { FocusRequester() }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Add Team Players",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Add at least 5 players to create your team",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))
        
        PlayerProgressIndicator(
            currentPlayers = playersCount,
            minPlayers = 5
        )

        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Player ${playersCount + 1}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(20.dp))

                TextField(
                    value = playerName,
                    onValueChange = { updatePlayerName(it) },
                    label = { Text("Player Name") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Next,
                        capitalization = KeyboardCapitalization.Words
                    ),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                TextField(
                    value = playerNumber,
                    onValueChange = { number ->
                        // Validate number is between 0-99
                        if (number.isEmpty() || (number.toIntOrNull()
                                ?.let { it in 0..99 } == true)
                        ) {
                            updatePlayerNumber(number)
                        }
                    },
                    label = { Text("Jersey Number (0-99)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardActions = if (finishButtonEnabled) {
                        KeyboardActions(onDone = { onFinish() })
                    } else {
                        KeyboardActions(onNext = {
                            onNextPlayer()
                            focusRequester.requestFocus()
                        })
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = if (finishButtonEnabled) ImeAction.Done else ImeAction.Next,
                        keyboardType = KeyboardType.Number
                    ),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

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
}

@Composable
private fun PlayerProgressIndicator(
    currentPlayers: Int,
    minPlayers: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Team Progress",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(minPlayers) { index ->
                    val isFilled = index < currentPlayers
                    Image(
                        painter = painterResource(
                            if (isFilled) R.drawable.person_fill else R.drawable.person_unfill
                        ),
                        contentDescription = if (isFilled) "Player added" else "Player needed",
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "$currentPlayers / $minPlayers players",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            if (currentPlayers >= minPlayers) {
                Text(
                    text = "✓ Ready to finish!",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            } else {
                Text(
                    text = "${minPlayers - currentPlayers} more needed",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }
        }
    }
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
        modifier = Modifier.imePadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = onNext,
            enabled = onNextEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = "Add Player",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                contentDescription = "Next Arrow"
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = if (onFinishEnabled) Arrangement.spacedBy(16.dp) else Arrangement.Center
        ) {
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier
                    .then(if (onFinishEnabled) Modifier.weight(1f) else Modifier.fillMaxWidth())
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Cancel")
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Cancel"
                )
            }

            if (onFinishEnabled) {
                Button(
                    onClick = onFinish,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "Create Team",
                        style = MaterialTheme.typography.titleMedium
                    )
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
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Text(
            text = "Create Your Team",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Give your team a unique name",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Form Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextField(
                    value = teamName,
                    onValueChange = { updateTeam(it) },
                    label = { Text("Team Name") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Next,
                        capitalization = KeyboardCapitalization.Words
                    ),
                    keyboardActions = KeyboardActions(onNext = { onNext() }),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(12.dp),
                    isError = isErrorShow
                )

                if (isErrorShow) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "The name is empty or already exists",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        FormButtons(
            onNext = { onNext() },
            onCancel = onCancel,
            onNextEnabled = true,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CreateTeamScreenAddTeamNamePreview() {
    YambolTheme {
        CreateTeamScreenStateless(
            uiState = CreateTeamUiState.AddTeamName("", true),
            onCreateTeam = {},
            onNavigateHome = {},
            onNextPlayer = {},
            onFinish = {},
            updateTeam = {},
            updatePlayerName = {},
            updatePlayerNumber = {},
            playersCount = 0
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CreateTeamScreenAddPlayerNamePreview() {
    YambolTheme {
        CreateTeamScreenStateless(
            uiState = CreateTeamUiState.AddPlayer("Antonio", "10"),
            onCreateTeam = {},
            onNavigateHome = {},
            onNextPlayer = {},
            onFinish = {},
            updateTeam = {},
            updatePlayerName = {},
            updatePlayerNumber = {},
            playersCount = 3
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PlayerProgressIndicatorPreview() {
    YambolTheme {
        PlayerProgressIndicator(
            currentPlayers = 3,
            minPlayers = 5
        )
    }
}