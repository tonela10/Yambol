package com.sedilant.yambol.ui.createTrain.stepsScreens.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AddTaskBottomSheet(
    onAddTask: (name: String, description: String, concepts: List<String>, variants: List<String>) -> Unit,
    onDismiss: () -> Unit
) {
    var taskName by remember { mutableStateOf("") }
    var taskDescription by remember { mutableStateOf("") }
    var conceptInput by remember { mutableStateOf("") }
    val currentConcepts = remember { mutableStateListOf<String>() }
    val taskVariants = remember {
        mutableStateListOf(
            "Var 1",
            "Var 2"
        )
    }

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
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Cerrar",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Nuevo ejercicio",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Spacer(modifier = Modifier.height(24.dp))

        // Campo: Nombre del ejercicio
        Text(
            text = "Nombre del ejercicio",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = taskName,
            onValueChange = { taskName = it },
            placeholder = { Text("Ej. Tiro libre") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo: Descripción
        Text(
            text = "Descripción",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = taskDescription,
            onValueChange = { taskDescription = it },
            placeholder = { Text("Describe los pasos y objetivos del ejercicio...") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3, // Multi-line
            maxLines = 5,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Chips
        Text(
            text = "Concepto",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = conceptInput,
            onValueChange = {
                conceptInput = it
                // Here can be add the logic to suggest concepts or add when pressing Enter
            },
            placeholder = { Text("Buscar o crear concepto...") },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Search, contentDescription = "Buscar")
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors()
        )

        // Example of adding a concept when pressing Enter (just for simulation)
        LaunchedEffect(conceptInput) {
            if (conceptInput.endsWith("\n") && conceptInput.isNotBlank()) {
                val newConcept = conceptInput.trim()
                if (newConcept.isNotEmpty() && !currentConcepts.contains(newConcept)) {
                    currentConcepts.add(newConcept)
                }
                conceptInput = ""
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Concepts
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            currentConcepts.forEach { concept ->
                FilterChip(
                    selected = true,
                    onClick = { currentConcepts.remove(concept) },
                    label = { Text(concept) },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Eliminar concepto",
                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        selectedTrailingIconColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Variants
        Text(
            text = "Variantes",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))

        taskVariants.forEachIndexed { index, variant ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = variant,
                    onValueChange = { newValue ->
                        taskVariants[index] = newValue
                    },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors()
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = { taskVariants.removeAt(index) },
                    modifier = Modifier.size(48.dp) // Ajustar tamaño del IconButton
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar variante",
                        tint = MaterialTheme.colorScheme.error // Color rojo para eliminar
                    )
                }
            }
        }
        // Botón para añadir nueva variante
        TextButton(onClick = { taskVariants.add("") }) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Añadir variante")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Añadir variante")
        }


        Spacer(modifier = Modifier.height(32.dp))

        // Botones de acción (Cancelar y Guardar)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancelar")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(
                onClick = {
                    //  onAddTask: (name: String, description: String, concepts: List<String>, variants: List<String>) -> Unit,
                    onAddTask(
                        taskName,
                        taskDescription,
                        currentConcepts.toList(),
                        taskVariants.filter { it.isNotBlank() }.toList() // variants
                    )
                    onDismiss()
                },
                modifier = Modifier.weight(1f),
                enabled = taskName.isNotBlank() // Solo habilitar si el nombre no está vacío
            ) {
                Text("Guardar")
            }
        }
    }
}

@Preview(showBackground = true, name = "1. Preview: Bottom Sheet Añadir Tarea")
@Composable
private fun AddTaskBottomSheetPreview() {
    MaterialTheme {
        Surface {
            // Pasamos lambdas vacías, ya que la lógica de guardado no importa en la preview
            AddTaskBottomSheet(
                onAddTask = { _, _, _, _ -> },
                onDismiss = {}
            )
        }
    }
}