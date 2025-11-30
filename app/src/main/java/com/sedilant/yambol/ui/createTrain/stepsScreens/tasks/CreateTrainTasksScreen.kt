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
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
    onNext: () -> Unit, // Navegar a la siguiente pantalla del flow
    onClose: () -> Unit,
    viewModel: CreateTrainTasksViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Show error if any
    uiState.error?.let { error ->
        LaunchedEffect(error) {
            // Show a snackBar or something
            viewModel.clearError()
        }
    }

    CreateTrainTasksScreenStateless(
        tasks = uiState.tasksList,
        isLoading = uiState.isLoading,
        onMove = viewModel::onTasksMove,
        onAddTask = viewModel::onAddTask,
        onDeleteTask = viewModel::onDeleteTask,
        onBack = onBack,
        onNext = {
            viewModel.saveStepData() // Save before continue
            onNext()
        },
        onClose = onClose
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateTrainTasksScreenStateless(
    onBack: () -> Unit,
    onNext: () -> Unit,
    onClose: () -> Unit,
    tasks: List<TaskUI> = emptyList(),
    isLoading: Boolean = false,
    onMove: (from: Int, to: Int) -> Unit,
    onAddTask: (name: String, description: String, variation: List<String>, concepts: List<String>) -> Unit,
    onDeleteTask: (taskId: String) -> Unit
) {
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { it != SheetValue.Hidden }
    )

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
            if (showBottomSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showBottomSheet = false },
                    sheetState = sheetState
                ) {
                    AddTaskBottomSheet(
                        onAddTask = { name, description, variation, concepts ->
                            onAddTask(name, description, variation, concepts)
                            showBottomSheet = false
                        },
                        onDismiss = { showBottomSheet = false }
                    )
                }
            }

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = "¿QUÉ EJERCICIOS HARÁS?",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(16.dp))

            AddTaskButton(onClick = { showBottomSheet = true })

            OutlinedButton(
                onClick = {}, // TODO navigate to existing exercises screen
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .height(36.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Añadir Ejercicio existente")
            }

            // Task counter
            if (tasks.isNotEmpty()) {
                Text(
                    text = "${tasks.size} ejercicio(s) añadido(s)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            ListOfTasks(
                listOfTasks = tasks,
                onMove = onMove,
                onDelete = onDeleteTask,
                isLoading = isLoading,
            )
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
