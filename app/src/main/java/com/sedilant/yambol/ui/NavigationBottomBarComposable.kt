package com.sedilant.yambol.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import com.sedilant.yambol.R
import com.sedilant.yambol.ui.theme.YambolTheme

@Composable
fun NavigationBottomBar(modifier: Modifier = Modifier) {
    var selectedItem by remember { mutableIntStateOf(0) }
    val items = listOf("Home", "Training", "Statistics", "Board", "Profile")
    val selectedIcons = listOf(
        Icons.Filled.Home,
        ImageVector.vectorResource(id = R.drawable.sports_basketball_filled),
        ImageVector.vectorResource(id = R.drawable.analytics_filled),
        ImageVector.vectorResource(id = R.drawable.draw_filled),
        Icons.Filled.AccountCircle,
    )
    val unselectedIcons =
        listOf(
            Icons.Outlined.Home,
            ImageVector.vectorResource(id = R.drawable.sports_basketball_oulined),
            ImageVector.vectorResource(id = R.drawable.analytics_outlined),
            ImageVector.vectorResource(id = R.drawable.draw_outlined),
            Icons.Outlined.AccountCircle,
        )
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Bottom
    ) {
        NavigationBar {
            items.forEachIndexed { index, item ->
                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = if (selectedItem == index) selectedIcons[index] else unselectedIcons[index],
                            contentDescription = item
                        )
                    },
                    label = { Text(item) },
                    selected = selectedItem == index,
                    onClick = { selectedItem = index }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NavigationBarPreview() {
    YambolTheme {
        NavigationBottomBar()
    }
}