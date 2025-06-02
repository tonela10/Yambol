package com.sedilant.yambol.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sedilant.yambol.ui.home.models.TeamObjectivesUiModel
import com.sedilant.yambol.ui.theme.YambolTheme

@Composable
fun TaskList(
    listOfTask: List<TeamObjectivesUiModel>,
    onToggleObjectiveStatus: (Int) -> Unit,
    onDeleteObjective: (Int, String, Boolean) -> Unit,
    onUpdateObjective: (Int, String) -> Unit,
    onSaveNewObjective: (String) -> Unit,
    currentTeamId: Int,
    modifier: Modifier = Modifier
) {
    val isObjectiveDialogShow = remember { mutableStateOf(false) }

    if (isObjectiveDialogShow.value) {
        CreateObjectiveDialogComposable(
            onSave = onSaveNewObjective,
            onDismiss = { isObjectiveDialogShow.value = false },
        )
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Team Objectives",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                IconButton(
                    onClick = { isObjectiveDialogShow.value = true },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add objective",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            if (listOfTask.isEmpty()) {
                // Empty state
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No objectives yet",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Tap the + button to add your first objective",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(8.dp))

                // Task list - using Column instead of LazyColumn to avoid nested scrolling
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOfTask.forEach { task ->
                        ObjectiveItem(
                            objective = task,
                            onToggleStatus = { onToggleObjectiveStatus(task.id) },
                            onDelete = { onDeleteObjective(task.id, task.description, task.isFinish) },
                            onUpdate = { newDescription -> onUpdateObjective(task.id, newDescription) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ObjectiveItem(
    objective: TeamObjectivesUiModel,
    onToggleStatus: () -> Unit,
    onDelete: () -> Unit,
    onUpdate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isEditDialogVisible = remember { mutableStateOf(false) }
    val editTextState = remember { mutableStateOf(objective.description) }
    val isEditMenuShow = remember { mutableStateOf(false) }

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = if (objective.isFinish) {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onLongPress = {
                            isEditMenuShow.value = true
                        }
                    )
                },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Checkbox(
                checked = objective.isFinish,
                onCheckedChange = { onToggleStatus() },
            )

            Text(
                text = objective.description,
                style = TextStyle(
                    textDecoration = if (objective.isFinish) TextDecoration.LineThrough else null,
                    color = if (objective.isFinish) {
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                ),
                modifier = Modifier.weight(1f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // Context menu icons that appear when long pressed
            AnimatedVisibility(
                visible = isEditMenuShow.value,
                enter = fadeIn() + expandHorizontally(),
                exit = fadeOut() + shrinkHorizontally()
            ) {
                Row {
                    IconButton(
                        onClick = {
                            isEditDialogVisible.value = true
                            isEditMenuShow.value = false
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit objective",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(
                        onClick = {
                            onDelete()
                            isEditMenuShow.value = false
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete objective",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(
                        onClick = { isEditMenuShow.value = false }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close menu",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }

    // Edit dialog
    if (isEditDialogVisible.value) {
        AlertDialog(
            onDismissRequest = { isEditDialogVisible.value = false },
            title = {
                Text(
                    "Edit Objective",
                    style = MaterialTheme.typography.titleMedium
                )
            },
            text = {
                TextField(
                    value = editTextState.value,
                    onValueChange = { editTextState.value = it },
                    label = { Text("Objective description") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(8.dp)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onUpdate(editTextState.value)
                        isEditDialogVisible.value = false
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { isEditDialogVisible.value = false }
                ) {
                    Text("Cancel")
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TaskListPreview() {
    YambolTheme {
        TaskList(
            listOf(
                TeamObjectivesUiModel("Correr 10 minutos", false, 1),
                TeamObjectivesUiModel("Ejercicio de bote", false, 2),
                TeamObjectivesUiModel("Que todos metan dos libre", true, 3),
            ),
            onToggleObjectiveStatus = {},
            onDeleteObjective = { _, _, _ -> },
            onUpdateObjective = { _, _ -> },
            onSaveNewObjective = {},
            currentTeamId = 1,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EmptyTaskListPreview() {
    YambolTheme {
        TaskList(
            listOf(),
            onToggleObjectiveStatus = {},
            onDeleteObjective = { _, _, _ -> },
            onUpdateObjective = { _, _ -> },
            onSaveNewObjective = {},
            currentTeamId = 1,
        )
    }
}