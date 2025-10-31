package com.sedilant.yambol.ui.createTrain.stepsScreens.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.sedilant.yambol.ui.createTrain.commonComposables.CreateTrainScaffold
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@Composable
fun CreateTrainTasksScreen(
    viewModel: CreateTrainTasksViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsState()

    CreateTrainTasksScreenStateless(
        tasks = uiState.tasksList,
        onMove = viewModel::onTasksMove
    )
}

@Composable
private fun CreateTrainTasksScreenStateless(
    tasks: List<Task> = emptyList(),
    onMove: (from: Int, to: Int) -> Unit
) {
    CreateTrainScaffold(
        title = "Nuevo Entrenamiento",
        onCloseClick = {},
        onBottomButtonClick = {},
        bottomButtonText = "CREAR ENTRENAMIENTO",
        showBackButton = true,
        onBackClick = {},
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = "¿QUE EJERCICIOS HARÁS?",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        MaterialTheme.colorScheme.primaryContainer
                    ),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    modifier = Modifier.padding(8.dp),
                    text = "Añadir nuevo ejercicio",
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyLarge
                )
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add",
                    modifier = Modifier
                        .padding(8.dp)
                        .clip(
                            RoundedCornerShape(
                                size = 8
                                    .dp
                            )
                        )
                        .background(MaterialTheme.colorScheme.inversePrimary)
                )
            }
            OutlinedButton(
                onClick = {}, modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .height(36.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Añadir Ejercicio existente")
            }
            ListOfTasks(
                listOfTasks = tasks,
                onMove = onMove
            )
        }
    }
}

@Composable
private fun ListOfTasks(
    listOfTasks: List<Task>,
    onMove: (from: Int, to: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val lazyListState = rememberLazyListState()
    val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
        onMove(from.index, to.index)
    }
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        state = lazyListState,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(listOfTasks, key = { it.id }) { task ->
            ReorderableItem(reorderableLazyListState, key = task.id) {
                TaskItem(task = task)
            }
        }
    }
}

@Composable
private fun TaskItem(task: Task, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = task.name,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        Text(text = task.duration, style = MaterialTheme.typography.bodyMedium)
    }
}


@Preview(showBackground = true)
@Composable
fun CreateTrainTasksScreenPreview() {
    val tasks = listOf(
        Task("1", "Sentadillas", "3x12"),
        Task("2", "Press Banca", "3x10"),
        Task("3", "Dominadas", "3x8")
    )
    CreateTrainTasksScreenStateless(
        tasks = tasks,
        onMove = { _, _ -> }
    )

}