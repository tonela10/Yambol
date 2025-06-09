package com.sedilant.yambol.di

import com.sedilant.yambol.domain.DeleteTeamObjectiveUseCase
import com.sedilant.yambol.domain.DeleteTeamObjectiveUseCaseImpl
import com.sedilant.yambol.domain.get.GetPlayerByIdUseCase
import com.sedilant.yambol.domain.get.GetPlayerByIdUseCaseImpl
import com.sedilant.yambol.domain.get.GetPlayerStatsUseCase
import com.sedilant.yambol.domain.get.GetPlayerStatsUseCaseImpl
import com.sedilant.yambol.domain.get.GetPlayersByTeamIdUseCase
import com.sedilant.yambol.domain.get.GetPlayersByTeamIdUseCaseImpl
import com.sedilant.yambol.domain.get.GetStatByIdUseCase
import com.sedilant.yambol.domain.get.GetStatByIdUseCaseImpl
import com.sedilant.yambol.domain.get.GetStatByNameUseCase
import com.sedilant.yambol.domain.get.GetStatByNameUseCaseImpl
import com.sedilant.yambol.domain.get.GetTeamIdUseCase
import com.sedilant.yambol.domain.get.GetTeamIdUseCaseImpl
import com.sedilant.yambol.domain.get.GetTeamObjectivesUseCase
import com.sedilant.yambol.domain.get.GetTeamObjectivesUseCaseImpl
import com.sedilant.yambol.domain.get.GetTeamsUseCase
import com.sedilant.yambol.domain.get.GetTeamsUseCaseImpl
import com.sedilant.yambol.domain.insert.InsertPlayerUseCase
import com.sedilant.yambol.domain.insert.InsertPlayerUseCaseImpl
import com.sedilant.yambol.domain.insert.InsertTeamObjectiveUseCase
import com.sedilant.yambol.domain.insert.InsertTeamObjectiveUseCaseImpl
import com.sedilant.yambol.domain.insert.InsertTeamUseCase
import com.sedilant.yambol.domain.insert.InsertTeamUseCaseImpl
import com.sedilant.yambol.domain.insert.SavePlayerStatUseCase
import com.sedilant.yambol.domain.insert.SavePlayerStatUseCaseImpl
import com.sedilant.yambol.domain.ToggleTeamObjectiveUseCase
import com.sedilant.yambol.domain.ToggleTeamObjectiveUseCaseImpl
import com.sedilant.yambol.domain.UpdateTeamObjectiveUseCase
import com.sedilant.yambol.domain.UpdateTeamObjectiveUseCaseImpl
import com.sedilant.yambol.domain.get.GetAllTrainsByTeamIdUseCase
import com.sedilant.yambol.domain.get.GetAllTrainsByTeamIdUseCaseImpl
import com.sedilant.yambol.domain.get.GetLastTrainOfTeamUseCase
import com.sedilant.yambol.domain.get.GetLastTrainOfTeamUseCaseImpl
import com.sedilant.yambol.domain.get.GetTrainWithTrainTaskByTrainIdUseCase
import com.sedilant.yambol.domain.get.GetTrainWithTrainTaskByTrainIdUseCaseImpl
import com.sedilant.yambol.domain.insert.CreateTrainTaskUseCase
import com.sedilant.yambol.domain.insert.CreateTrainTaskUseCaseImpl
import com.sedilant.yambol.domain.insert.CreateTrainUseCase
import com.sedilant.yambol.domain.insert.CreateTrainUseCaseImpl
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
    abstract fun bindGetTeamIdUseCase(impl: GetTeamIdUseCaseImpl): GetTeamIdUseCase

    @Binds
    abstract fun bindInsertPlayer(impl: InsertPlayerUseCaseImpl): InsertPlayerUseCase

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
    abstract fun bindGetPlayerStatsdUseCase(impl: GetPlayerStatsUseCaseImpl): GetPlayerStatsUseCase

    @Binds
    abstract fun bindGetStatByIdUseCase(impl: GetStatByIdUseCaseImpl): GetStatByIdUseCase

    @Binds
    abstract fun bindSavePlayerStatUseCase(impl: SavePlayerStatUseCaseImpl): SavePlayerStatUseCase

    @Binds
    abstract fun bindGetStatByNameUseCase(impl: GetStatByNameUseCaseImpl): GetStatByNameUseCase

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
}
