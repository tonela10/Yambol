package com.sedilant.yambol

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import org.koin.androidx.compose.koinViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.sedilant.yambol.ui.NavigationBottomBar
import com.sedilant.yambol.ui.createTeam.CreateTeamScreen
import com.sedilant.yambol.ui.createTrain.CreateTrainScreenV2
import com.sedilant.yambol.ui.home.HomeScreen
import com.sedilant.yambol.ui.login.LoginScreen
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
    data object Login : YambolScreen

    @Serializable
    data class CreateTeam(val isCancellable: Boolean) : YambolScreen

    @Serializable
    data class PlayerCardDetails(val id: Int?) : YambolScreen

    // TODO implement AddTeamStats screen when ready
    @Serializable
    data class AddTeamStats(val teamId: Int, val statsIds: List<Int>) : YambolScreen

    @Serializable
    data class TrainingDetails(val trainId: String) : YambolScreen

    @Serializable
    data class CreateTrain(val currentTeam: String) : YambolScreen
}

@Composable
fun YambolApp(
    viewModel: YambolAppViewModel = koinViewModel()
) {
    val navController = rememberNavController()
    val isAuthenticated = viewModel.isAuthenticated.collectAsState()

    // Determine initial route based on auth state
    val startDestination = if (isAuthenticated.value) {
        YambolScreen.Home
    } else {
        YambolScreen.Login
    }

    val loginRoute = YambolScreen.Login::class.qualifiedName.orEmpty()
    val homeRoute = YambolScreen.Home::class.qualifiedName.orEmpty()

    LaunchedEffect(isAuthenticated.value) {
        val currentRoute = navController.currentBackStackEntry?.destination?.route.orEmpty()
        if (isAuthenticated.value) {
            if (!currentRoute.contains(homeRoute)) {
                navController.navigate(YambolScreen.Home) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }
        } else {
            if (!currentRoute.contains(loginRoute)) {
                navController.navigate(YambolScreen.Login) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBottomBar(navController = navController)
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            composable<YambolScreen.Login> {
                LoginScreen()
            }

            composable<YambolScreen.Home> {
                HomeScreen(
                    onCreateTeam = { isCancellable ->
                        navController.navigate(
                            YambolScreen.CreateTeam(
                                isCancellable = isCancellable
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

            composable<YambolScreen.CreateTeam> { navBackStackEntry ->
                val args = navBackStackEntry.toRoute<YambolScreen.CreateTeam>()
                CreateTeamScreen(
                    isCancellable = args.isCancellable
                ) {
                    navController.navigate(YambolScreen.Home) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            inclusive = true
                        }
                    }
                }
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
