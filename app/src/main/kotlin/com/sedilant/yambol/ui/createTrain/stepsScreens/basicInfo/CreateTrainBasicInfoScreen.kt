package com.sedilant.yambol.ui.createTrain.stepsScreens.basicInfo

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sedilant.yambol.feature.createTrain.stepsScreens.basicInfo.CreateTrainBasicInfoScreenStateless

@Composable
fun CreateTrainBasicInfoScreen(
    onClose: () -> Unit,
    onNext: () -> Unit,
    teamId: String,
    viewModel: CreateTrainBasicInfoViewModel = koinViewModel(
        parameters = { parametersOf(teamId) }
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CreateTrainBasicInfoScreenStateless(
        onClose = onClose,
        onNext = {
            viewModel.saveStepData()
            onNext()
        },
        uiState = uiState,
        onTeamSelected = viewModel::onTeamSelected,
        onDateSelected = viewModel::onDateSelected,
        onStartTimeChanged = viewModel::onStartTimeChanged,
        onEndTimeChanged = viewModel::onEndTimeChanged
    )
}
