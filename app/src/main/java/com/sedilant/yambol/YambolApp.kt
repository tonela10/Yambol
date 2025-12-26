package com.sedilant.yambol

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.sedilant.yambol.ui.NavigationBottomBar
import com.sedilant.yambol.ui.addStats.AddTeamStatsScreen
import com.sedilant.yambol.ui.createTeam.CreateTeamScreen
import com.sedilant.yambol.ui.createTrain.CreateTrainScreenV2
import com.sedilant.yambol.ui.home.HomeScreen
import com.sedilant.yambol.ui.playerCard.PlayerCardScreen
import com.sedilant.yambol.ui.profile.ProfileScreen
import com.sedilant.yambol.ui.training.TrainingScreen
import com.sedilant.yambol.ui.trainingDetails.TrainingDetailsScreen
import kotlinx.serialization.Serializable

// TODO Create an issue to move this code to another file. Add the issue url when created
@Serializable
sealed interface YambolScreen {
    @Serializable
    data object Home : YambolScreen

    @Serializable
    data object Training : YambolScreen

    // TODO implement Statistics & Board screen when ready
    @Serializable
    data object Statistics : YambolScreen

    @Serializable
    data object Board : YambolScreen

    @Serializable
    data object Profile : YambolScreen

    @Serializable
    data object CreateTeam : YambolScreen

    @Serializable
    data class PlayerCardDetails(val id: Int?) : YambolScreen

    @Serializable
    data class AddTeamStats(val teamId: Int, val statsIds: List<Int>) : YambolScreen

    @Serializable
    data class TrainingDetails(val trainId: Int) : YambolScreen

    @Serializable
    data class CreateTrain(val currentTeam: Int) : YambolScreen
}

@Composable
fun YambolApp() {
    val navController = rememberNavController()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBottomBar(navController = navController)
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = YambolScreen.Home,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
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
                    onLastTrainClick = { trainId ->
                        navController.navigate(
                            YambolScreen.TrainingDetails(
                                trainId = trainId
                            )
                        )
                    }
                )
            }

            composable<YambolScreen.Training> {
                TrainingScreen(
                    onTrainClicked = { trainId ->
                        navController.navigate(
                            YambolScreen.TrainingDetails(
                                trainId = trainId
                            )
                        )
                    },
                    onNavigateToCreateTraining = { currentTeamId ->
                        navController.navigate(
                            YambolScreen.CreateTrain(
                                currentTeam = currentTeamId
                            )
                        )
                    }
                )
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

            composable<YambolScreen.TrainingDetails> { navBackStackEntry ->
                val args = navBackStackEntry.toRoute<YambolScreen.TrainingDetails>()

                TrainingDetailsScreen(
                    trainId = args.trainId,
                    onNavigateBack = { navController.popBackStack() },
                )
            }

            composable<YambolScreen.CreateTrain> { navBackStackEntry ->
                val args = navBackStackEntry.toRoute<YambolScreen.CreateTrain>()
                CreateTrainScreenV2(
                    onCancel = { navController.popBackStack() },
                    teamId = args.currentTeam
                )
            }
        }
    }
}
