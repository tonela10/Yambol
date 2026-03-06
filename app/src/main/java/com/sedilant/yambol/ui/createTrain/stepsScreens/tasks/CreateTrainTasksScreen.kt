package com.sedilant.yambol.ui.createTrain.stepsScreens.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.sedilant.yambol.ui.createTrain.commonComposables.CreateTrainScaffold

@Composable
fun CreateTrainTasksScreen(
    onBack: () -> Unit,
    onNext: () -> Unit,
    onClose: () -> Unit,
    viewModel: CreateTrainTasksViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    CreateTrainTasksScreenStateless(
        uiState = uiState,
        onMove = viewModel::onTasksMove,
        onAddTask = viewModel::onAddTask,
        onDeleteTask = viewModel::onDeleteTask,
        onBack = onBack,
        onNext = onNext, // saveStepData removed as it's now reactive
        onClose = onClose,
        onClearError = viewModel::clearError,
        onTaskSelected = viewModel::onTaskSelected
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateTrainTasksScreenStateless(
    uiState: CreateTrainTasksViewModel.UiStateNew,
    onBack: () -> Unit,
    onNext: () -> Unit,
    onClose: () -> Unit,
    onClearError: () -> Unit,
    onMove: (from: Int, to: Int) -> Unit,
    onAddTask: (name: String, description: String, variation: List<String>, concepts: List<String>) -> Unit,
    onTaskSelected: (TaskUI) -> Unit,
    onDeleteTask: (taskId: String) -> Unit
) {
    var showCreateTaskBottomSheet by remember { mutableStateOf(false) }
    var showExistingTasksBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    CreateTrainScaffold(
        title = "Nuevo Entrenamiento",
        onCloseClick = onClose,
        onBottomButtonClick = onNext,
        bottomButtonText = "Siguiente",
        showBackButton = true,
        onBackClick = onBack,
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Handle different UI states
            when (uiState) {
                is CreateTrainTasksViewModel.UiStateNew.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                is CreateTrainTasksViewModel.UiStateNew.Error -> {
                    // Centralized error view or a Snackbar
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = uiState.message, color = MaterialTheme.colorScheme.error)
                            Button(onClick = onClearError) { Text("Reintentar") }
                        }
                    }
                }

                is CreateTrainTasksViewModel.UiStateNew.Success -> {
                    Text(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        text = "¿QUÉ EJERCICIOS HARÁS?",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleLarge
                    )

                    AddTaskButton(onClick = { showCreateTaskBottomSheet = true })

                    OutlinedButton(
                        onClick = { showExistingTasksBottomSheet = true },
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .fillMaxWidth()
                            .height(40.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Añadir Ejercicio existente")
                    }

                    if (uiState.draftTasks.isNotEmpty()) {
                        Text(
                            text = "${uiState.draftTasks.size} ejercicio(s) añadido(s)",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                        )
                    }

                    ListOfTasks(
                        listOfTasks = uiState.draftTasks,
                        onMove = onMove,
                        onDelete = onDeleteTask,
                        isLoading = false,
                    )


                    // Bottom Sheet Logic
                    if (showCreateTaskBottomSheet) {
                        ModalBottomSheet(
                            onDismissRequest = { showCreateTaskBottomSheet = false },
                            sheetState = sheetState
                        ) {
                            AddTaskBottomSheet(
                                onAddTask = { name, description, concepts, variation ->
                                    onAddTask(name, description, variation, concepts)
                                    showCreateTaskBottomSheet = false
                                },
                                onDismiss = { showCreateTaskBottomSheet = false },
                                concepts = uiState.listOfConcept,
                            )
                        }
                    }

                    if (showExistingTasksBottomSheet) {
                        ModalBottomSheet(
                            onDismissRequest = { showExistingTasksBottomSheet = false },
                            sheetState = sheetState
                        ) {
                            AddExistingTaskBottomSheet(
                                existingTasks = uiState.existingTasks,
                                onTaskSelected = { task ->
                                    onTaskSelected(task)
                                    showExistingTasksBottomSheet = false
                                },
                                onDismiss = { showExistingTasksBottomSheet = false }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AddTaskButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        // Remove the padding by default to have total control
        contentPadding = PaddingValues(0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.padding(8.dp),
                text = "Añadir nuevo ejercicio",
                style = MaterialTheme.typography.bodyLarge
            )
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add",
                modifier = Modifier
                    .padding(8.dp)
                    .clip(RoundedCornerShape(size = 8.dp))
                    .background(MaterialTheme.colorScheme.inversePrimary)
            )
        }
    }
}
