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
import org.jetbrains.compose.resources.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sedilant.yambol.core.designsystem.Res
import com.sedilant.yambol.core.designsystem.*
import com.sedilant.yambol.domain.models.TrainDomainModel
import com.sedilant.yambol.ui.theme.YambolTheme
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime

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
                            painter = painterResource(Res.drawable.calendar_month_24dp),
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
    val tz = TimeZone.currentSystemDefault()
    val today = Clock.System.now().toLocalDateTime(tz).date

    return when (filter) {
        DateFilter.ALL -> trainings
        DateFilter.TODAY -> {
            trainings.filter { it.date.toLocalDateTime(tz).date == today }
        }
        DateFilter.THIS_WEEK -> {
            val weekAgo = today.minus(DatePeriod(days = 7))
            trainings.filter { it.date.toLocalDateTime(tz).date >= weekAgo }
        }
        DateFilter.THIS_MONTH -> {
            val monthAgo = today.minus(DatePeriod(months = 1))
            trainings.filter { it.date.toLocalDateTime(tz).date >= monthAgo }
        }
        DateFilter.LAST_3_MONTHS -> {
            val threeMonthsAgo = today.minus(DatePeriod(months = 3))
            trainings.filter { it.date.toLocalDateTime(tz).date >= threeMonthsAgo }
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
