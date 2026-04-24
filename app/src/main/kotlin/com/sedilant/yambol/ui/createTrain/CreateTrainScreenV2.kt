package com.sedilant.yambol.ui.createTrain

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import com.sedilant.yambol.feature.createTrain.CreateTrainScreenV2Stateless
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
    teamId: String,
) {
    val viewModel: CreateTrainV2ViewModel = koinViewModel(
        parameters = { parametersOf(teamId) }
    )
    val uiState by viewModel.uiState.collectAsState()

    CreateTrainScreenV2Stateless(
        uiState = uiState,
        onNextStep = viewModel::onNextStep,
        onBack = viewModel::onBack,
        onCancel = onCancel,
        basicInfoContent = { id ->
            CreateTrainBasicInfoScreen(
                onClose = onCancel,
                onNext = viewModel::onNextStep,
                teamId = id
            )
        },
        conceptsContent = { onBack, onNext, onClose ->
            CreateTrainConceptsScreen(
                onBack = onBack,
                onNext = onNext,
                onClose = onClose,
            )
        },
        tasksContent = { onBack, onNext, onClose ->
            CreateTrainTasksScreen(
                onBack = onBack,
                onNext = onNext,
                onClose = onClose,
            )
        },
        trainDetailsContent = { onBack, onClose, id ->
            CreateTrainDetailsScreenV2(
                onBack = onBack,
                onClose = onClose,
                onTrainingSaved = onCancel,
                teamId = id,
            )
        }
    )
}
