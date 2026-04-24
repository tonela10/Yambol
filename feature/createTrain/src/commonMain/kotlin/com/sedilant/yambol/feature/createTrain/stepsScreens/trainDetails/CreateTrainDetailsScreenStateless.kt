package com.sedilant.yambol.feature.createTrain.stepsScreens.trainDetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sedilant.yambol.core.designsystem.Res
import com.sedilant.yambol.core.designsystem.concepts_worked
import com.sedilant.yambol.core.designsystem.error_loading_training
import com.sedilant.yambol.core.designsystem.error_unknown
import com.sedilant.yambol.core.designsystem.exercises_label
import com.sedilant.yambol.core.designsystem.general_information
import com.sedilant.yambol.core.designsystem.save_training
import com.sedilant.yambol.core.designsystem.saving
import com.sedilant.yambol.core.designsystem.training_details
import com.sedilant.yambol.core.designsystem.variation_label
import com.sedilant.yambol.feature.createTrain.commonComposables.CreateTrainScaffold
import com.sedilant.yambol.feature.createTrain.stepsScreens.tasks.TaskUI
import org.jetbrains.compose.resources.stringResource

@Composable
fun CreateTrainDetailsScreenStateless(
    onBack: () -> Unit,
    onClose: () -> Unit,
    onSave: () -> Unit,
    uiState: CreateTrainDetailsUiState
) {
    when (uiState) {
        is CreateTrainDetailsUiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is CreateTrainDetailsUiState.Error -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = stringResource(Res.string.error_loading_training),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                    Text(
                        text = uiState.throwable.message ?: stringResource(Res.string.error_unknown),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        is CreateTrainDetailsUiState.Success -> {
            CreateTrainScaffold(
                onBackClick = onBack,
                title = stringResource(Res.string.training_details),
                onCloseClick = onClose,
                onBottomButtonClick = {
                    if (!uiState.isSaving) {
                        onSave()
                    }
                },
                bottomButtonText = if (uiState.isSaving) stringResource(Res.string.saving) else stringResource(
                    Res.string.save_training
                ),
                showBackButton = true
            ) { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(paddingValues)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Loading overlay durante el guardado
                    if (uiState.isSaving) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    // General Information
                    GeneralInfo(trainInfo = uiState.trainInfo)

                    Spacer(modifier = Modifier.height(16.dp))

                    // Concepts Information
                    if (uiState.listOfConcepts.isNotEmpty()) {
                        ConceptsInfo(concepts = uiState.listOfConcepts)
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Tasks Information
                    if (uiState.listOfTasks.isNotEmpty()) {
                        TasksInfo(tasks = uiState.listOfTasks)
                    }
                }
            }
        }
    }
}

@Composable
private fun TasksInfo(tasks: List<TaskUI>) {
    Column {
        Text(
            text = stringResource(Res.string.exercises_label, tasks.size),
            style = MaterialTheme.typography.titleLarge,
        )
        Spacer(modifier = Modifier.height(8.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            tasks.forEach { task ->
                TaskItemDetail(task)
            }
        }
    }
}

@Composable
private fun TaskItemDetail(task: TaskUI) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                text = task.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )

            if (task.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = task.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (task.variation.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(Res.string.variation_label, task.variation),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (task.concepts.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = task.concepts.joinToString(", ") { it.conceptName },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ConceptsInfo(concepts: List<String>, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(Res.string.concepts_worked, concepts.size),
            style = MaterialTheme.typography.titleLarge,
        )
        Spacer(modifier = Modifier.height(8.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            concepts.forEach { concept ->
                SuggestionChip(
                    onClick = { },
                    label = { Text(concept) }
                )
            }
        }
    }
}

@Composable
private fun GeneralInfo(trainInfo: TrainInfo, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(Res.string.general_information),
            style = MaterialTheme.typography.titleLarge,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                InfoRow(field = "Fecha", data = trainInfo.date)
                HorizontalDivider()
                InfoRow(field = "Hora", data = trainInfo.hour)
                HorizontalDivider()
                InfoRow(field = "Duración", data = trainInfo.duration)
                if (trainInfo.team.isNotBlank()) {
                    HorizontalDivider()
                    InfoRow(field = "Equipo", data = trainInfo.team)
                }
            }
        }
    }
}

@Composable
private fun InfoRow(modifier: Modifier = Modifier, field: String, data: String) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = field,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = data,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}
