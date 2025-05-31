package com.sedilant.yambol.ui.playerCard

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sedilant.yambol.domain.Position
import com.sedilant.yambol.ui.home.models.PlayerUiModel
import com.sedilant.yambol.ui.theme.YambolTheme
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

data class Ability(
    val name: String,
    val value: Float,
)

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
                Column {
                    PlayerInfoSection(
                        name = uiState.player.name,
                        number = uiState.player.number,
                        position = uiState.player.position.toString()
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(
                            horizontal = 16.dp,
                            vertical = 8.dp
                        )
                    )

                    PlayerAbilityInfo()

                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        thickness = 2.dp,
                    )

                    PlayerAbilityChart()

                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        thickness = 2.dp,
                    )

                    PlayerObjectives()
                }
            }
        }
    }
}

@Composable
private fun PlayerAbilityInfo(
    modifier: Modifier = Modifier
) {
    val listOfAbilities = listOf(
        Ability("Bounce", 2.3f),
        Ability("Pass", 4.1f),
        Ability("Shoot", 1f),
        Ability("Defense", 5f)
    )
    Column(modifier = modifier.padding(16.dp)) {
        Text(
            text = "Abilities",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn {
            items(listOfAbilities) { item ->
                AbilityRow(item)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun AbilityRow(ability: Ability, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = ability.name,
            style = MaterialTheme.typography.bodyLarge,
        )
        LinearProgressIndicator(
            modifier = Modifier
                .padding(start = 8.dp)
                .weight(3f),
            progress = { ability.value / 5f }, // Fixed: Convert to 0-1 range (assuming max value is 5)
        )
        Text(
            modifier = Modifier
                .padding(start = 4.dp)
                .weight(1f),
            text = ability.value.toString(),
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun PlayerAbilityChart(
    modifier: Modifier = Modifier
) {
    val listOfAbilities = listOf(
        Ability("Bounce", 2.3f),
        Ability("Pass", 4.1f),
        Ability("Shoot", 1f),
        Ability("Defense", 5f)
    )

    Column(modifier = modifier.padding(16.dp)) {
        Text(
            text = "Ability Chart",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp),
            contentAlignment = Alignment.Center
        ) {
            RadarChart(
                abilities = listOfAbilities,
                modifier = Modifier.size(200.dp)
            )
        }
    }
}

@Composable
private fun RadarChart(
    abilities: List<Ability>,
    modifier: Modifier = Modifier,
    maxValue: Int = 5
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface

    Canvas(modifier = modifier) {
        val center = size.center
        val radius = min(size.width, size.height) / 2 * 0.8f
        val angleStep = 360f / abilities.size

        // Draw grid circles
        for (i in 1..maxValue) {
            val gridRadius = radius * (i.toFloat() / maxValue)
            drawCircle(
                color = onSurfaceColor.copy(alpha = 0.2f),
                radius = gridRadius,
                center = center,
                style = Stroke(width = 1.dp.toPx())
            )
        }

        // Draw axes
        abilities.forEachIndexed { index, _ ->
            val angle = Math.toRadians((index * angleStep - 90).toDouble())
            val endX = center.x + cos(angle).toFloat() * radius
            val endY = center.y + sin(angle).toFloat() * radius

            drawLine(
                color = onSurfaceColor.copy(alpha = 0.3f),
                start = center,
                end = Offset(endX, endY),
                strokeWidth = 1.dp.toPx()
            )
        }

        // Draw ability polygon
        val path = Path()
        abilities.forEachIndexed { index, ability ->
            val angle = Math.toRadians((index * angleStep - 90).toDouble())
            val valueRadius = radius * (ability.value.toFloat() / maxValue)
            val x = center.x + cos(angle).toFloat() * valueRadius
            val y = center.y + sin(angle).toFloat() * valueRadius

            if (index == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }
        path.close()

        // Fill the polygon
        drawPath(
            path = path,
            color = primaryColor.copy(alpha = 0.3f)
        )

        // Draw the polygon border
        drawPath(
            path = path,
            color = primaryColor,
            style = Stroke(width = 2.dp.toPx())
        )

        // Draw points
        abilities.forEachIndexed { index, ability ->
            val angle = Math.toRadians((index * angleStep - 90).toDouble())
            val valueRadius = radius * (ability.value.toFloat() / maxValue)
            val x = center.x + cos(angle).toFloat() * valueRadius
            val y = center.y + sin(angle).toFloat() * valueRadius

            drawCircle(
                color = primaryColor,
                radius = 4.dp.toPx(),
                center = Offset(x, y)
            )
        }
    }
}

@Composable
private fun PlayerInfoSection(
    modifier: Modifier = Modifier,
    name: String,
    number: String,
    position: String,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            modifier = Modifier.padding(start = 16.dp, top = 16.dp),
            text = "Player Information",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
        )
        Text(
            modifier = Modifier.padding(start = 16.dp, top = 16.dp),
            text = name,
            style = MaterialTheme.typography.headlineMedium
        )
        Row(
            Modifier.padding(start = 16.dp, top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Number: ",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Thin
            )
            Text(
                text = number,
                style = MaterialTheme.typography.headlineSmall,
            )
            Text(
                modifier = Modifier.padding(start = 16.dp),
                text = "Position: ",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Thin
            )
            Text(
                text = position,
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}

@Composable
fun PlayerObjectives(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = "Objectives",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            Button(onClick = {}) {
                Text(text = "New Objective")
            }
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
                    name = "Tonela",
                    number = "1",
                    position = Position.CENTER,
                    id = 1,
                )
            ),
            modifier = Modifier.fillMaxSize()
        )
    }
}