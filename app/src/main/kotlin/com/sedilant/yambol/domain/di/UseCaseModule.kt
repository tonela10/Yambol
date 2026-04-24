package com.sedilant.yambol.domain.di

import com.sedilant.yambol.UpdateTeamUseCaseImpl
import com.sedilant.yambol.domain.CheckJerseyNumberUseCase
import com.sedilant.yambol.domain.CheckJerseyNumberUseCaseImpl
import com.sedilant.yambol.domain.DeleteTeamObjectiveUseCase
import com.sedilant.yambol.domain.DeleteTeamObjectiveUseCaseImpl
import com.sedilant.yambol.domain.ToggleTeamObjectiveUseCase
import com.sedilant.yambol.domain.ToggleTeamObjectiveUseCaseImpl
import com.sedilant.yambol.domain.UpdatePlayerUseCase
import com.sedilant.yambol.domain.UpdatePlayerUseCaseImpl
import com.sedilant.yambol.domain.UpdateTeamObjectiveUseCase
import com.sedilant.yambol.domain.UpdateTeamObjectiveUseCaseImpl
import com.sedilant.yambol.domain.UpdateTeamUseCase
import com.sedilant.yambol.domain.delete.DeletePlayerUseCase
import com.sedilant.yambol.domain.delete.DeletePlayerUseCaseImpl
import com.sedilant.yambol.domain.get.GetAllTaskUseCase
import com.sedilant.yambol.domain.get.GetAllTasksUseCaseImpl
import com.sedilant.yambol.domain.get.GetAllTrainsByTeamIdUseCase
import com.sedilant.yambol.domain.get.GetAllTrainsByTeamIdUseCaseImpl
import com.sedilant.yambol.domain.get.GetLastTrainOfTeamUseCase
import com.sedilant.yambol.domain.get.GetLastTrainOfTeamUseCaseImpl
import com.sedilant.yambol.domain.get.GetPlayerByIdUseCase
import com.sedilant.yambol.domain.get.GetPlayerByIdUseCaseImpl
import com.sedilant.yambol.domain.get.GetPlayersByTeamIdUseCase
import com.sedilant.yambol.domain.get.GetPlayersByTeamIdUseCaseImpl
import com.sedilant.yambol.domain.get.GetTeamObjectivesUseCase
import com.sedilant.yambol.domain.get.GetTeamObjectivesUseCaseImpl
import com.sedilant.yambol.domain.get.GetTeamsUseCase
import com.sedilant.yambol.domain.get.GetTeamsUseCaseImpl
import com.sedilant.yambol.domain.get.GetTrainWithTrainTaskByTrainIdUseCase
import com.sedilant.yambol.domain.get.GetTrainWithTrainTaskByTrainIdUseCaseImpl
import com.sedilant.yambol.domain.insert.CreateTrainTaskUseCase
import com.sedilant.yambol.domain.insert.CreateTrainTaskUseCaseImpl
import com.sedilant.yambol.domain.insert.CreateTrainUseCase
import com.sedilant.yambol.domain.insert.CreateTrainUseCaseImpl
import com.sedilant.yambol.domain.insert.InsertPlayerUseCase
import com.sedilant.yambol.domain.insert.InsertPlayerUseCaseImpl
import com.sedilant.yambol.domain.insert.InsertPlayersUseCase
import com.sedilant.yambol.domain.insert.InsertPlayersUseCaseImpl
import com.sedilant.yambol.domain.insert.InsertTeamObjectiveUseCase
import com.sedilant.yambol.domain.insert.InsertTeamObjectiveUseCaseImpl
import com.sedilant.yambol.domain.insert.InsertTeamUseCase
import com.sedilant.yambol.domain.insert.InsertTeamUseCaseImpl
import org.koin.dsl.module

val useCaseModule = module {
    factory<GetTeamsUseCase>                       { GetTeamsUseCaseImpl(get(), get()) }
    factory<GetPlayersByTeamIdUseCase>             { GetPlayersByTeamIdUseCaseImpl(get(), get()) }
    factory<InsertPlayerUseCase>                   { InsertPlayerUseCaseImpl(get(), get()) }
    factory<InsertPlayersUseCase>                  { InsertPlayersUseCaseImpl(get(), get()) }
    factory<InsertTeamUseCase>                     { InsertTeamUseCaseImpl(get(), get()) }
    factory<GetTeamObjectivesUseCase>              { GetTeamObjectivesUseCaseImpl(get(), get()) }
    factory<InsertTeamObjectiveUseCase>            { InsertTeamObjectiveUseCaseImpl(get(), get()) }
    factory<UpdateTeamObjectiveUseCase>            { UpdateTeamObjectiveUseCaseImpl(get(), get()) }
    factory<ToggleTeamObjectiveUseCase>            { ToggleTeamObjectiveUseCaseImpl(get()) }
    factory<DeleteTeamObjectiveUseCase>            { DeleteTeamObjectiveUseCaseImpl(get()) }
    factory<GetPlayerByIdUseCase>                  { GetPlayerByIdUseCaseImpl(get()) }
    factory<GetAllTrainsByTeamIdUseCase>           { GetAllTrainsByTeamIdUseCaseImpl(get(), get()) }
    factory<GetTrainWithTrainTaskByTrainIdUseCase> { GetTrainWithTrainTaskByTrainIdUseCaseImpl(get(), get(), get()) }
    factory<CreateTrainUseCase>                    { CreateTrainUseCaseImpl(get(), get()) }
    factory<CreateTrainTaskUseCase>                { CreateTrainTaskUseCaseImpl(get(), get(), get()) }
    factory<GetLastTrainOfTeamUseCase>             { GetLastTrainOfTeamUseCaseImpl(get(), get()) }
    factory<UpdateTeamUseCase>                     { UpdateTeamUseCaseImpl(get()) }
    factory<UpdatePlayerUseCase>                   { UpdatePlayerUseCaseImpl(get(), get()) }
    factory<CheckJerseyNumberUseCase>              { CheckJerseyNumberUseCaseImpl(get(), get()) }
    factory<DeletePlayerUseCase>                   { DeletePlayerUseCaseImpl(get()) }
    factory<GetAllTaskUseCase>                     { GetAllTasksUseCaseImpl(get(), get()) }
}
