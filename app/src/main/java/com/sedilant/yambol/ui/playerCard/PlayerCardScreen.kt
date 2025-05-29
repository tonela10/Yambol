package com.sedilant.yambol.ui.playerCard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sedilant.yambol.ui.theme.YambolTheme

@Composable
fun PlayerCardScreen(
    modifier: Modifier = Modifier,
    playerId: Int?,
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
    )
}

@Composable
private fun PlayerCardScreenStateless(
    uiState: PlayerCardDetailsUiState,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()

    when (uiState) {
        PlayerCardDetailsUiState.Loading -> {
            // TODO show something while loading
        }

        is PlayerCardDetailsUiState.Success -> {
            Card(modifier = modifier.padding(16.dp)) {
                PlayerInfoSection(
                    name = uiState.player.name,
                    number = uiState.player.number,
                    position = uiState.player.position.toString()
                )
            }
        }
    }

}

@Composable
private fun PlayerInfoSection(
    name: String,
    number: String,
    position: String,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = name)
    }
}

@Composable
fun PlayerRadarChart() {
}

@Preview(showBackground = true)
@Composable
private fun PlayerCardScreenPreview() {
    YambolTheme {
        PlayerCardScreen(
            playerId = 0
        )
    }
}
