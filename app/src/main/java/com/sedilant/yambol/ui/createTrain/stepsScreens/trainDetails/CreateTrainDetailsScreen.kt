package com.sedilant.yambol.ui.createTrain.stepsScreens.trainDetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sedilant.yambol.ui.createTrain.commonComposables.CreateTrainScaffold
import com.sedilant.yambol.ui.createTrain.stepsScreens.tasks.Task

@Composable
public fun CreateTrainDetailsScreenV2S() {
    CreateTrainDetailsScreenV2Stateless()
}

@Composable
public fun CreateTrainDetailsScreenV2Stateless() {
    CreateTrainScaffold(
        onBackClick = { /*TODO*/ },
        title = "Detalles del entrenamiento",
        onCloseClick = { /*TODO*/ },
        onBottomButtonClick = { /*TODO*/ },
        bottomButtonText = "Guardar entrenamiento",
        showBackButton = true
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // General Information
            GeneralInfo()

            Spacer(modifier = Modifier.height(16.dp))

            // Concepts Information
            ConceptsInfo()

            Spacer(modifier = Modifier.height(16.dp))

            // Tasks Information
            TasksInfo()
        }
    }
}

@Composable
private fun TasksInfo() { // TODO pass the list of exercise
    val exercises = listOf(
        Task(
            id = "1",
            name = "Tiro en suspensión desde la línea de 3",
            duration = "20 repeticiones"
        ),
        Task(id = "2", name = "Defensa individual en media cancha", duration = "15 minutos"),
        Task(id = "3", name = "Transiciones defensa-ataque", duration = "10 minutos"),
        Task(id = "4", name = "Transiciones defensa-ataque", duration = "10 minutos"),
        Task(id = "5", name = "Transiciones defensa-ataque", duration = "10 minutos")
    )

    Column {
        Text(
            text = "Ejercicios",
            style = MaterialTheme.typography.titleLarge,
        )
        Spacer(modifier = Modifier.height(8.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            exercises.forEach { exercise ->
                TaskItem(exercise)
            }
        }
    }
}

@Composable
private fun TaskItem(task: Task) {
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
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = task.duration,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ConceptsInfo(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = "Conceptos trabajados",
            style = MaterialTheme.typography.titleLarge,
        )
        Spacer(modifier = Modifier.height(8.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy((-8).dp)
        ) {
            val concepts = listOf("Tiro en suspensión", "Defensa individual", "Transiciones")
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
private fun GeneralInfo(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = "Información general",
            style = MaterialTheme.typography.titleLarge,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                InfoRow(field = "Fecha", data = "15 de marzo de 2024")
                HorizontalDivider()
                InfoRow(field = "Hora", data = "18:00")
                HorizontalDivider()
                InfoRow(field = "Duración", data = "1 hora 30 minutos")
                HorizontalDivider()
                InfoRow(field = "Equipo", data = "Equipo A")
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

@Preview
@Composable
private fun CreateTrainDetailsScreenV2Preview() {
    CreateTrainDetailsScreenV2Stateless()
}
