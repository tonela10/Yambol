package com.sedilant.yambol.ui.createTrain.stepsScreens.tasks

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.koin.androidx.compose.koinViewModel
import com.sedilant.yambol.feature.createTrain.stepsScreens.tasks.CreateTrainTasksScreenStateless

@Composable
fun CreateTrainTasksScreen(
    onBack: () -> Unit,
    onNext: () -> Unit,
    onClose: () -> Unit,
    viewModel: CreateTrainTasksViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    CreateTrainTasksScreenStateless(
        uiState = uiState,
        onMove = viewModel::onTasksMove,
        onAddTask = viewModel::onAddTask,
        onDeleteTask = viewModel::onDeleteTask,
        onBack = onBack,
        onNext = onNext,
        onClose = onClose,
        onClearError = viewModel::clearError,
        onTaskSelected = viewModel::onTaskSelected,
        onClearTaskConceptError = viewModel::clearTaskConceptError
    )
}
