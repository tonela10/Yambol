package com.sedilant.yambol.ui.training.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sedilant.yambol.R
import com.sedilant.yambol.domain.models.TrainDomainModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TrainingList(
    modifier: Modifier = Modifier,
    trainings: List<TrainDomainModel>,
    onTrainingClick: (Int) -> Unit = {},
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(trainings) { training ->
            TrainingItem(
                training = training,
                onClick = { onTrainingClick(training.id.toInt()) }
            )
        }
    }
}

@Composable
private fun TrainingItem(
    training: TrainDomainModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.calendar_month_24dp),
                        contentDescription = "Date",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = formatDate(training.date),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.schedule_24dp), // TODO Clock icon
                        contentDescription = "Duration",
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = formatDuration(training.time),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            if (training.concepts.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        painter = painterResource(R.drawable.target_24dp), // TODO SportsSoccer icon
                        contentDescription = "Concepts",
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(18.dp)
                    )

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Training Focus:",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )

                        ConceptsRow(concepts = training.concepts)
                    }
                }
            }
        }
    }
}

// TODO fix when the concepts does not fit in a row. LazyGrid?
@Composable
private fun ConceptsRow(
    concepts: List<String>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        concepts.forEachIndexed { index, concept ->
            Text(
                text = concept,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (index < concepts.size - 1) {
                VerticalDivider(
                    modifier = Modifier
                        .height(16.dp)
                        .padding(horizontal = 8.dp),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}

private fun formatDate(date: Date): String {
    val formatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    return formatter.format(date)
}

private fun formatDuration(hours: Float): String {
    return when {
        hours == 1f -> "1 hour"
        hours == hours.toInt().toFloat() -> "${hours.toInt()} hours"
        else -> "$hours hours"
    }
}

@Preview(showBackground = true)
@Composable
private fun TrainingListPreview() {
    MaterialTheme {
        val sampleTrainings = listOf(
            TrainDomainModel(
                id = 1,
                date = Date(),
                time = 1.5f,
                concepts = listOf(
                    "Dribbling",
                    "Ball handling",
                    "Basic moves",
                    "Footwork so fast that anyone can see"
                ),
                teamId = 1
            ),
            TrainDomainModel(
                id = 2,
                date = Date(System.currentTimeMillis() + 86400000), // Tomorrow
                time = 2f,
                concepts = listOf("Shooting", "Free throws"),
                teamId = 1
            ),
            TrainDomainModel(
                id = 3,
                date = Date(System.currentTimeMillis() + 172800000), // Day after tomorrow
                time = 1f,
                concepts = listOf("Defense", "Man-to-man", "Zone defense"),
                teamId = 1
            )
        )

        TrainingList(
            trainings = sampleTrainings,
            onTrainingClick = { }
        )
    }
}
