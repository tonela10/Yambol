package com.sedilant.yambol.ui.createTrain.stepsScreens.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.sedilant.yambol.ui.createTrain.commonComposables.CreateTrainScaffold
import sh.calvin.reorderable.rememberReorderableLazyListState

@Composable
fun CreateTrainTasksScreen() {
    val viewModel: CreateTrainTasksViewModel = hiltViewModel()
}

@Composable
private fun CreateTrainTasksScreenStateless() {

    CreateTrainScaffold(
        title = "Nuevo Entrenamiento",
        onCloseClick = {},
        onBottomButtonClick = {},
        bottomButtonText = "CREAR ENTRENAMIENTO",
        showBackButton = true,
        onBackClick = {},
    ) { innerPadding ->
        // ADD THE PROGRESS BAR TO THE SCAFFOLD AND PASS IT AS A PARAMETER
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // PROGRESS BAR
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
        }
    }
}

@Composable
private fun ListOfTasks(
    listOfTasks: List<String>,
    modifier: Modifier = Modifier
) {
    val lazyListState = rememberLazyListState()
    val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
    }
    LazyColumn() { }
}

@Preview
@Composable
fun CreateTrainTasksScreenPreview() {
    CreateTrainTasksScreenStateless()
}