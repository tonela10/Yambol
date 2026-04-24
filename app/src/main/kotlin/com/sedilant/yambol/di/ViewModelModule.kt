package com.sedilant.yambol.di

import androidx.lifecycle.SavedStateHandle
import com.sedilant.yambol.YambolAppViewModel
import com.sedilant.yambol.ui.createTeam.CreateTeamViewModel
import com.sedilant.yambol.ui.createTrain.CreateTrainV2ViewModel
import com.sedilant.yambol.ui.createTrain.stepsScreens.basicInfo.CreateTrainBasicInfoViewModel
import com.sedilant.yambol.ui.createTrain.stepsScreens.concepts.CreateTrainConceptsViewModel
import com.sedilant.yambol.ui.createTrain.stepsScreens.tasks.CreateTrainTasksViewModel
import com.sedilant.yambol.ui.createTrain.stepsScreens.trainDetails.CreateTrainDetailsViewModel
import com.sedilant.yambol.ui.home.HomeViewModel
import com.sedilant.yambol.ui.login.LoginViewModel
import com.sedilant.yambol.ui.profile.ProfileViewModel
import com.sedilant.yambol.ui.training.TrainingViewModel
import com.sedilant.yambol.ui.trainingDetails.TrainingDetailsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(::YambolAppViewModel)
    viewModelOf(::LoginViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::CreateTeamViewModel)
    viewModelOf(::CreateTrainConceptsViewModel)
    viewModelOf(::CreateTrainTasksViewModel)
    viewModelOf(::TrainingViewModel)
    viewModelOf(::ProfileViewModel)

    // ViewModels with runtime (navigation) parameters
    viewModel { params -> CreateTrainV2ViewModel(teamId = params.get()) }
    viewModel { params ->
        CreateTrainBasicInfoViewModel(
            teamId = params.get(),
            repository = get(),
            getAllTeamsUseCase = get(),
            savedStateHandle = get<SavedStateHandle>()
        )
    }
    viewModel { params ->
        CreateTrainDetailsViewModel(
            teamId = params.get(),
            draftRepository = get(),
            conceptRepository = get(),
            createTrainUseCase = get(),
            createTrainTaskUseCase = get(),
            getTeamsUseCase = get(),
            authRepository = get()
        )
    }
    viewModel { params ->
        TrainingDetailsViewModel(
            trainId = params.get(),
            getTrainWithTrainTaskByTrainIdUseCase = get()
        )
    }
}
