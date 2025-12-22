package com.sedilant.yambol.ui.createTrain.stepsScreens.basicInfo

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sedilant.yambol.ui.createTrain.commonComposables.CreateTrainScaffold
import com.sedilant.yambol.ui.createTrain.commonComposables.TeamSelectionDropdown
import com.sedilant.yambol.ui.createTrain.commonComposables.TimeWheelPicker
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
public fun CreateTrainBasicInfoScreen(
    onClose: () -> Unit,
    onNext: () -> Unit,
    viewModel: CreateTrainBasicInfoViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    uiState.error?.let { error ->
        LaunchedEffect(error) {
            // TODO here we can show a snackBar or something
            viewModel.clearError()
        }
    }

    CreateTrainBasicInfoScreenStateless(
        onClose = onClose,
        onNext = {
            viewModel.saveStepData() // Guardar antes de avanzar
            onNext()
        },
        uiState = uiState,
        onTeamSelected = viewModel::onTeamSelected,
        onDateSelected = viewModel::onDateSelected,
        onTimeChange = viewModel::onTimeChanged,
        onDurationChange = viewModel::onDurationChanged
    )
}

@Composable
private fun CreateTrainBasicInfoScreenStateless(
    onClose: () -> Unit,
    onNext: () -> Unit,
    uiState: CreateTrainBasicInfoViewModel.UiState,
    onTeamSelected: (String) -> Unit,
    onDateSelected: (Date) -> Unit,
    onTimeChange: (Int, Int) -> Unit,
    onDurationChange: (Int, Int) -> Unit
) {
    // Convertir Float a horas y minutos para la UI
    val selectedHours = uiState.selectedHour.toInt()
    val selectedMinutes = ((uiState.selectedHour - selectedHours) * 60).toInt()

    val durationHours = (uiState.selectedDuration / 60).toInt()
    val durationMinutes = (uiState.selectedDuration % 60).toInt()

    CreateTrainScaffold(
        title = "Nuevo entrenamiento",
        onCloseClick = onClose,
        onBottomButtonClick = onNext,
        bottomButtonText = "Siguiente",
        showBackButton = false
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
        ) {
            // Loading indicator
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            // Date Section
            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = "Fecha",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            )

            // Calendar
            DateSelector(
                selectedDate = uiState.selectedDate,
                onDateSelected = onDateSelected
            )

            // Time and Duration Section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.Top
            ) {
                // Hour
                TimeSelector(
                    title = "Hora",
                    modifier = Modifier.weight(1f),
                    selectedHour = selectedHours,
                    selectedMinute = selectedMinutes,
                    onTimeChange = onTimeChange
                )

                // Duration
                TimeSelector(
                    title = "Duración",
                    modifier = Modifier.weight(1f),
                    selectedHour = durationHours,
                    selectedMinute = durationMinutes,
                    onTimeChange = onDurationChange
                )
            }

            // Team Selection
            TeamSelectionDropdown(
                teams = listOf(),
                selectedTeamId = 0,
                onTeamSelected = {}
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun TimeSelector(
    title: String,
    modifier: Modifier = Modifier,
    selectedHour: Int,
    selectedMinute: Int,
    onTimeChange: (Int, Int) -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.primary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(8.dp))

        TimeWheelPicker(
            selectedHour = selectedHour,
            selectedMinute = selectedMinute,
            onTimeChange = onTimeChange
        )
    }
}


