package com.sedilant.yambol.ui.createTrain.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun TimeWheelPicker(
    selectedHour: Int,
    selectedMinute: Int,
    onTimeChange: (hour: Int, minute: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Hour picker
        WheelPicker(
            items = (0..23).toList(),
            selectedIndex = selectedHour,
            onItemSelected = { onTimeChange(it, selectedMinute) },
            modifier = Modifier.width(60.dp)
        )

        Text(
            text = ":",
            color = MaterialTheme.colorScheme.primary,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 4.dp)
        )

        // Minute picker
        WheelPicker(
            items = (0..59).toList(),
            selectedIndex = selectedMinute,
            onItemSelected = { onTimeChange(selectedHour, it) },
            modifier = Modifier.width(60.dp)
        )
    }
}

@Composable
fun DurationWheelPicker(
    selectedValue: Int,
    selectedUnit: String,
    onDurationChange: (value: Int, unit: String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Value picker (1-120)
        WheelPicker(
            items = (1..120).toList(),
            selectedIndex = selectedValue - 1,
            onItemSelected = { onDurationChange(it + 1, selectedUnit) },
            modifier = Modifier.width(70.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Unit picker (Minutos/Horas)
        WheelPicker(
            items = listOf("Minutos", "Horas"),
            selectedIndex = if (selectedUnit == "Minutos") 0 else 1,
            onItemSelected = {
                val unit = if (it == 0) "Minutos" else "Horas"
                onDurationChange(selectedValue, unit)
            },
            modifier = Modifier.width(90.dp),
            displayText = { it }
        )
    }
}

@Composable
fun <T> WheelPicker(
    items: List<T>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    displayText: (T) -> String = { it.toString() }
) {
    var currentIndex by remember { mutableStateOf(selectedIndex) }
    var dragOffset by remember { mutableStateOf(0f) }

    LaunchedEffect(selectedIndex) {
        currentIndex = selectedIndex
    }

    Box(
        modifier = modifier
            .height(150.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            .pointerInput(Unit) {
                detectDragGestures(
                    onDrag = { change, offset ->
                        change.consume()
                        dragOffset += offset.y

                        val itemHeight = 40f
                        val scrolledItems = (dragOffset / itemHeight).roundToInt()

                        if (abs(scrolledItems) >= 1) {
                            val newIndex = (currentIndex - scrolledItems)
                                .coerceIn(0, items.lastIndex)

                            if (newIndex != currentIndex) {
                                currentIndex = newIndex
                                onItemSelected(newIndex)
                            }

                            dragOffset = 0f
                        }
                    },
                    onDragEnd = {
                        dragOffset = 0f
                    }
                )
            }
    ) {
        // Selection indicator
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .align(Alignment.Center)
                .background(
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                    RoundedCornerShape(8.dp)
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Show 2 items before
            for (i in -2..-1) {
                val index = currentIndex + i
                if (index in items.indices) {
                    WheelPickerItem(
                        text = displayText(items[index]),
                        isSelected = false,
                        alpha = 1f - abs(i) * 0.35f
                    )
                } else {
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }

            // Selected item
            if (currentIndex in items.indices) {
                WheelPickerItem(
                    text = displayText(items[currentIndex]),
                    isSelected = true,
                    alpha = 1f
                )
            }

            // Show 2 items after
            for (i in 1..2) {
                val index = currentIndex + i
                if (index in items.indices) {
                    WheelPickerItem(
                        text = displayText(items[index]),
                        isSelected = false,
                        alpha = 1f - abs(i) * 0.35f
                    )
                } else {
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}

@Composable
fun WheelPickerItem(
    text: String,
    isSelected: Boolean,
    alpha: Float,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(40.dp)
            .fillMaxWidth()
            .alpha(alpha),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
            fontSize = if (isSelected) 20.sp else 16.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

// Preview Components
@Preview(showBackground = true)
@Composable
fun TimeWheelPickerPreview() {
    var hour by remember { mutableStateOf(10) }
    var minute by remember { mutableStateOf(30) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A1F2E))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Hora",
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(8.dp))
        TimeWheelPicker(
            selectedHour = hour,
            selectedMinute = minute,
            onTimeChange = { h, m ->
                hour = h
                minute = m
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DurationWheelPickerPreview() {
    var value by remember { mutableStateOf(2) }
    var unit by remember { mutableStateOf("Horas") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A1F2E))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Duración",
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(8.dp))
        DurationWheelPicker(
            selectedValue = value,
            selectedUnit = unit,
            onDurationChange = { v, u ->
                value = v
                unit = u
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CombinedPickersPreview() {
    var hour by remember { mutableStateOf(10) }
    var minute by remember { mutableStateOf(0) }
    var duration by remember { mutableStateOf(2) }
    var durationUnit by remember { mutableStateOf("Horas") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A1F2E))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Hora",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
                TimeWheelPicker(
                    selectedHour = hour,
                    selectedMinute = minute,
                    onTimeChange = { h, m ->
                        hour = h
                        minute = m
                    }
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Duración",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
                DurationWheelPicker(
                    selectedValue = duration,
                    selectedUnit = durationUnit,
                    onDurationChange = { v, u ->
                        duration = v
                        durationUnit = u
                    }
                )
            }
        }
    }
}
