package com.sedilant.yambol.ui.createTrain.stepsScreens.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sedilant.yambol.ui.createTrain.stepsScreens.concepts.Concept

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddExistingTaskBottomSheet(
    existingTasks: List<TaskUI>, // Passed from ViewModel
    onTaskSelected: (TaskUI) -> Unit,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    // Filter the list based on search input
    val filteredTasks = remember(searchQuery, existingTasks) {
        existingTasks.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
                    it.concepts.any { concept ->
                        concept.conceptName.contains(
                            searchQuery,
                            ignoreCase = true
                        )
                    }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 400.dp, max = 700.dp) // Control height for search usability
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onDismiss) {
                Icon(Icons.Default.Close, contentDescription = "Cerrar")
            }
            Text(
                text = "Ejercicios Existentes",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text(text = "Buscar por nombre o concepto...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = if (searchQuery.isNotEmpty()) {
                {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Close, contentDescription = "Limpiar")
                    }
                }
            } else null,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Results List
        if (filteredTasks.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = if (searchQuery.isEmpty()) "No hay tareas guardadas" else "No se encontraron resultados",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredTasks) { task ->
                    ExistingTaskItem(
                        task = task,
                        onClick = {
                            onTaskSelected(task)
                            onDismiss()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ExistingTaskItem(
    task: TaskUI,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                if (task.concepts.isNotEmpty()) {
                    Text(
                        text = task.concepts.joinToString(", ") { it.conceptName },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Preview(showBackground = true, name = "Buscador de Tareas Existentes")
@Composable
private fun AddExistingTaskPreview() {
    val mockExistingTasks = listOf(
        TaskUI(
            id = "1",
            name = "Rueda de entradas",
            concepts = listOf(Concept(1, "Finalizaciones"), Concept(2, "Táctica")),
            description = "Entradas por derecha e izquierda",
            variation = "Sin defensa",
            duration = "0"
        ),
        TaskUI(
            id = "2",
            name = "3x3 Continuo",
            concepts = listOf(Concept(1, "Finalizaciones"), Concept(2, "Táctica")),
            description = "Juego real a media pista",
            variation = "Con comodín",
            duration = "0"
        ),
        TaskUI(
            id = "3",
            name = "Tiros libres",
            concepts = listOf(Concept(1, "Finalizaciones"), Concept(2, "Táctica")),
            description = "Series de 10 tiros",
            variation = "Bajo presión",
            duration = "0"
        )
    )

    MaterialTheme {
        Surface {
            AddExistingTaskBottomSheet(
                existingTasks = mockExistingTasks,
                onTaskSelected = { /* No-op */ },
                onDismiss = { /* No-op */ }
            )
        }
    }
}