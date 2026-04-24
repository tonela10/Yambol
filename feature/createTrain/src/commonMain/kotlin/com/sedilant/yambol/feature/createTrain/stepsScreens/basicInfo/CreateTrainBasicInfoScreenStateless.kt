package com.sedilant.yambol.feature.createTrain.stepsScreens.basicInfo

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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.unit.dp
import com.sedilant.yambol.core.designsystem.Res
import com.sedilant.yambol.core.designsystem.*
import com.sedilant.yambol.feature.createTrain.commonComposables.CreateTrainScaffold
import com.sedilant.yambol.feature.createTrain.commonComposables.TeamSelectionDropdown
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource

@Composable
fun CreateTrainBasicInfoScreenStateless(
    uiState: BasicInfoUiState,
    onClose: () -> Unit,
    onNext: () -> Unit,
    onTeamSelected: (String) -> Unit,
    onDateSelected: (Instant) -> Unit,
    onStartTimeChanged: (Int, Int) -> Unit,
    onEndTimeChanged: (Int, Int) -> Unit
) {
    CreateTrainScaffold(
        title = stringResource(Res.string.new_training_session),
        onCloseClick = onClose,
        onBottomButtonClick = onNext,
        bottomButtonText = stringResource(Res.string.next),
        showBackButton = false
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
        ) {
            when (uiState) {
                is BasicInfoUiState.Error -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = uiState.message, color = MaterialTheme.colorScheme.error)
                    }
                }

                BasicInfoUiState.Loading -> {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is BasicInfoUiState.Success -> {
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
    selectedDate: Instant,
    startHourFloat: Float,
    endHourFloat: Float,
    onDateSelected: (Instant) -> Unit,
    onStartTimeChanged: (Int, Int) -> Unit,
    onEndTimeChanged: (Int, Int) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }

    fun formatInstant(instant: Instant): String {
        val dt = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        return "${dt.dayOfMonth.toString().padStart(2, '0')}/${dt.monthNumber.toString().padStart(2, '0')}/${dt.year}"
    }

    fun floatToTimeString(value: Float): String {
        val h = value.toInt()
        val m = ((value - h) * 60).toInt()
        return "%02d:%02d".format(h, m)
    }

    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedDate.toEpochMilliseconds())

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

        OutlinedTextField(
            value = formatInstant(selectedDate),
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

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { onDateSelected(Instant.fromEpochMilliseconds(it)) }
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
