package com.sedilant.yambol.ui.createTrain.stepsScreens.concepts

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import org.koin.androidx.compose.koinViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sedilant.yambol.feature.createTrain.stepsScreens.concepts.CreateTrainConceptsScreenStateless

@Composable
fun CreateTrainConceptsScreen(
    onBack: () -> Unit,
    onNext: () -> Unit,
    onClose: () -> Unit,
    viewModel: CreateTrainConceptsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CreateTrainConceptsScreenStateless(
        uiState = uiState,
        onBack = onBack,
        onNext = onNext,
        onClose = onClose,
        onConceptSelected = viewModel::onConceptSelected,
        onAddConcept = viewModel::onAddConcept
    )
}
