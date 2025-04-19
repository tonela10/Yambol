package com.sedilant.yambol.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.sedilant.yambol.ui.home.models.TeamObjectivesUiModel

@Composable
fun TaskList(
    listOfTask: List<TeamObjectivesUiModel>,
    onAddObjective: () -> Unit,
    onToggleObjectiveStatus: (Int) -> Unit,
    onDeleteObjective: (Int) -> Unit,
    onUpdateObjective: (Int, String) -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = "Objectives",
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center)
            )

            IconButton(
                onClick = onAddObjective,
                modifier = Modifier.align(Alignment.CenterEnd),
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "add note"
                )
            }
        }
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(listOfTask) { task ->
                val isContextMenuVisible = remember { mutableStateOf(false) }
                val isEditDialogVisible = remember { mutableStateOf(false) }
                val editTextState = remember { mutableStateOf(task.description) }

                Box {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onLongPress = {
                                        isContextMenuVisible.value = true
                                    }
                                )
                            },
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Checkbox(
                            checked = task.isFinish,
                            onCheckedChange = { onToggleObjectiveStatus(task.id) },
                        )

                        Text(
                            text = task.description,
                            style = TextStyle(
                                textDecoration = if (task.isFinish) TextDecoration.LineThrough else null
                            ),
                            modifier = Modifier.weight(1f)
                        )

                        // Context menu icons that appear when long pressed
                        AnimatedVisibility(
                            visible = isContextMenuVisible.value,
                            enter = fadeIn() + expandHorizontally(),
                            exit = fadeOut() + shrinkHorizontally()
                        ) {
                            Row {
                                IconButton(
                                    onClick = {
                                        isEditDialogVisible.value = true
                                        isContextMenuVisible.value = false
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit objective",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        onDeleteObjective(task.id)
                                        isContextMenuVisible.value = false
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete objective",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }

                                // Close button for context menu
                                IconButton(
                                    onClick = { isContextMenuVisible.value = false }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Close menu"
                                    )
                                }
                            }
                        }
                    }

                    // Edit dialog
                    if (isEditDialogVisible.value) {
                        AlertDialog(
                            onDismissRequest = { isEditDialogVisible.value = false },
                            title = { Text("Edit Objective") },
                            text = {
                                TextField(
                                    value = editTextState.value,
                                    onValueChange = { editTextState.value = it },
                                    label = { Text("Objective description") },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        onUpdateObjective(task.id, task.description)
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
                            }
                        )
                    }
                }
            }
        }
    }
}
