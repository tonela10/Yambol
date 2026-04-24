package com.sedilant.yambol.feature.createTeam

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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sedilant.yambol.core.designsystem.Res
import com.sedilant.yambol.core.designsystem.add_at_least_players
import com.sedilant.yambol.core.designsystem.add_player
import com.sedilant.yambol.core.designsystem.add_team_players
import com.sedilant.yambol.core.designsystem.cancel
import com.sedilant.yambol.core.designsystem.create_team
import com.sedilant.yambol.core.designsystem.create_your_team
import com.sedilant.yambol.core.designsystem.error_name_empty_or_exists
import com.sedilant.yambol.core.designsystem.give_team_unique_name
import com.sedilant.yambol.core.designsystem.loading
import com.sedilant.yambol.core.designsystem.more_needed
import com.sedilant.yambol.core.designsystem.next
import com.sedilant.yambol.core.designsystem.person_fill
import com.sedilant.yambol.core.designsystem.person_unfill
import com.sedilant.yambol.core.designsystem.player_number
import com.sedilant.yambol.core.designsystem.players_progress
import com.sedilant.yambol.core.designsystem.ready_to_finish
import com.sedilant.yambol.core.designsystem.team_label
import com.sedilant.yambol.core.designsystem.team_progress
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun CreateTeamScreenStateless(
    uiState: CreateTeamUiState,
    onCreateTeam: () -> Unit,
    onNavigateHome: () -> Unit,
    onNextPlayer: () -> Unit,
    onFinish: () -> Unit,
    updateTeam: (String) -> Unit,
    updatePlayerName: (String) -> Unit,
    updatePlayerNumber: (String) -> Unit,
    playersCount: Int = 0,
    isCancellable: Boolean = true
) {
    // Prevent leaving setup when team creation is mandatory.
    PlatformBackHandler(enabled = !isCancellable) { }

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
                            isCancellable = isCancellable
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
                            playersCount = playersCount,
                            isCancellable = isCancellable
                        )
                    }

                    CreateTeamUiState.Loading ->
                        Text(text = stringResource(Res.string.loading))
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
    playersCount: Int,
    isCancellable: Boolean
) {
    val focusRequester = remember { FocusRequester() }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(Res.string.add_team_players),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(Res.string.add_at_least_players),
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
                    text = stringResource(Res.string.player_number, playersCount + 1),
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
            showCancel = isCancellable
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
                text = stringResource(Res.string.team_progress),
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
                            if (isFilled) Res.drawable.person_fill else Res.drawable.person_unfill
                        ),
                        contentDescription = if (isFilled) "Player added" else "Player needed",
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(Res.string.players_progress, currentPlayers, minPlayers),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            if (currentPlayers >= minPlayers) {
                Text(
                    text = stringResource(Res.string.ready_to_finish),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            } else {
                Text(
                    text = pluralStringResource(Res.plurals.more_needed, minPlayers - currentPlayers, minPlayers - currentPlayers),
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
    onFinishEnabled: Boolean = false,
    showCancel: Boolean = true
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
                text = stringResource(Res.string.add_player),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                contentDescription = stringResource(Res.string.next)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (showCancel || onFinishEnabled) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = if (showCancel && onFinishEnabled) Arrangement.spacedBy(16.dp) else Arrangement.Center
            ) {
                if (showCancel) {
                    OutlinedButton(
                        onClick = onCancel,
                        modifier = Modifier
                            .then(if (onFinishEnabled) Modifier.weight(1f) else Modifier.fillMaxWidth())
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(stringResource(Res.string.cancel))
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = stringResource(Res.string.cancel)
                        )
                    }
                }

                if (onFinishEnabled) {
                    Button(
                        onClick = onFinish,
                        modifier = if (showCancel) {
                            Modifier
                                .weight(1f)
                                .height(56.dp)
                        } else {
                            Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                        },
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = stringResource(Res.string.create_team),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
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
    isCancellable: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(Res.string.create_your_team),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(Res.string.give_team_unique_name),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
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
                TextField(
                    value = teamName,
                    onValueChange = { updateTeam(it) },
                    label = { Text(stringResource(Res.string.team_label)) },
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
                        text = stringResource(Res.string.error_name_empty_or_exists),
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
            showCancel = isCancellable
        )
    }
}