@SuppressLint("NewApi")
@Composable
fun DateSelector(
    selectedDate: Date,
    onDateSelected: (Date) -> Unit
) {
    // Convertir Date a LocalDate para trabajar con YearMonth
    val calendar = Calendar.getInstance().apply { time = selectedDate }
    val selectedLocalDate = LocalDate.of(
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH) + 1,
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    var currentMonth by remember(selectedLocalDate) {
        mutableStateOf(YearMonth.from(selectedLocalDate))
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(16.dp)
    ) {
        // Month Navigation Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Mes anterior",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Text(
                text = "${
                    currentMonth.month.getDisplayName(TextStyle.FULL, Locale("es", "ES"))
                        .replaceFirstChar {
                            if (it.isLowerCase()) it.titlecase(Locale("es", "ES"))
                            else it.toString()
                        }
                } ${currentMonth.year}",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )

            IconButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Mes siguiente",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Days of week header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf("D", "L", "M", "X", "J", "V", "S").forEach { day ->
                Text(
                    text = day,
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = 12.sp,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Calendar Grid
        CalendarGrid(
            yearMonth = currentMonth,
            selectedDate = selectedLocalDate,
            onDaySelected = { day ->
                // Convertir el día seleccionado a Date
                val newDate = currentMonth.atDay(day)
                val newCalendar = Calendar.getInstance().apply {
                    set(Calendar.YEAR, newDate.year)
                    set(Calendar.MONTH, newDate.monthValue - 1)
                    set(Calendar.DAY_OF_MONTH, newDate.dayOfMonth)
                    // Mantener la hora actual
                    set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY))
                    set(Calendar.MINUTE, calendar.get(Calendar.MINUTE))
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                onDateSelected(newCalendar.time)
            },
            primaryBlue = MaterialTheme.colorScheme.primary,
            textSecondary = MaterialTheme.colorScheme.secondary
        )
    }
}

@SuppressLint("NewApi")
@Composable
fun CalendarGrid(
    yearMonth: YearMonth,
    selectedDate: LocalDate,
    onDaySelected: (Int) -> Unit,
    primaryBlue: Color,
    textSecondary: Color
) {
    val firstDayOfMonth = yearMonth.atDay(1)
    val daysInMonth = yearMonth.lengthOfMonth()
    val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7

    // Verificar si la fecha seleccionada está en el mes actual
    val selectedDay = if (selectedDate.year == yearMonth.year &&
        selectedDate.month == yearMonth.month) {
        selectedDate.dayOfMonth
    } else {
        null
    }

    val today = LocalDate.now()
    val isCurrentMonth = yearMonth.year == today.year && yearMonth.month == today.month

    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        modifier = Modifier.height(250.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Empty cells before first day
        items(firstDayOfWeek) {
            Spacer(modifier = Modifier.size(40.dp))
        }

        // Days of month
        items(daysInMonth) { index ->
            val day = index + 1
            val isSelected = day == selectedDay
            val isToday = isCurrentMonth && day == today.dayOfMonth

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            isSelected -> primaryBlue
                            isToday -> primaryBlue.copy(alpha = 0.2f)
                            else -> Color.Transparent
                        }
                    )
                    .border(
                        width = if (isToday && !isSelected) 1.dp else 0.dp,
                        color = if (isToday && !isSelected) primaryBlue else Color.Transparent,
                        shape = CircleShape
                    )
                    .clickable { onDaySelected(day) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = day.toString(),
                    color = when {
                        isSelected -> Color.White
                        isToday -> primaryBlue
                        else -> textSecondary
                    },
                    fontWeight = when {
                        isSelected || isToday -> FontWeight.Bold
                        else -> FontWeight.Normal
                    }
                )
            }
        }
    }
}

/**
 * Extension para convertir Date a LocalDate de forma segura
 */
@SuppressLint("NewApi")
fun Date.toLocalDate(): LocalDate {
    val calendar = Calendar.getInstance().apply { time = this@toLocalDate }
    return LocalDate.of(
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH) + 1,
        calendar.get(Calendar.DAY_OF_MONTH)
    )
}

/**
 * Extension para convertir LocalDate a Date
 */
@SuppressLint("NewApi")
fun LocalDate.toDate(): Date {
    return Calendar.getInstance().apply {
        set(Calendar.YEAR, this@toDate.year)
        set(Calendar.MONTH, this@toDate.monthValue - 1)
        set(Calendar.DAY_OF_MONTH, this@toDate.dayOfMonth)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.time
}

@Preview(showBackground = true)
@Composable
fun BasicInfoScreenPreview() {
    MaterialTheme {
        CreateTrainBasicInfoScreenStateless(
            onClose = {},
            onNext = {},
            uiState = CreateTrainBasicInfoViewModel.UiState(),
            onTeamSelected = {},
            onTimeChange = { _, _ -> },
            onDurationChange = { _, _ -> },
            onDateSelected = {}
        )
    }
}
