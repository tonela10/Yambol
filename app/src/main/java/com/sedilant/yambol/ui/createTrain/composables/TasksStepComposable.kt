package com.sedilant.yambol.ui.createTrain.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sedilant.yambol.R
import com.sedilant.yambol.ui.createTrain.TrainTaskData
import com.sedilant.yambol.ui.theme.YambolTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksStep(
    tasks: List<TrainTaskData>,
    concepts: List<String>,
    onTaskAdded: (TrainTaskData) -> Unit,
    onTaskRemoved: (Int) -> Unit,
    onTaskUpdated: (Int, TrainTaskData) -> Unit
) {
    var showTaskBottomSheet by remember { mutableStateOf(false) }
    var editingTaskIndex by remember { mutableStateOf<Int?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = stringResource(R.string.add_specific_tasks_and_drills_for_this_training_optional),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Add task button
        item {
            OutlinedButton(
                onClick = {
                    editingTaskIndex = null
                    showTaskBottomSheet = true
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add New Task")
            }
        }

        // Tasks list
        if (tasks.isNotEmpty()) {
            item {
                Text(
                    text = "Tasks (${tasks.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
            }

            itemsIndexed(tasks) { index, task ->
                TaskCard(
                    task = task,
                    onEdit = {
                        editingTaskIndex = index
                        showTaskBottomSheet = true
                    },
                    onDelete = { onTaskRemoved(index) }
                )
            }
        } else {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.no_tasks_added_yet),
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = stringResource(R.string.tasks_are_optional_but_help_structure_your_training),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
    // Task creation/edit bottom sheet
    if (showTaskBottomSheet) {
        val taskToEdit = editingTaskIndex?.let { tasks[it] }
        ModalBottomSheet(
            onDismissRequest = { showTaskBottomSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            modifier = Modifier.fillMaxHeight()
        ) {
            TaskBottomSheetContent(
                initialTask = taskToEdit,
                concepts = concepts,
                onDismiss = { showTaskBottomSheet = false },
                onSave = { task ->
                    if (editingTaskIndex != null) {
                        onTaskUpdated(editingTaskIndex!!, task)
                    } else {
                        onTaskAdded(task)
                    }
                    showTaskBottomSheet = false
                }
            )
        }
    }
}

@Composable
fun TaskCard(
    task: TrainTaskData,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = task.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                Row {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit task")
                    }
                    IconButton(onClick = onDelete) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete task",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = task.concept,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${task.numberOfPlayer} players",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (task.description.isNotEmpty()) {
                Text(
                    text = task.description,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (task.variables.isNotEmpty()) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(task.variables) { variable ->
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = MaterialTheme.shapes.extraSmall
                        ) {
                            Text(
                                text = variable,
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TasksStepPreview() {
    YambolTheme {
        TasksStep(
            tasks = mutableListOf(),
            concepts = listOf("Shooting form", "Test concepts"),
            onTaskAdded = {},
            onTaskRemoved = {},
            onTaskUpdated = { _, _ -> },
        )
    }
}
