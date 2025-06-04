package com.sedilant.yambol

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.sedilant.yambol.ui.NavigationBottomBar
import com.sedilant.yambol.ui.addStats.AddTeamStatsScreen
import com.sedilant.yambol.ui.board.BoardScreen
import com.sedilant.yambol.ui.createTeam.CreateTeamScreen
import com.sedilant.yambol.ui.home.HomeScreen
import com.sedilant.yambol.ui.playerCard.PlayerCardScreen
import com.sedilant.yambol.ui.profile.ProfileScreen
import com.sedilant.yambol.ui.statistics.StatisticsScreen
import com.sedilant.yambol.ui.training.TrainingScreen
import kotlinx.serialization.Serializable

@Serializable
sealed class YambolScreen(@StringRes val route: Int) {
    @Serializable
    data object Home : YambolScreen(route = R.string.home_screen_title)

    @Serializable
    data object Training : YambolScreen(route = R.string.training_screen_title)

    @Serializable
    data object Statistics : YambolScreen(route = R.string.statistic_screen_title)

    @Serializable
    data object Board : YambolScreen(route = R.string.board_screen_title)

    @Serializable
    data object Profile : YambolScreen(route = R.string.profile_screen_title)

    @Serializable
    data object CreateTeam : YambolScreen(route = R.string.create_team_screen)

    @Serializable
    data class PlayerCardDetails(val id: Int?) :
        YambolScreen(route = R.string.player_card_screen)

    @Serializable
    data class AddTeamStats(val teamId: Int, val statsIds: List<Int>) : YambolScreen(
        route = R.string.add_teams_stats_screen
    )
}

@Composable
fun YambolApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    // Check the current destination against the actual screen types
    val currentDestination = navBackStackEntry?.destination?.route
    val shouldShowBottomBar = when {
        currentDestination?.contains("CreateTeam") == true -> false
        currentDestination?.contains("PlayerCardDetails") == true -> false
        currentDestination?.contains("AddTeamStats") == true -> false
        else -> true
    }

    Scaffold(
        //contentWindowInsets = WindowInsets(0.dp),
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (shouldShowBottomBar) {
                NavigationBottomBar(navController = navController)
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = YambolScreen.Home,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable<YambolScreen.Home> {
                HomeScreen(
                    onCreateTeam = {
                        navController.navigate(YambolScreen.CreateTeam)
                    },
                    onPlayerClicked = { player ->
                        navController.navigate(
                            YambolScreen.PlayerCardDetails(player)
                        )
                    },
                    onRegisterTrain = { teamId, statIds ->
                        navController.navigate(
                            YambolScreen.AddTeamStats(
                                teamId = teamId,
                                statsIds = statIds
                            )
                        )
                    },
                )
            }

            composable<YambolScreen.Training> {
                TrainingScreen()
            }

            composable<YambolScreen.Statistics> {
                StatisticsScreen()
            }

            composable<YambolScreen.Board> {
                BoardScreen()
            }

            composable<YambolScreen.Profile> {
                ProfileScreen()
            }

            composable<YambolScreen.CreateTeam> {
                CreateTeamScreen {
                    navController.navigate(YambolScreen.Home) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            inclusive = true
                        }
                    }
                }
            }

            composable<YambolScreen.PlayerCardDetails> { navBackStackEntry ->
                val id = navBackStackEntry.toRoute<YambolScreen.PlayerCardDetails>().id
                PlayerCardScreen(
                    playerId = id,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable<YambolScreen.AddTeamStats> { navBackStackEntry ->
                val args = navBackStackEntry.toRoute<YambolScreen.AddTeamStats>()

                AddTeamStatsScreen(
                    teamId = args.teamId,
                    statIds = args.statsIds,
                    onNavigateBack = { navController.popBackStack() },
                    onCompleted = { navController.popBackStack() },
                )
            }
        }
    }
}
