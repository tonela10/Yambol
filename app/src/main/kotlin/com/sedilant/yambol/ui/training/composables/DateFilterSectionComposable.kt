package com.sedilant.yambol.ui.training.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sedilant.yambol.R
import com.sedilant.yambol.domain.models.TrainDomainModel
import com.sedilant.yambol.ui.theme.YambolTheme
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun DateFilterSection(
    modifier: Modifier = Modifier,
    selectedFilter: DateFilter,
    onFilterSelected: (DateFilter) -> Unit,
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 20.dp),
    ) {
        items(DateFilter.entries.toTypedArray()) { filter ->
            FilterChip(
                selected = selectedFilter == filter,
                onClick = { onFilterSelected(filter) },
                label = {
                    Text(
                        text = filter.displayName,
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                leadingIcon = if (selectedFilter == filter) {
                    {
                        Icon(
                            painter = painterResource(R.drawable.calendar_month_24dp),
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                } else null,
                shape = RoundedCornerShape(16.dp)
            )
        }
    }
}

// TODO move the viewModel or some UseCase
fun filterTrainingsByDate(
    trainings: List<TrainDomainModel>,
    filter: DateFilter
): List<TrainDomainModel> {
    val calendar = Calendar.getInstance()
    val today = calendar.time

    return when (filter) {
        DateFilter.ALL -> trainings
        DateFilter.TODAY -> {
            val todayFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            trainings.filter {
                todayFormat.format(it.date) == todayFormat.format(today)
            }
        }

        DateFilter.THIS_WEEK -> {
            calendar.add(Calendar.DAY_OF_YEAR, -7)
            val weekAgo = calendar.time
            trainings.filter { it.date.after(weekAgo) }
        }

        DateFilter.THIS_MONTH -> {
            calendar.time = today
            calendar.add(Calendar.MONTH, -1)
            val monthAgo = calendar.time
            trainings.filter { it.date.after(monthAgo) }
        }

        DateFilter.LAST_3_MONTHS -> {
            calendar.time = today
            calendar.add(Calendar.MONTH, -3)
            val threeMonthsAgo = calendar.time
            trainings.filter { it.date.after(threeMonthsAgo) }
        }
    }
}

enum class DateFilter(val displayName: String) {
    ALL("All Time"),
    TODAY("Today"),
    THIS_WEEK("This Week"),
    THIS_MONTH("This Month"),
    LAST_3_MONTHS("Last 3 Months")
}

@Preview(showBackground = true)
@Composable
private fun DateFilterSectionPreview() {
    YambolTheme {
        DateFilterSection(
            selectedFilter = DateFilter.ALL,
            onFilterSelected = {},
            modifier = Modifier
        )
    }
}
