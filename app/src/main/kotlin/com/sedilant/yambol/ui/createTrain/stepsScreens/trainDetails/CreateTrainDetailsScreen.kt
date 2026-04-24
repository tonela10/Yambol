package com.sedilant.yambol.ui.createTrain.stepsScreens.trainDetails

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sedilant.yambol.feature.createTrain.stepsScreens.concepts.Concept
import com.sedilant.yambol.feature.createTrain.stepsScreens.tasks.TaskUI
import com.sedilant.yambol.feature.createTrain.stepsScreens.trainDetails.CreateTrainDetailsScreenStateless
import com.sedilant.yambol.feature.createTrain.stepsScreens.trainDetails.CreateTrainDetailsUiState
import com.sedilant.yambol.feature.createTrain.stepsScreens.trainDetails.TrainInfo

@Composable
fun CreateTrainDetailsScreenV2(
    onBack: () -> Unit,
    onClose: () -> Unit,
    onTrainingSaved: () -> Unit, // Navigate after save
    teamId: String,
    viewModel: CreateTrainDetailsViewModel = koinViewModel(
        parameters = { parametersOf(teamId) }
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CreateTrainDetailsScreenStateless(
        onBack = onBack,
        onClose = onClose,
        onSave = {
            viewModel.saveTraining()
            onTrainingSaved()
        },
        uiState = uiState
    )
}

@Preview
@Composable
private fun CreateTrainDetailsScreenPreview() {
    CreateTrainDetailsScreenStateless(
        uiState = CreateTrainDetailsUiState.Success(
            trainInfo = TrainInfo(
                date = "15 de marzo de 2024",
                hour = "18:00",
                duration = "1 hora 30 minutos",
                team = "Equipo A"
            ),
            listOfTasks = listOf(
                TaskUI(
                    id = "1",
                    name = "Sentadillas",
                    description = "3x12 con barra libre",
                    variation = "Con barra libre",
                    concepts = listOf(Concept("1", "Sentadillas")),
                )
            ),
            listOfConcepts = listOf("Tiro en suspensión", "Defensa individual")
        ),
        onBack = {},
        onClose = {},
        onSave = {}
    )
}
