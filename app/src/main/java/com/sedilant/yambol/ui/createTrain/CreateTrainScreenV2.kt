package com.sedilant.yambol.ui.createTrain

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.sedilant.yambol.ui.createTrain.stepsScreens.basicInfo.CreateTrainBasicInfoScreen
import com.sedilant.yambol.ui.createTrain.stepsScreens.concepts.CreateTrainConceptsScreen
import com.sedilant.yambol.ui.createTrain.stepsScreens.tasks.CreateTrainTasksScreen
import com.sedilant.yambol.ui.createTrain.stepsScreens.trainDetails.CreateTrainDetailsScreenV2


/**
 * Each screen should add into a Data store the values of the current train. When the user arrives
 * to the final screen he will save the train. So the app use the information of the data stores to
 * create a new train and leave the data store empty.
 */
@Composable
fun CreateTrainScreenV2(
    onCancel: () -> Unit,
    teamId: Long,
) {
    val viewModel: CreateTrainV2ViewModel = hiltViewModel(
        creationCallback = { factory: CreateTrainV2ViewModelFactory ->
            factory.create(teamId)
        }
    )
    val uiState by viewModel.uiState.collectAsState()

    CreateTrainScreenV2Stateless(
        uiState = uiState,
        onNextStep = viewModel::onNextStep,
        onBack = viewModel::onBack,
        onCancel = onCancel,
    )
}

@Composable
private fun CreateTrainScreenV2Stateless(
    uiState: CreateTrainUiStateV2,
    onNextStep: () -> Unit,
    onBack: () -> Unit,
    onCancel: () -> Unit,
) {
    val pagerState = rememberPagerState(pageCount = { Step.entries.size })

    LaunchedEffect(uiState.currentStep) {
        pagerState.animateScrollToPage(uiState.currentStep.ordinal)
    }
    HorizontalPager(
        state = pagerState,
        modifier = Modifier
            .fillMaxSize(),
        userScrollEnabled = false
    ) { page ->
        when (Step.entries[page]) {
            Step.BASIC_INFO -> CreateTrainBasicInfoScreen(
                onClose = onCancel,
                onNext = onNextStep,
                teamId = uiState.teamId
            )

            Step.CONCEPTS -> CreateTrainConceptsScreen(
                onBack = onBack,
                onNext = onNextStep,
                onClose = onCancel,
            )

            Step.TASKS -> CreateTrainTasksScreen(
                onBack = onBack,
                onNext = onNextStep,
                onClose = onCancel,
            )

            Step.TRAIN_DETAIL -> CreateTrainDetailsScreenV2(
                onBack = onBack,
                onClose = {
                    onCancel()
                },
                onTrainingSaved = onCancel, //  navigate up
                teamId = uiState.teamId,
            )
        }
    }
}
