package com.sedilant.yambol.ui.home

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskList(
    listOfTask: List<TeamObjectivesUiModel>,
    onToggleObjectiveStatus: (Int) -> Unit,
    onDeleteObjective: (Int, String, Boolean) -> Unit,
    onUpdateObjective: (Int, String) -> Unit,
    onSaveNewObjective: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isBottomSheetVisible = remember { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val scope = rememberCoroutineScope()

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
                    onClick = { isBottomSheetVisible.value = true },
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

                // Task list
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOfTask.forEach { task ->
                        ObjectiveItem(
                            objective = task,
                            onToggleStatus = { onToggleObjectiveStatus(task.id) },
                            onDelete = {
                                onDeleteObjective(
                                    task.id,
                                    task.description,
                                    task.isFinish
                                )
                            },
                            onUpdate = { newDescription ->
                                onUpdateObjective(
                                    task.id,
                                    newDescription
                                )
                            }
                        )
                    }
                }
            }
        }
    }

    if (isBottomSheetVisible.value) {
        ModalBottomSheet(
            onDismissRequest = { isBottomSheetVisible.value = false },
            sheetState = bottomSheetState,
        ) {
            CreateObjectiveBottomSheetContent(
                onSave = { description ->
                    onSaveNewObjective(description)
                    scope.launch {
                        bottomSheetState.hide()
                        isBottomSheetVisible.value = false
                    }
                },
                onCancel = {
                    scope.launch {
                        bottomSheetState.hide()
                        isBottomSheetVisible.value = false
                    }
                }
            )
        }
    }
}

@Composable
private fun CreateObjectiveBottomSheetContent(
    onSave: (String) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val textState = remember { mutableStateOf("") }
    val isError = remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp)
            .padding(bottom = 32.dp), // Extra padding for better bottom sheet appearance
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Add New Objective",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = textState.value,
            onValueChange = {
                textState.value = it
                isError.value = false
            },
            label = { Text("Objective description") },
            placeholder = { Text("e.g., Score 10 free throws in a row") },
            modifier = Modifier.fillMaxWidth(),
            isError = isError.value,
            supportingText = if (isError.value) {
                { Text("Please enter an objective description") }
            } else null,
            shape = RoundedCornerShape(12.dp),
            maxLines = 3
        )

        Text(
            text = "Describe what the team should achieve during training",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 4.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Cancel")
            }

            Button(
                onClick = {
                    if (textState.value.trim().isNotEmpty()) {
                        onSave(textState.value.trim())
                    } else {
                        isError.value = true
                    }
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Add Objective")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ObjectiveItem(
    objective: TeamObjectivesUiModel,
    onToggleStatus: () -> Unit,
    onDelete: () -> Unit,
    onUpdate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isEditBottomSheetVisible = remember { mutableStateOf(false) }
    val editBottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val scope = rememberCoroutineScope()

    Card(
        modifier = modifier
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        onToggleStatus()
                    },
                    onLongPress = {
                        isEditBottomSheetVisible.value = true
                    }
                )
            },
        colors = CardDefaults.cardColors(
            containerColor = if (objective.isFinish) {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
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
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,

            ) {
            Checkbox(
                checked = objective.isFinish,
                onCheckedChange = null, // Disable checkbox direct interaction
                enabled = false // Visual indicator only
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
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 4.dp),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }

    if (isEditBottomSheetVisible.value) {
        ModalBottomSheet(
            onDismissRequest = { isEditBottomSheetVisible.value = false },
            sheetState = editBottomSheetState,
        ) {
            ObjectiveOptionsBottomSheetContent(
                objective = objective,
                onEdit = { newDescription ->
                    onUpdate(newDescription)
                    scope.launch {
                        editBottomSheetState.hide()
                        isEditBottomSheetVisible.value = false
                    }
                },
                onDelete = {
                    onDelete()
                    scope.launch {
                        editBottomSheetState.hide()
                        isEditBottomSheetVisible.value = false
                    }
                },
                onCancel = {
                    scope.launch {
                        editBottomSheetState.hide()
                        isEditBottomSheetVisible.value = false
                    }
                }
            )
        }
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
        )
    }
}

@Composable
private fun ObjectiveOptionsBottomSheetContent(
    objective: TeamObjectivesUiModel,
    onEdit: (String) -> Unit,
    onDelete: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val textState = remember { mutableStateOf(objective.description) }
    val isError = remember { mutableStateOf(false) }
    val showDeleteConfirmation = remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp)
            .padding(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Edit Objective",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (objective.isFinish) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Completed",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "This objective is completed",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }

        OutlinedTextField(
            value = textState.value,
            onValueChange = {
                textState.value = it
                isError.value = false
            },
            label = { Text("Objective description") },
            modifier = Modifier.fillMaxWidth(),
            isError = isError.value,
            supportingText = if (isError.value) {
                { Text("Please enter an objective description") }
            } else null,
            shape = RoundedCornerShape(12.dp),
            maxLines = 3
        )

        Spacer(modifier = Modifier.height(8.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    if (textState.value.trim().isNotEmpty()) {
                        onEdit(textState.value.trim())
                    } else {
                        isError.value = true
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text("Save Changes")
            }

            OutlinedButton(
                onClick = { showDeleteConfirmation.value = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.error
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text("Delete Objective")
            }

            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Cancel")
            }
        }
    }

    if (showDeleteConfirmation.value) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation.value = false },
            title = {
                Text(
                    "Delete Objective",
                    style = MaterialTheme.typography.titleMedium
                )
            },
            text = {
                Text(
                    "Are you sure you want to delete this objective? This action cannot be undone.",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirmation.value = false
                        onDelete()
                    }
                ) {
                    Text(
                        "Delete",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteConfirmation.value = false }
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
private fun CreateObjectiveBottomSheetPreview() {
    YambolTheme {
        CreateObjectiveBottomSheetContent(
            onSave = {},
            onCancel = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ObjectiveOptionsBottomSheetPreview() {
    YambolTheme {
        ObjectiveOptionsBottomSheetContent(
            objective = TeamObjectivesUiModel("Correr 10 minutos", false, 1),
            onEdit = {},
            onDelete = {},
            onCancel = {}
        )
    }
}