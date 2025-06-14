package com.sedilant.yambol.ui.playerCard.composables

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sedilant.yambol.ui.playerCard.StatUiModel
import com.sedilant.yambol.ui.theme.YambolTheme

@Composable
fun PlayerAbilityCard(
    modifier: Modifier = Modifier,
    listOfAbilities: List<StatUiModel>,
) {
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(4.dp),
        //|colors = CardDefaults.cardColors(MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Abilities",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))

            listOfAbilities.forEach { ability ->
                AbilityRow(ability)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
private fun AbilityRow(ability: StatUiModel, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = ability.name.substringBefore(" "),
            style = MaterialTheme.typography.bodyLarge,
            maxLines = 1,
        )
        LinearProgressIndicator(
            modifier = Modifier
                .padding(start = 8.dp)
                .weight(3f),
            progress = {
                ability.value.div(5f) ?: 0f
            }, // Fixed: Convert to 0-1 range (assuming max value is 5)
        )
        Text(
            modifier = Modifier
                .padding(start = 4.dp)
                .weight(1f),
            text = String.format("%.2f", ability.value),
            textAlign = TextAlign.Center,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PlayerAbilityCardPreview() {
    YambolTheme {
        PlayerAbilityCard(
            modifier = Modifier,
            listOfAbilities = listOf(
                StatUiModel(
                    id = 1,
                    name = "shooting too much",
                    value = 3.6666665f
                )
            )
        )
    }
}

