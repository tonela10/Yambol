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
import com.sedilant.yambol.domain.UpdateTaskUseCase
import com.sedilant.yambol.domain.UpdateTaskUseCaseImpl
import com.sedilant.yambol.domain.UpdateTeamObjectiveUseCase
import com.sedilant.yambol.domain.UpdateTeamObjectiveUseCaseImpl
import com.sedilant.yambol.domain.UpdateTeamUseCase
import com.sedilant.yambol.domain.delete.DeletePlayerUseCase
import com.sedilant.yambol.domain.delete.DeletePlayerUseCaseImpl
import com.sedilant.yambol.domain.delete.DeleteTaskUseCase
import com.sedilant.yambol.domain.delete.DeleteTaskUseCaseImpl
import com.sedilant.yambol.domain.get.GetAllTaskUseCase
import com.sedilant.yambol.domain.get.GetAllTasksUseCaseImpl
import com.sedilant.yambol.domain.get.GetTaskConceptNameUseCase
import com.sedilant.yambol.domain.get.GetTaskConceptNameUseCaseImpl
import com.sedilant.yambol.domain.get.GetTaskByIdUseCase
import com.sedilant.yambol.domain.get.GetTaskByIdUseCaseImpl
import com.sedilant.yambol.domain.get.IsTaskUsedByAnyTrainUseCase
import com.sedilant.yambol.domain.get.IsTaskUsedByAnyTrainUseCaseImpl
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
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class UseCaseModule {

    @Binds
    abstract fun bindGetTeamsUseCase(impl: GetTeamsUseCaseImpl): GetTeamsUseCase

    @Binds
    abstract fun bindGetPlayersUseCase(impl: GetPlayersByTeamIdUseCaseImpl): GetPlayersByTeamIdUseCase

    @Binds
    abstract fun bindInsertPlayer(impl: InsertPlayerUseCaseImpl): InsertPlayerUseCase

    @Binds
    abstract fun bindInsertPlayers(impl: InsertPlayersUseCaseImpl): InsertPlayersUseCase

    @Binds
    abstract fun bindInsertTeam(impl: InsertTeamUseCaseImpl): InsertTeamUseCase

    @Binds
    abstract fun bindGetTeamObjectives(impl: GetTeamObjectivesUseCaseImpl): GetTeamObjectivesUseCase

    @Binds
    abstract fun bindInsertTeamObjectives(impl: InsertTeamObjectiveUseCaseImpl): InsertTeamObjectiveUseCase

    @Binds
    abstract fun bindUpdateTeamObjective(impl: UpdateTeamObjectiveUseCaseImpl): UpdateTeamObjectiveUseCase

    @Binds
    abstract fun bindToggleTeamObjective(impl: ToggleTeamObjectiveUseCaseImpl): ToggleTeamObjectiveUseCase

    @Binds
    abstract fun bindDeleteTeamObjective(impl: DeleteTeamObjectiveUseCaseImpl): DeleteTeamObjectiveUseCase

    @Binds
    abstract fun bindGetPlayerById(impl: GetPlayerByIdUseCaseImpl): GetPlayerByIdUseCase

    @Binds
    abstract fun bindGetAllTrainsByTeamIdUseCase(impl: GetAllTrainsByTeamIdUseCaseImpl): GetAllTrainsByTeamIdUseCase

    @Binds
    abstract fun bindGetTrainWithTrainTaskByTrainIdUseCase(impl: GetTrainWithTrainTaskByTrainIdUseCaseImpl): GetTrainWithTrainTaskByTrainIdUseCase

    @Binds
    abstract fun bindCreateTrainUseCase(impl: CreateTrainUseCaseImpl): CreateTrainUseCase

    @Binds
    abstract fun bindCreateTrainTaskUseCase(impl: CreateTrainTaskUseCaseImpl): CreateTrainTaskUseCase

    @Binds
    abstract fun bindGetLastTrainOfTeamUseCase(impl: GetLastTrainOfTeamUseCaseImpl): GetLastTrainOfTeamUseCase

    @Binds
    abstract fun bindUpdateTeamUseCase(impl: UpdateTeamUseCaseImpl): UpdateTeamUseCase

    @Binds
    abstract fun bindUpdatePlayerUseCase(impl: UpdatePlayerUseCaseImpl): UpdatePlayerUseCase

    @Binds
    abstract fun bindUpdateTaskUseCase(impl: UpdateTaskUseCaseImpl): UpdateTaskUseCase

    @Binds
    abstract fun bindCheckJerseyNumberUseCase(impl: CheckJerseyNumberUseCaseImpl): CheckJerseyNumberUseCase

    @Binds
    abstract fun bindDeletePlayerUseCase(impl: DeletePlayerUseCaseImpl): DeletePlayerUseCase

    @Binds
    abstract fun bindDeleteTaskUseCase(impl: DeleteTaskUseCaseImpl): DeleteTaskUseCase

    @Binds
    abstract fun bindGetAllTasksUseCase(impl: GetAllTasksUseCaseImpl): GetAllTaskUseCase

    @Binds
    abstract fun bindGetTaskConceptNameUseCase(impl: GetTaskConceptNameUseCaseImpl): GetTaskConceptNameUseCase

    @Binds
    abstract fun bindGetTaskByIdUseCase(impl: GetTaskByIdUseCaseImpl): GetTaskByIdUseCase

    @Binds
    abstract fun bindIsTaskUsedByAnyTrainUseCase(impl: IsTaskUsedByAnyTrainUseCaseImpl): IsTaskUsedByAnyTrainUseCase
}
