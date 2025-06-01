package com.sedilant.yambol.ui.addStats

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sedilant.yambol.domain.Position
import com.sedilant.yambol.ui.home.models.PlayerUiModel
import com.sedilant.yambol.ui.playerCard.StatUiModel
import com.sedilant.yambol.ui.theme.YambolTheme

@Composable
fun AddTeamStatsScreen(
    modifier: Modifier = Modifier,
    teamId: Int,
    statIds: List<Int>,
    onNavigateBack: () -> Unit,
    onCompleted: () -> Unit,
    viewModel: AddTeamStatsViewModel = hiltViewModel(
        creationCallback = { factory: AddTeamStatsViewModelFactory ->
            factory.create(
                teamId = teamId,
                statIds = statIds,
            )
        }
    )
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        if (uiState is AddTeamStatsUiState.Completed) {
            onCompleted()
        }
    }

    AddTeamStatsScreenStateless(
        modifier = modifier,
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        updatePlayerRating = viewModel::updatePlayerRating,
        onNextClicked = viewModel::proceedToNext,
        onPreviousClicked = viewModel::goToPrevious,
        canProceed = viewModel.canProceed(),
        onRetry = viewModel::retry,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddTeamStatsScreenStateless(
    modifier: Modifier = Modifier,
    uiState: AddTeamStatsUiState,
    onNavigateBack: () -> Unit,
    updatePlayerRating: (Int, Int) -> Unit,
    onNextClicked: () -> Unit,
    onPreviousClicked: () -> Unit,
    canProceed: Boolean,
    onRetry: () -> Unit,
) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Rate Players")
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is AddTeamStatsUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is AddTeamStatsUiState.Success -> {
                    TeamRatingContent(
                        state = state,
                        onRatingChanged = updatePlayerRating,
                        onNextClicked = onNextClicked,
                        onPreviousClicked = onPreviousClicked,
                        canProceed = canProceed
                    )
                }

                is AddTeamStatsUiState.Error -> {
                    ErrorContent(
                        message = state.message,
                        onRetry = onRetry,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is AddTeamStatsUiState.Completed -> {
                    // This will trigger navigation through LaunchedEffect
                }
            }
        }
    }
}

@Composable
private fun TeamRatingContent(
    state: AddTeamStatsUiState.Success,
    onRatingChanged: (Int, Int) -> Unit,
    onNextClicked: () -> Unit,
    onPreviousClicked: () -> Unit,
    canProceed: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Progress indicator
        ProgressSection(
            currentIndex = state.currentStatIndex,
            total = state.totalStats,
            statName = state.currentStat.name
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Players list
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(state.players) { player ->
                PlayerRatingRow(
                    player = player,
                    currentRating = state.playerRatings[player.id],
                    onRatingChanged = { rating ->
                        onRatingChanged(player.id, rating)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Navigation buttons
        NavigationButtons(
            canGoBack = state.currentStatIndex > 0,
            canProceed = canProceed,
            isLastStat = state.currentStatIndex == state.totalStats - 1,
            onPreviousClicked = onPreviousClicked,
            onNextClicked = onNextClicked
        )
    }
}

@Composable
private fun ProgressSection(
    currentIndex: Int,
    total: Int,
    statName: String
) {
    Column {
        Text(
            text = "Rating: $statName",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Step ${currentIndex + 1} of $total",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        LinearProgressIndicator(
            progress = { (currentIndex + 1).toFloat() / total.toFloat() },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun PlayerRatingRow(
    player: PlayerUiModel,
    currentRating: Int?,
    onRatingChanged: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = player.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                repeat(5) { index ->
                    val rating = index + 1
                    RatingButton(
                        rating = rating,
                        isSelected = currentRating == rating,
                        onClick = { onRatingChanged(rating) }
                    )
                }
            }
        }
    }
}

@Composable
private fun RatingButton(
    rating: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(
                if (isSelected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.surface
                }
            )
            .border(
                width = 2.dp,
                color = if (isSelected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.outline
                },
                shape = CircleShape
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = rating.toString(),
            style = MaterialTheme.typography.titleMedium,
            color = if (isSelected) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                MaterialTheme.colorScheme.onSurface
            },
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun NavigationButtons(
    canGoBack: Boolean,
    canProceed: Boolean,
    isLastStat: Boolean,
    onPreviousClicked: () -> Unit,
    onNextClicked: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (canGoBack) {
            OutlinedButton(
                onClick = onPreviousClicked,
                modifier = Modifier.weight(1f)
            ) {
                Text("Previous")
            }
        }

        Button(
            onClick = onNextClicked,
            enabled = canProceed,
            modifier = Modifier.weight(1f)
        ) {
            Text(if (isLastStat) "Save" else "Next")
        }
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Error",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.error
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AddTeamStatsScreenPreview() {
    YambolTheme {
        AddTeamStatsScreenStateless(
            modifier = Modifier,
            uiState = AddTeamStatsUiState.Success(
                players = listOf(
                    PlayerUiModel(
                        name = "John Doe",
                        number = "1",
                        position = Position.CENTER,
                    ),
                    PlayerUiModel(
                        name = "Theo Johnson",
                        number = "1",
                        position = Position.CENTER,
                    )

                ),
                currentStat = StatUiModel(
                    id = 0,
                    name = "physical status",
                    value = 0f,
                ),
                currentStatIndex = 0,
                totalStats = 1,
                playerRatings = mapOf()
            ),
            onNavigateBack = {},
            updatePlayerRating = { _, _ -> },
            onNextClicked = {},
            onPreviousClicked = {},
            canProceed = true,
            onRetry = {}
        )
    }
}
