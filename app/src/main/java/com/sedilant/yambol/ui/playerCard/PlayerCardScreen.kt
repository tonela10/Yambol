package com.sedilant.yambol.ui.playerCard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sedilant.yambol.domain.Position
import com.sedilant.yambol.ui.home.models.PlayerUiModel
import com.sedilant.yambol.ui.playerCard.composables.EditPlayerBottomSheet
import com.sedilant.yambol.ui.playerCard.composables.EditPlayerData
import com.sedilant.yambol.ui.playerCard.composables.PlayerAbilityCard
import com.sedilant.yambol.ui.playerCard.composables.PlayerActionsCard
import com.sedilant.yambol.ui.playerCard.composables.PlayerChartCard
import com.sedilant.yambol.ui.playerCard.composables.PlayerObjectivesCard
import com.sedilant.yambol.ui.playerCard.composables.PlayerStatsCard
import com.sedilant.yambol.ui.theme.YambolTheme
import kotlinx.coroutines.launch

@Composable
fun PlayerCardScreen(
    modifier: Modifier = Modifier,
    playerId: Int?,
    onNavigateBack: () -> Unit,
    playerCardViewModel: PlayerCardViewModel = hiltViewModel(
        creationCallback = { factory: PlayerCardViewModelFactory ->
            factory.create(
                playerId = playerId
            )
        }
    )
) {

    val uiState = playerCardViewModel.uiState.collectAsState().value
    PlayerCardScreenStateless(
        uiState = uiState,
        modifier = modifier,
        onEditPlayer = playerCardViewModel::showEditPlayerDialog,
        onUpdatePlayer = playerCardViewModel::updatePlayer,
        onDismissEditPlayer = playerCardViewModel::hideEditPlayerDialog,
        onDeletePlayer = {
            playerCardViewModel.deletePlayer()
            onNavigateBack()
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlayerCardScreenStateless(
    modifier: Modifier = Modifier,
    uiState: PlayerCardDetailsUiState,
    onEditPlayer: () -> Unit,
    onUpdatePlayer: (EditPlayerData) -> Unit,
    onDismissEditPlayer: () -> Unit,
    onDeletePlayer: () -> Unit = {},
) {
    val scrollState = rememberScrollState()
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val scope = rememberCoroutineScope()

    when (uiState) {
        PlayerCardDetailsUiState.Loading -> {
            // TODO show something while loading
        }

        is PlayerCardDetailsUiState.Error -> {}

        is PlayerCardDetailsUiState.Success -> {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                PlayerInfoCard(
                    name = uiState.player.name,
                    number = uiState.player.number,
                    position = uiState.player.position.toString()
                )

                PlayerStatsCard()

                // TODO add case for when ability are empty
                PlayerAbilityCard(listOfAbilities = uiState.abilityList)

                PlayerChartCard(listOfAbilities = uiState.abilityList)

                PlayerPerformanceCard()

                PlayerObjectivesCard()

                PlayerActionsCard(onEditPlayer = onEditPlayer, onDeletePlayer = onDeletePlayer)
            }

            // Show edit bottom sheet when visible
            if (uiState.isEditBottomSheetVisible) {
                ModalBottomSheet(
                    onDismissRequest = {
                        if (!uiState.editBottomSheetLoading) {
                            scope.launch {
                                bottomSheetState.hide()
                                onDismissEditPlayer()
                            }
                        }
                    },
                    sheetState = bottomSheetState,
                    dragHandle = {
                        Column(
                            modifier = Modifier.padding(vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(32.dp)
                                    .height(4.dp)
                                    .background(
                                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                                        RoundedCornerShape(2.dp)
                                    )
                            )
                        }
                    }
                ) {
                    EditPlayerBottomSheet(
                        player = uiState.player,
                        onSave = { editData ->
                            onUpdatePlayer(editData)
                        },
                        onDismiss = {
                            scope.launch {
                                bottomSheetState.hide()
                                onDismissEditPlayer()
                            }
                        },
                        isLoading = uiState.editBottomSheetLoading,
                        errorMessage = uiState.editBottomSheetError
                    )
                }
            }
        }
    }
}

@Composable
private fun PlayerInfoCard(
    modifier: Modifier = Modifier,
    name: String,
    number: String,
    position: String,
) {
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(4.dp),
        //  colors = CardDefaults.cardColors(MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Player Information",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                )
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Player Info",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Text(
                modifier = Modifier.padding(top = 16.dp),
                text = name,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Jersey Number",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "#$number",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column {
                    Text(
                        text = "Position",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = position,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun PlayerPerformanceCard(
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(4.dp),
        //  colors = CardDefaults.cardColors(MaterialTheme.colorScheme.primaryContainer),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Performance Rating",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                )
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Performance",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Overall Rating",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f)
                )
                LinearProgressIndicator(
                    progress = { 0.75f },
                    modifier = Modifier
                        .weight(2f)
                        .padding(horizontal = 8.dp)
                )
                Text(
                    text = "7.5/10",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Recent Performance: Excellent showing in last 3 games with improved shooting accuracy.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PlayerCardScreenPreview() {
    YambolTheme {
        PlayerCardScreenStateless(
            uiState = PlayerCardDetailsUiState.Success(
                PlayerUiModel(
                    name = "Antonio",
                    number = "1",
                    position = Position.CENTER,
                    id = 1,
                ),
                abilityList = listOf()
            ),
            modifier = Modifier.fillMaxSize(),
            onEditPlayer = {},
            onUpdatePlayer = {},
            onDismissEditPlayer = {}
        )
    }
}
