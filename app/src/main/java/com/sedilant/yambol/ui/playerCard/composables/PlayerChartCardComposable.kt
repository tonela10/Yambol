package com.sedilant.yambol.ui.playerCard.composables

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sedilant.yambol.ui.playerCard.Ability
import com.sedilant.yambol.ui.theme.YambolTheme
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

@Composable
fun PlayerChartCard(
    modifier: Modifier = Modifier
) {
    val listOfAbilities = listOf(
        Ability("Bounce", 2),
        Ability("Pass", 4),
        Ability("Shoot", 1),
        Ability("Defense", 5)
    )

    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(4.dp),
        // colors = CardDefaults.cardColors(MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Ability Chart",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                contentAlignment = Alignment.Center
            ) {
                RadarChart(
                    abilities = listOfAbilities,
                    modifier = Modifier.size(200.dp)
                )
            }
        }
    }
}


@Composable
private fun RadarChart(
    abilities: List<Ability>,
    modifier: Modifier = Modifier,
    maxValue: Int = 5
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val textStyle = MaterialTheme.typography.bodySmall

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = size.center
            val radius = min(size.width, size.height) / 2 * 0.7f // Reduced to make room for labels
            val angleStep = 360f / abilities.size

            // Draw grid circles
            for (i in 1..maxValue) {
                val gridRadius = radius * (i.toFloat() / maxValue)
                drawCircle(
                    color = onSurfaceColor.copy(alpha = 0.2f),
                    radius = gridRadius,
                    center = center,
                    style = Stroke(width = 1.dp.toPx())
                )
            }

            // Draw axes
            abilities.forEachIndexed { index, _ ->
                val angle = Math.toRadians((index * angleStep - 90).toDouble())
                val endX = center.x + cos(angle).toFloat() * radius
                val endY = center.y + sin(angle).toFloat() * radius

                drawLine(
                    color = onSurfaceColor.copy(alpha = 0.3f),
                    start = center,
                    end = Offset(endX, endY),
                    strokeWidth = 1.dp.toPx()
                )
            }

            // Draw ability polygon
            val path = Path()
            abilities.forEachIndexed { index, ability ->
                val angle = Math.toRadians((index * angleStep - 90).toDouble())
                val valueRadius = radius * (ability.value.toFloat() / maxValue)
                val x = center.x + cos(angle).toFloat() * valueRadius
                val y = center.y + sin(angle).toFloat() * valueRadius

                if (index == 0) {
                    path.moveTo(x, y)
                } else {
                    path.lineTo(x, y)
                }
            }
            path.close()

            // Fill the polygon
            drawPath(
                path = path,
                color = primaryColor.copy(alpha = 0.3f)
            )

            // Draw the polygon border
            drawPath(
                path = path,
                color = primaryColor,
                style = Stroke(width = 2.dp.toPx())
            )

            // Draw points
            abilities.forEachIndexed { index, ability ->
                val angle = Math.toRadians((index * angleStep - 90).toDouble())
                val valueRadius = radius * (ability.value.toFloat() / maxValue)
                val x = center.x + cos(angle).toFloat() * valueRadius
                val y = center.y + sin(angle).toFloat() * valueRadius

                drawCircle(
                    color = primaryColor,
                    radius = 4.dp.toPx(),
                    center = Offset(x, y)
                )
            }
        }

//        // Draw ability labels positioned around the chart
//        abilities.forEachIndexed { index, ability ->
//            val angle = Math.toRadians((index * (360f / abilities.size) - 90).toDouble())
//            val labelRadius = (min(size.width, size.height) / 2 * 0.9f)
//            val labelX = cos(angle).toFloat() * labelRadius
//            val labelY = sin(angle).toFloat() * labelRadius
//
//            Box(
//                modifier = Modifier
//                    .offset(
//                        x = (labelX - 30.dp.value).dp, // Offset to center the text
//                        y = (labelY - 10.dp.value).dp
//                    )
//                    .width(60.dp),
//                contentAlignment = Alignment.Center
//            ) {
//                Text(
//                    text = "${ability.name}\n(${ability.value})",
//                    style = textStyle,
//                    textAlign = TextAlign.Center,
//                    color = onSurfaceColor,
//                    fontWeight = FontWeight.Medium,
//                    lineHeight = MaterialTheme.typography.bodySmall.lineHeight
//                )
//            }
//        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PlayerChartCardPreview() {
    YambolTheme {
        PlayerAbilityCard()
    }
}