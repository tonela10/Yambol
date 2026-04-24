package com.sedilant.yambol.feature.createTrain.stepsScreens.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sedilant.yambol.feature.createTrain.stepsScreens.concepts.Concept

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
internal fun AddTaskBottomSheet(
    onAddTask: (name: String, description: String, concepts: List<String>, variants: List<String>) -> Unit,
    onDismiss: () -> Unit,
    concepts: List<Concept>,
    conceptsError: String? = null
) {
    var taskName by remember { mutableStateOf("") }
    var taskDescription by remember { mutableStateOf("") }

    val selectedConceptIds = remember { mutableStateListOf<String>() }

    val taskVariants = remember { mutableStateListOf("Var 1") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onDismiss) {
                Icon(Icons.Default.Close, contentDescription = "Cerrar")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Nuevo ejercicio",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Exercise Name
        Text("Nombre del ejercicio", style = MaterialTheme.typography.labelLarge)
        OutlinedTextField(
            value = taskName,
            onValueChange = { taskName = it },
            placeholder = { Text("Ej. Tiro libre") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Description
        Text("Descripción", style = MaterialTheme.typography.labelLarge)
        OutlinedTextField(
            value = taskDescription,
            onValueChange = { taskDescription = it },
            placeholder = { Text("Describe los pasos...") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Concept Selection Logic
        Text("Seleccionar conceptos", style = MaterialTheme.typography.labelLarge)
        Spacer(modifier = Modifier.height(8.dp))

        // Render all available concepts as selectable chips
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            concepts.forEach { concept ->
                val isSelected = selectedConceptIds.contains(concept.id)
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        if (isSelected) selectedConceptIds.remove(concept.id)
                        else selectedConceptIds.add(concept.id)
                    },
                    label = { Text(concept.conceptName) },
                    leadingIcon = if (isSelected) {
                        {
                            Icon(
                                Icons.Default.Add,
                                null,
                                Modifier.size(FilterChipDefaults.IconSize)
                            )
                        }
                    } else null
                )
            }
        }

        conceptsError?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Variants Section
        Text("Variantes", style = MaterialTheme.typography.labelLarge)
        taskVariants.forEachIndexed { index, variant ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = variant,
                    onValueChange = { taskVariants[index] = it },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
                IconButton(onClick = { taskVariants.removeAt(index) }) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }

        TextButton(onClick = { taskVariants.add("") }) {
            Icon(Icons.Default.Add, null)
            Text("Añadir variante")
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Action Buttons
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                Text("Cancelar")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(
                onClick = {
                    onAddTask(
                        taskName,
                        taskDescription,
                        selectedConceptIds.toList(),
                        taskVariants.filter { it.isNotBlank() }.toList()
                    )
                },
                modifier = Modifier.weight(1f),
                enabled = taskName.isNotBlank() && selectedConceptIds.isNotEmpty()
            ) {
                Text("Guardar")
            }
        }
    }
}
