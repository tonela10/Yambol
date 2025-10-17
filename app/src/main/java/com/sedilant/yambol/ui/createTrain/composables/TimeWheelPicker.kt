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
    Box(modifier = modifier) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
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

            Spacer(modifier = Modifier.width(8.dp))

            // Minute picker
            WheelPicker(
                items = (0..59).toList(),
                selectedIndex = selectedMinute,
                onItemSelected = { onTimeChange(selectedHour, it) },
                modifier = Modifier.width(60.dp)
            )
        }

        // Selection indicator overlay
        Box(
            modifier = Modifier
                .width(128.dp) // 60dp + 8dp + 60dp = 128dp
                .height(40.dp)
                .align(Alignment.Center)
                .background(
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                    RoundedCornerShape(8.dp)
                )
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
            .height(120.dp)
            .clip(RoundedCornerShape(12.dp))
            //   .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
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
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Show 1 item before (if exists)
            val prevIndex = currentIndex - 1
            if (prevIndex in items.indices) {
                WheelPickerItem(
                    text = displayText(items[prevIndex]),
                    isSelected = false,
                    alpha = 0.5f
                )
            } else {
                Spacer(modifier = Modifier.height(40.dp))
            }

            // Selected item (center)
            if (currentIndex in items.indices) {
                WheelPickerItem(
                    text = displayText(items[currentIndex]),
                    isSelected = true,
                    alpha = 1f
                )
            }

            // Show 1 item after (if exists)
            val nextIndex = currentIndex + 1
            if (nextIndex in items.indices) {
                WheelPickerItem(
                    text = displayText(items[nextIndex]),
                    isSelected = false,
                    alpha = 0.5f
                )
            } else {
                Spacer(modifier = Modifier.height(40.dp))
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
    Box(modifier = Modifier.fillMaxSize()) {
        TimeWheelPicker(
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.Center),
            selectedHour = 10,
            selectedMinute = 11,
            onTimeChange = { h, m ->
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DurationWheelPickerPreview() {
    DurationWheelPicker(
        selectedValue = 1,
        selectedUnit = "Minutos",
        onDurationChange = { v, u ->
        }
    )

}
