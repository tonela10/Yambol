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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.sedilant.yambol.ui.theme.YambolTheme
import com.sedilant.yambol.R
import com.sedilant.yambol.YambolScreen

@Composable
fun NavigationBottomBar(modifier: Modifier = Modifier, navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val topLevelRoutes = listOf(
        TopLevelRoute(
            "Home",
            YambolScreen.Home,
            Icons.Filled.Home,
            Icons.Outlined.Home
        ),
        TopLevelRoute(
            "Training",
            YambolScreen.Training,
            ImageVector.vectorResource(id = R.drawable.sports_basketball_filled),
            ImageVector.vectorResource(id = R.drawable.sports_basketball_oulined)
        ),
        TopLevelRoute(
            "Statistics",
            YambolScreen.Statistics,
            ImageVector.vectorResource(id = R.drawable.analytics_filled),
            ImageVector.vectorResource(id = R.drawable.analytics_outlined),
        ),
        TopLevelRoute(
            "Board",
            YambolScreen.Board,
            ImageVector.vectorResource(id = R.drawable.draw_filled),
            ImageVector.vectorResource(id = R.drawable.draw_outlined),
        ),
        TopLevelRoute(
            "Profile",
            YambolScreen.Profile,
            Icons.Filled.AccountCircle,
            Icons.Outlined.AccountCircle,
        ),
    )

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Bottom
    ) {
        NavigationBar {
            topLevelRoutes.forEach { item ->
                val isSelected = currentDestination?.route?.startsWith(item.route::class.qualifiedName.orEmpty()) == true

                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = if (isSelected) item.filledIcon else item.outlinedIcon,
                            contentDescription = item.name
                        )
                    },
                    label = { Text(item.name) },
                    selected = isSelected,
                    onClick = {
                        navController.navigate(item.route) {
                            // Pop up to the start destination of the graph to
                            // avoid building up a large stack of destinations
                            // on the back stack as users select items
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            // Avoid multiple copies of the same destination when
                            // reelecting the same item
                            launchSingleTop = true
                            // Restore state when reelecting a previously selected item
                            restoreState = true
                        }
                    }
                )
            }
        }
    }
}

data class TopLevelRoute(
    val name: String,
    val route: YambolScreen,
    val filledIcon: ImageVector,
    val outlinedIcon: ImageVector
)

@Preview(showBackground = true)
@Composable
fun NavigationBarPreview() {
    YambolTheme {
        NavigationBottomBar(
            navController = rememberNavController()
        )
    }
}