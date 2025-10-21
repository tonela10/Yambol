package com.sedilant.yambol.ui.createTrain.stepsScreens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.sedilant.yambol.domain.models.TeamDomainModel
import com.sedilant.yambol.ui.createTrain.CreateTrainUiState
import com.sedilant.yambol.ui.createTrain.composables.CreateTrainScaffold
import com.sedilant.yambol.ui.createTrain.composables.TeamSelectionDropdown
import com.sedilant.yambol.ui.createTrain.composables.TimeWheelPicker
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun BasicInfoScreen(
    onDismiss: () -> Unit,
    onNext: () -> Unit,
    uiState: CreateTrainUiState,
    onTeamSelected: (Int) -> Unit,
    onTimeChange: (Int, Int) -> Unit,
    onDurationChange: (Int, Int) -> Unit
) {
    CreateTrainScaffold(
        title = "Nuevo entrenamiento",
        onCloseClick = onDismiss,
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
            // Date Section
            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = "Fecha",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            )

            // Calendar
            DateSelector()

            // Time and Duration Section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.Top
            ) {
                // Hora
                TimeSelector(
                    title = "Hora",
                    modifier = Modifier.weight(1f),
                    selectedHour = uiState.selectedHours,
                    selectedMinute = uiState.selectedMinutes,
                    onTimeChange = onTimeChange
                )

                // Duración
                TimeSelector(
                    title = "Duración",
                    modifier = Modifier.weight(1f),
                    selectedHour = uiState.selectedHours,
                    selectedMinute = uiState.selectedMinutes,
                    onTimeChange = onDurationChange
                )
            }

            // Team Selection
            TeamSelectionDropdown(
                teams = uiState.teams,
                selectedTeamId = uiState.selectedTeamId,
                onTeamSelected = onTeamSelected
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
private fun DateSelector() {
    var selectedDate by remember { mutableStateOf(5) }
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(16.dp)
    ) {
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
                text = "${currentMonth.month.getDisplayName(TextStyle.FULL, Locale("es", "ES")).replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale("es", "ES")) else it.toString() }} ${currentMonth.year}",
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

        // Days of week
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
            selectedDay = selectedDate,
            onDaySelected = { selectedDate = it },
            primaryBlue = MaterialTheme.colorScheme.primary,
            textSecondary = MaterialTheme.colorScheme.secondary
        )
    }
}

@SuppressLint("NewApi")
@Composable
fun CalendarGrid(
    yearMonth: YearMonth,
    selectedDay: Int,
    onDaySelected: (Int) -> Unit,
    primaryBlue: Color,
    textSecondary: Color
) {
    val firstDayOfMonth = yearMonth.atDay(1)
    val daysInMonth = yearMonth.lengthOfMonth()
    val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7

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

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) primaryBlue else Color.Transparent)
                    .clickable { onDaySelected(day) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = day.toString(),
                    color = if (isSelected) Color.White else textSecondary,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BasicInfoScreenPreview() {
    MaterialTheme {
        BasicInfoScreen(
            onDismiss = {},
            onNext = {},
            uiState = CreateTrainUiState(
                teams = listOf(
                    TeamDomainModel(id = 1, name = "Team 1")
                )
            ),
            onTeamSelected = {},
            onTimeChange = { _, _ -> },
            onDurationChange = { _, _ -> }
        )
    }
}
