package com.sedilant.yambol.feature.createTrain

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier

@Composable
fun CreateTrainScreenV2Stateless(
    uiState: CreateTrainUiStateV2,
    onNextStep: () -> Unit,
    onBack: () -> Unit,
    onCancel: () -> Unit,
    basicInfoContent: @Composable (teamId: String) -> Unit,
    conceptsContent: @Composable (onBack: () -> Unit, onNext: () -> Unit, onClose: () -> Unit) -> Unit,
    tasksContent: @Composable (onBack: () -> Unit, onNext: () -> Unit, onClose: () -> Unit) -> Unit,
    trainDetailsContent: @Composable (onBack: () -> Unit, onClose: () -> Unit, teamId: String) -> Unit,
) {
    val pagerState = rememberPagerState(pageCount = { Step.entries.size })

    LaunchedEffect(uiState.currentStep) {
        pagerState.animateScrollToPage(uiState.currentStep.ordinal)
    }
    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize(),
        userScrollEnabled = false
    ) { page ->
        when (Step.entries[page]) {
            Step.BASIC_INFO -> basicInfoContent(uiState.teamId)
            Step.CONCEPTS -> conceptsContent(onBack, onNextStep, onCancel)
            Step.TASKS -> tasksContent(onBack, onNextStep, onCancel)
            Step.TRAIN_DETAIL -> trainDetailsContent(onBack, { onCancel() }, uiState.teamId)
        }
    }
}
