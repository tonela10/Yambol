package com.sedilant.yambol.ui.createTrain

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sedilant.yambol.domain.models.TeamDomainModel
import com.sedilant.yambol.ui.createTrain.composables.BasicInfoStep
import com.sedilant.yambol.ui.createTrain.composables.ConceptsStep
import com.sedilant.yambol.ui.createTrain.composables.ReviewStep
import com.sedilant.yambol.ui.createTrain.composables.TasksStep
import com.sedilant.yambol.ui.theme.YambolTheme
import java.util.Date

@Composable
fun CreateTrainScreen(
    modifier: Modifier = Modifier,
    defaultTeamId: Int,
    onNavigateBack: () -> Unit,
    onTrainCreated: () -> Unit,
    createTrainViewModel: CreateTrainViewModel = hiltViewModel(
        creationCallback = { factory: CreateTrainViewModelFactory ->
            factory.create(defaultTeamId = defaultTeamId)
        }
    )
) {
    val uiState by createTrainViewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isTrainSaved) {
        if (uiState.isTrainSaved) {
            onTrainCreated()
        }
    }

    CreateTrainScreenStateless(
        modifier = modifier,
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onNextStep = createTrainViewModel::nextStep,
        onPreviousStep = createTrainViewModel::previousStep,
        onDateSelected = createTrainViewModel::updateDate,
        onHoursChanged = createTrainViewModel::updateHours,
        onMinutesChanged = createTrainViewModel::updateMinutes,
        onTeamSelected = createTrainViewModel::updateTeam,
        onConceptAdded = createTrainViewModel::addConcept,
        onConceptRemoved = createTrainViewModel::removeConcept,
        onTaskAdded = createTrainViewModel::addTask,
        onTaskRemoved = createTrainViewModel::removeTask,
        onTaskUpdated = createTrainViewModel::updateTask,
        onSaveTrain = createTrainViewModel::saveTrain,
        canProceedFromBasicInfo = createTrainViewModel::canProceedFromBasicInfo,
        canProceedFromConcepts = createTrainViewModel::canProceedFromConcepts,
        onErrorDismissed = createTrainViewModel::clearError
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTrainScreenStateless(
    modifier: Modifier = Modifier,
    uiState: CreateTrainUiState,
    onNavigateBack: () -> Unit = {},
    onNextStep: () -> Unit = {},
    onPreviousStep: () -> Unit = {},
    onDateSelected: (Date) -> Unit = {},
    onHoursChanged: (Int) -> Unit = {},
    onMinutesChanged: (Int) -> Unit = {},
    onTeamSelected: (Int) -> Unit = {},
    onConceptAdded: (String) -> Unit = {},
    onConceptRemoved: (String) -> Unit = {},
    onTaskAdded: (TrainTaskData) -> Unit = {},
    onTaskRemoved: (Int) -> Unit = {},
    onTaskUpdated: (Int, TrainTaskData) -> Unit = { _, _ -> },
    onSaveTrain: () -> Unit = {},
    canProceedFromBasicInfo: () -> Boolean = { true },
    canProceedFromConcepts: () -> Boolean = { true },
    onErrorDismissed: () -> Unit = {}
) {
    Column(modifier = modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Text(
                    text = "Create Training - ${uiState.currentStep.title}",
                    style = MaterialTheme.typography.titleMedium
                )
            },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Navigate back"
                    )
                }
            }
        )

        LinearProgressIndicator(
            progress = { (uiState.currentStep.stepNumber / 4f) },
            modifier = Modifier.fillMaxWidth()
        )

        // Step indicator
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            CreateTrainStep.entries.forEach { step ->
                StepIndicator(
                    stepNumber = step.stepNumber,
                    isActive = step == uiState.currentStep,
                    isCompleted = step.stepNumber < uiState.currentStep.stepNumber
                )
            }
        }

        // Content based on current step
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when (uiState.currentStep) {
                CreateTrainStep.BASIC_INFO -> BasicInfoStep(
                    uiState = uiState,
                    onDateSelected = onDateSelected,
                    onHoursChanged = onHoursChanged,
                    onMinutesChanged = onMinutesChanged,
                    onTeamSelected = onTeamSelected
                )

                CreateTrainStep.CONCEPTS -> ConceptsStep(
                    concepts = uiState.concepts,
                    onConceptAdded = onConceptAdded,
                    onConceptRemoved = onConceptRemoved
                )

                CreateTrainStep.TASKS -> TasksStep(
                    tasks = uiState.tasks,
                    concepts = uiState.concepts,
                    onTaskAdded = onTaskAdded,
                    onTaskRemoved = onTaskRemoved,
                    onTaskUpdated = onTaskUpdated
                )

                CreateTrainStep.REVIEW -> ReviewStep(
                    uiState = uiState,
                    onSaveTrain = onSaveTrain
                )
            }

            // Loading overlay
            if (uiState.isLoading) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }

        // Navigation buttons
        NavigationButtons(
            currentStep = uiState.currentStep,
            onPreviousStep = onPreviousStep,
            onNextStep = onNextStep,
            canProceed = when (uiState.currentStep) {
                CreateTrainStep.BASIC_INFO -> canProceedFromBasicInfo()
                CreateTrainStep.CONCEPTS -> canProceedFromConcepts()
                CreateTrainStep.TASKS -> true // Tasks are optional
                CreateTrainStep.REVIEW -> true
            }
        )
    }

    // Error dialog
    uiState.error?.let { error ->
        AlertDialog(
            onDismissRequest = onErrorDismissed,
            title = { Text("Error") },
            text = { Text(error) },
            confirmButton = {
                TextButton(onClick = onErrorDismissed) {
                    Text("OK")
                }
            }
        )
    }
}

@Composable
fun StepIndicator(
    stepNumber: Int,
    isActive: Boolean,
    isCompleted: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
    ) {
        Surface(
            shape = MaterialTheme.shapes.small,
            color = when {
                isCompleted -> MaterialTheme.colorScheme.primary
                isActive -> MaterialTheme.colorScheme.primaryContainer
                else -> MaterialTheme.colorScheme.surfaceVariant
            },
            modifier = Modifier.size(32.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = stepNumber.toString(),
                    style = MaterialTheme.typography.labelMedium,
                    color = when {
                        isCompleted -> MaterialTheme.colorScheme.onPrimary
                        isActive -> MaterialTheme.colorScheme.onPrimaryContainer
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
    }
}

@Composable
fun NavigationButtons(
    currentStep: CreateTrainStep,
    onPreviousStep: () -> Unit,
    onNextStep: () -> Unit,
    canProceed: Boolean
) {
    Surface(
        tonalElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Previous button
            if (currentStep != CreateTrainStep.BASIC_INFO) {
                OutlinedButton(onClick = onPreviousStep) {
                    Text("Previous")
                }
            } else {
                Spacer(modifier = Modifier.width(1.dp))
            }

            // Next button
            if (currentStep != CreateTrainStep.REVIEW) {
                Button(
                    onClick = onNextStep,
                    enabled = canProceed
                ) {
                    Text("Next")
                }
            } else {
                Spacer(modifier = Modifier.width(1.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CreateTrainScreenPreview() {
    YambolTheme {
        CreateTrainScreenStateless(
            uiState = CreateTrainUiState(
                currentStep = CreateTrainStep.BASIC_INFO,
                teams = listOf(
                    TeamDomainModel("Team A", 1),
                    TeamDomainModel("Team B", 2)
                ),
                selectedDate = Date(),
                selectedTeamId = 1,
                isLoading = false
            )
        )
    }
}
