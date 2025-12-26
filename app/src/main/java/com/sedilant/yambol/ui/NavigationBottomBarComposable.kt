package com.sedilant.yambol.ui

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.sedilant.yambol.R
import com.sedilant.yambol.YambolScreen
import com.sedilant.yambol.ui.theme.YambolTheme

data class TopLevelRoute(
    @StringRes val nameResId: Int,
    val route: YambolScreen,
    val filledIcon: IconType,
    val outlinedIcon: IconType
)

sealed interface IconType {
    data class Vector(val imageVector: ImageVector) : IconType
    data class Resource(val id: Int) : IconType
}

@Composable
fun NavigationBottomBar(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val topLevelRoutes = remember {
        listOf(
            TopLevelRoute(
                nameResId = R.string.nav_home,
                route = YambolScreen.Home,
                filledIcon = IconType.Vector(Icons.Filled.Home),
                outlinedIcon = IconType.Vector(Icons.Outlined.Home)
            ),
            TopLevelRoute(
                nameResId = R.string.nav_training,
                route = YambolScreen.Training,
                filledIcon = IconType.Resource(R.drawable.sports_basketball_filled),
                outlinedIcon = IconType.Resource(R.drawable.sports_basketball_oulined)
            ),
            TopLevelRoute(
                nameResId = R.string.nav_profile,
                route = YambolScreen.Profile,
                filledIcon = IconType.Vector(Icons.Filled.AccountCircle),
                outlinedIcon = IconType.Vector(Icons.Outlined.AccountCircle),
            ),
        )
    }

    // Visibility logic: show only if we are on a top-level route or its children
    val showBottomBar = topLevelRoutes.any { item ->
        currentDestination?.hierarchy?.any {
            it.route?.contains(item.route::class.qualifiedName.orEmpty()) == true
        } == true
    }

    AnimatedVisibility(
        visible = showBottomBar,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it }),
    ) {
        NavigationBar(
            modifier = modifier,
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = NavigationBarDefaults.Elevation
        ) {
            topLevelRoutes.forEach { item ->
                val isSelected = currentDestination?.hierarchy?.any { destination ->
                    destination.route?.contains(item.route::class.qualifiedName.orEmpty()) == true
                } == true

                val label = stringResource(item.nameResId)

                NavigationBarItem(
                    selected = isSelected,
                    label = { Text(label) },
                    alwaysShowLabel = true,
                    icon = {
                        val iconToRender = if (isSelected) item.filledIcon else item.outlinedIcon
                        NavigationIcon(iconType = iconToRender, contentDescription = label)
                    },
                    onClick = {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    }
}

/**
 * Clean helper to handle both Vector and Resource icons
 */
@Composable
private fun NavigationIcon(
    iconType: IconType,
    contentDescription: String
) {
    when (iconType) {
        is IconType.Vector -> Icon(
            imageVector = iconType.imageVector,
            contentDescription = contentDescription
        )
        is IconType.Resource -> Icon(
            imageVector = ImageVector.vectorResource(id = iconType.id),
            contentDescription = contentDescription
        )
    }
}

@Preview(showBackground = true)
@Composable
fun NavigationBarPreview() {
    YambolTheme {
        NavigationBottomBar(
            navController = rememberNavController()
        )
    }
}