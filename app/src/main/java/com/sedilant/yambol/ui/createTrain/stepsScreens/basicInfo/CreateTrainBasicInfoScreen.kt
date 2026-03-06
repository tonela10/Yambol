package com.sedilant.yambol.ui.createTrain.stepsScreens.basicInfo

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccessTimeFilled
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sedilant.yambol.R
import com.sedilant.yambol.domain.models.TeamDomainModel
import com.sedilant.yambol.ui.createTrain.commonComposables.CreateTrainScaffold
import com.sedilant.yambol.ui.createTrain.commonComposables.TeamSelectionDropdown
import com.sedilant.yambol.ui.theme.YambolTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CreateTrainBasicInfoScreen(
    onClose: () -> Unit,
    onNext: () -> Unit,
    teamId: String,
    viewModel: CreateTrainBasicInfoViewModel = hiltViewModel(
        creationCallback = { factory: CreateTrainBasicInfoViewModelFactory ->
            factory.create(teamId = teamId)
        }
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

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun CreateTrainBasicInfoScreenStateless(
    uiState: CreateTrainBasicInfoViewModel.UiStateNew,
    onClose: () -> Unit,
    onNext: () -> Unit,
    onTeamSelected: (String) -> Unit,
    onDateSelected: (Date) -> Unit,
    onStartTimeChanged: (Int, Int) -> Unit,
    onEndTimeChanged: (Int, Int) -> Unit
) {
    CreateTrainScaffold(
        title = stringResource(R.string.new_training_session),
        onCloseClick = onClose,
        onBottomButtonClick = onNext,
        bottomButtonText = stringResource(R.string.next),
        showBackButton = false
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
        ) {
            when (uiState) {
                is CreateTrainBasicInfoViewModel.UiStateNew.Error -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = uiState.message, color = MaterialTheme.colorScheme.error)
                    }
                }

                CreateTrainBasicInfoViewModel.UiStateNew.Loading -> {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        LoadingIndicator()
                    }
                }

                is CreateTrainBasicInfoViewModel.UiStateNew.Success -> {
                    SuccessState(
                        selectedDate = uiState.selectedDate,
                        startHourFloat = uiState.startHour,
                        endHourFloat = uiState.endHour,
                        onDateSelected = onDateSelected,
                        onStartTimeChanged = onStartTimeChanged,
                        onEndTimeChanged = onEndTimeChanged
                    )

                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        Text("¿Qué equipo entrena?", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        TeamSelectionDropdown(
                            teams = uiState.teamsList,
                            selectedTeamId = uiState.selectedTeamId,
                            onTeamSelected = onTeamSelected
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SuccessState(
    selectedDate: Date,
    startHourFloat: Float,
    endHourFloat: Float,
    onDateSelected: (Date) -> Unit,
    onStartTimeChanged: (Int, Int) -> Unit,
    onEndTimeChanged: (Int, Int) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }

    // Converters for Display
    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    fun floatToTimeString(value: Float): String {
        val h = value.toInt()
        val m = ((value - h) * 60).toInt()
        return "%02d:%02d".format(h, m)
    }

    // Picker States
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedDate.time)

    val startH = startHourFloat.toInt()
    val startM = ((startHourFloat - startH) * 60).toInt()
    val startTimeState =
        rememberTimePickerState(initialHour = startH, initialMinute = startM, is24Hour = true)

    val endH = endHourFloat.toInt()
    val endM = ((endHourFloat - endH) * 60).toInt()
    val endTimeState =
        rememberTimePickerState(initialHour = endH, initialMinute = endM, is24Hour = true)

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Detalles del entrenamiento", style = MaterialTheme.typography.titleMedium)

        // Date Field
        OutlinedTextField(
            value = dateFormatter.format(selectedDate),
            onValueChange = {},
            label = { Text("Fecha") },
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showDatePicker = true },
            enabled = false,
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledBorderColor = MaterialTheme.colorScheme.outline,
                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            trailingIcon = { Icon(Icons.Rounded.CalendarMonth, null) }
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            // Start Time Field
            OutlinedTextField(
                value = floatToTimeString(startHourFloat),
                onValueChange = {},
                label = { Text("Inicio") },
                readOnly = true,
                modifier = Modifier
                    .weight(1f)
                    .clickable { showStartTimePicker = true },
                enabled = false,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                trailingIcon = { Icon(Icons.Rounded.AccessTimeFilled, null) }
            )

            // End Time Field
            OutlinedTextField(
                value = floatToTimeString(endHourFloat),
                onValueChange = {},
                label = { Text("Fin") },
                readOnly = true,
                modifier = Modifier
                    .weight(1f)
                    .clickable { showEndTimePicker = true },
                enabled = false,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                trailingIcon = { Icon(Icons.Rounded.AccessTimeFilled, null) }
            )
        }
    }

    // --- Dialog Logic ---
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { onDateSelected(Date(it)) }
                    showDatePicker = false
                }) { Text("OK") }
            }
        ) { DatePicker(state = datePickerState) }
    }

    if (showStartTimePicker) {
        TimePickerDialog(
            onDismissRequest = { showStartTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    onStartTimeChanged(startTimeState.hour, startTimeState.minute)
                    showStartTimePicker = false
                }) { Text("OK") }
            }
        ) { TimePicker(state = startTimeState) }
    }

    if (showEndTimePicker) {
        TimePickerDialog(
            onDismissRequest = { showEndTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    onEndTimeChanged(endTimeState.hour, endTimeState.minute)
                    showEndTimePicker = false
                }) { Text("OK") }
            }
        ) { TimePicker(state = endTimeState) }
    }
}

@Composable
fun TimePickerDialog(
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = confirmButton,
        text = { content() }
    )
}

@Preview(showBackground = true)
@Composable
fun BasicInfoScreenPreview() {
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            CreateTrainBasicInfoScreenStateless(
                onClose = {},
                onNext = {},
                uiState = CreateTrainBasicInfoViewModel.UiStateNew.Success(
                    teamsList = listOf(
                        TeamDomainModel("1", "Cachos"),
                        TeamDomainModel("2", "Cachos2")
                    ),
                    selectedTeamId = "1",
                    selectedDate = Date(), // Current Date
                    startHour = 17.5f,    // 17:30
                    endHour = 19.0f       // 19:00
                ),
                onTeamSelected = {},
                onStartTimeChanged = { _, _ -> },
                onEndTimeChanged = { _, _ -> },
                onDateSelected = {}
            )
        }
    }
}

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
private fun SuccessStatePreview() {
    YambolTheme() {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            SuccessState(
                selectedDate = Date(),
                startHourFloat = 17.5f,
                endHourFloat = 18.5f,
                onDateSelected = {},
                onStartTimeChanged = { _, _ -> },
                onEndTimeChanged = { _, _ -> }
            )
        }
    }
}