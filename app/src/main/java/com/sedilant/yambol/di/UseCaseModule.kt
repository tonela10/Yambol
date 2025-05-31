package com.sedilant.yambol.di

import com.sedilant.yambol.domain.DeleteTeamObjectiveUseCase
import com.sedilant.yambol.domain.DeleteTeamObjectiveUseCaseImpl
import com.sedilant.yambol.domain.GetPlayerByIdUseCase
import com.sedilant.yambol.domain.GetPlayerByIdUseCaseImpl
import com.sedilant.yambol.domain.GetPlayerStatsUseCase
import com.sedilant.yambol.domain.GetPlayerStatsUseCaseImpl
import com.sedilant.yambol.domain.GetPlayersUseCase
import com.sedilant.yambol.domain.GetPlayersUseCaseImpl
import com.sedilant.yambol.domain.GetTeamIdUseCase
import com.sedilant.yambol.domain.GetTeamIdUseCaseImpl
import com.sedilant.yambol.domain.GetTeamObjectivesUseCase
import com.sedilant.yambol.domain.GetTeamObjectivesUseCaseImpl
import com.sedilant.yambol.domain.GetTeamsUseCase
import com.sedilant.yambol.domain.GetTeamsUseCaseImpl
import com.sedilant.yambol.domain.InsertPlayerUseCase
import com.sedilant.yambol.domain.InsertPlayerUseCaseImpl
import com.sedilant.yambol.domain.InsertTeamObjectiveUseCase
import com.sedilant.yambol.domain.InsertTeamObjectiveUseCaseImpl
import com.sedilant.yambol.domain.InsertTeamUseCase
import com.sedilant.yambol.domain.InsertTeamUseCaseImpl
import com.sedilant.yambol.domain.ToggleTeamObjectiveUseCase
import com.sedilant.yambol.domain.ToggleTeamObjectiveUseCaseImpl
import com.sedilant.yambol.domain.UpdateTeamObjectiveUseCase
import com.sedilant.yambol.domain.UpdateTeamObjectiveUseCaseImpl
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
    abstract fun bindGetPlayersUseCase(impl: GetPlayersUseCaseImpl): GetPlayersUseCase

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
}
