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

@Composable
fun CreateTrainScreenV2() {
    val viewModel: CreateTrainV2ViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    CreateTrainScreenV2Stateless(
        uiState = uiState,
        onNextStep = viewModel::onNextStep,
        onBack = viewModel::onBack,
        onCancel = viewModel::onCancel
    )
}

@Composable
private fun CreateTrainScreenV2Stateless(
    uiState: CreateTrainUiStateV2,
    onNextStep: () -> Unit,
    onBack: () -> Unit,
    onCancel: () -> Unit
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
                    onClose = onCancel,
                )
            }
        }
}
