package com.sedilant.yambol.ui.createTrain.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sedilant.yambol.ui.createTrain.TrainTaskData
import com.sedilant.yambol.ui.theme.YambolTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskBottomSheetContent(
    initialTask: TrainTaskData? = null,
    concepts: List<String>,
    onDismiss: () -> Unit,
    onSave: (TrainTaskData) -> Unit
) {
    var name by remember { mutableStateOf(initialTask?.name ?: "") }
    var numberOfPlayer by remember {
        mutableStateOf(
            initialTask?.numberOfPlayer?.toString() ?: "8"
        )
    }
    var selectedConcept by remember {
        mutableStateOf(
            initialTask?.concept ?: concepts.firstOrNull() ?: ""
        )
    }
    var description by remember { mutableStateOf(initialTask?.description ?: "") }
    var variablesText by remember {
        mutableStateOf(
            initialTask?.variables?.joinToString(", ") ?: ""
        )
    }
    var showConceptDropdown by remember { mutableStateOf(false) }


    // Header
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .systemBarsPadding()
            .verticalScroll(rememberScrollState())

    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (initialTask != null) "Edit Task" else "Create New Task",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close"
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Form content in scrollable column
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Task Name") },
                    placeholder = { Text("e.g., Ball Control Drill") },
                    modifier = Modifier
                        .fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        autoCorrectEnabled = true,
                        imeAction = ImeAction.Next,
                    )
                )
            }

            item {
                OutlinedTextField(
                    value = numberOfPlayer,
                    onValueChange = { numberOfPlayer = it },
                    label = { Text("Number of Players") },
                    placeholder = { Text("e.g., 8") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier
                        .fillMaxWidth(),
                    singleLine = true
                )
            }

            item {
                ExposedDropdownMenuBox(
                    expanded = showConceptDropdown,
                    onExpandedChange = { showConceptDropdown = it }
                ) {
                    OutlinedTextField(
                        value = selectedConcept,
                        onValueChange = { },
                        label = { Text("Concept") },
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = showConceptDropdown)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    )

                    ExposedDropdownMenu(
                        expanded = showConceptDropdown,
                        onDismissRequest = { showConceptDropdown = false }
                    ) {
                        concepts.forEach { concept ->
                            DropdownMenuItem(
                                text = { Text(concept) },
                                onClick = {
                                    selectedConcept = concept
                                    showConceptDropdown = false
                                }
                            )
                        }
                    }
                }
            }

            item {
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    placeholder = { Text("Describe the task in detail...") },
                    modifier = Modifier
                        .fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Next,
                        autoCorrectEnabled = true,
                    )
                )
            }

            item {
                OutlinedTextField(
                    value = variablesText,
                    onValueChange = { variablesText = it },
                    label = { Text("Variables (comma separated)") },
                    placeholder = { Text("Speed, Distance, Time, Difficulty") },
                    modifier = Modifier
                        .fillMaxWidth(),
                    minLines = 2,
                    maxLines = 3,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Done,
                        autoCorrectEnabled = true,
                    ),
                    keyboardActions = KeyboardActions(onDone = {
                        val variables =
                            variablesText.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                        onSave(
                            TrainTaskData(
                                name = name,
                                numberOfPlayer = numberOfPlayer.toIntOrNull() ?: 8,
                                concept = selectedConcept,
                                description = description,
                                variables = variables
                            )
                        )
                    })
                )
            }

            item {
                Text(
                    text = "Variables help you track and modify the task during training",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Action buttons
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancel")
            }

            Button(
                onClick = {
                    val variables =
                        variablesText.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                    onSave(
                        TrainTaskData(
                            name = name,
                            numberOfPlayer = numberOfPlayer.toIntOrNull() ?: 8,
                            concept = selectedConcept,
                            description = description,
                            variables = variables
                        )
                    )
                },
                enabled = name.isNotBlank() && selectedConcept.isNotBlank(),
                modifier = if (name.isNotBlank() && selectedConcept.isNotBlank()) {
                    Modifier
                        .weight(1f)
                        .imePadding()
                } else {
                    Modifier.weight(1f)
                }
            ) {
                Text("Save Task")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TaskBottomSheetContentPreview() {
    YambolTheme {
        TaskBottomSheetContent(
            concepts = listOf("Pivot foot", "finishing with contact"),
            onDismiss = {},
            onSave = {},
        )
    }
}
