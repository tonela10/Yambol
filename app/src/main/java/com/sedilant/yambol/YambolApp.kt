package com.sedilant.yambol

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.sedilant.yambol.ui.NavigationBottomBar
import com.sedilant.yambol.ui.board.BoardScreen
import com.sedilant.yambol.ui.createTeam.CreateTeamScreen
import com.sedilant.yambol.ui.home.HomeScreen
import com.sedilant.yambol.ui.profile.ProfileScreen
import com.sedilant.yambol.ui.statistics.StatisticsScreen
import com.sedilant.yambol.ui.training.TrainingScreen

enum class YambolScreen(@StringRes val title: Int) {
    Home(title = R.string.home_screen_title),
    Training(title = R.string.training_screen_title),
    Statistics(title = R.string.statistic_screen_title),
    Board(title = R.string.board_screen_title),
    Profile(title = R.string.profile_screen_title),
    CreateTeam(title = R.string.create_team_screen),
}

@Composable
fun YambolApp(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val screensWithoutBottomBar = listOf(
        YambolScreen.CreateTeam.name
    )
    val shouldShowBottomBar =
        !screensWithoutBottomBar.contains(navBackStackEntry?.destination?.route)
    if (shouldShowBottomBar) {
        NavigationBottomBar(modifier, navController)
    }

    NavHost(
        navController = navController,
        startDestination = YambolScreen.Home.name,
        modifier = modifier,
    ) {
        composable(route = YambolScreen.Home.name) {
            HomeScreen(onCreateTeam = { navController.navigate(YambolScreen.CreateTeam.name) })
        }

        composable(route = YambolScreen.Training.name) {
            TrainingScreen()
        }

        composable(route = YambolScreen.Statistics.name) {
            StatisticsScreen()
        }

        composable(route = YambolScreen.Board.name) {
            BoardScreen()
        }

        composable(route = YambolScreen.Profile.name) {
            ProfileScreen()
        }
        composable(route = YambolScreen.CreateTeam.name) {
            CreateTeamScreen(
                onNavigateHome = {
                    navController.navigate(YambolScreen.Home.name) {
                        popUpTo(navController.graph.findStartDestination().id)
                    }
                }
            )
        }
    }
}