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
import androidx.navigation.toRoute
import com.sedilant.yambol.ui.NavigationBottomBar
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
}

@Composable
fun YambolApp(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val screensWithoutBottomBar = listOf(
        YambolScreen.CreateTeam.route.toString()
    )
    val shouldShowBottomBar =
        !screensWithoutBottomBar.contains(navBackStackEntry?.destination?.route)
    if (shouldShowBottomBar) {
        NavigationBottomBar(navController = navController)
    }

    NavHost(
        navController = navController,
        startDestination = YambolScreen.Home,
    ) {
        composable<YambolScreen.Home> {
            HomeScreen(
                modifier = modifier,
                onCreateTeam = {
                    navController.navigate(YambolScreen.CreateTeam)
                },
                onPlayerClicked = { player ->
                    navController.navigate(
                        YambolScreen.PlayerCardDetails(
                            player
                        )
                    )
                }
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
                    popUpTo(navController.graph.findStartDestination().id)
                }
            }
        }

        composable<YambolScreen.PlayerCardDetails> { navBackStackEntry ->
            val id = navBackStackEntry.toRoute<YambolScreen.PlayerCardDetails>().id
            PlayerCardScreen(
                playerId = id,
                modifier = modifier
            )
        }
    }
}