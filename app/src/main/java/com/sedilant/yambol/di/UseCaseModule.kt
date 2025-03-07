package com.sedilant.yambol.di

import com.sedilant.yambol.domain.GetPlayersUseCase
import com.sedilant.yambol.domain.GetPlayersUseCaseImpl
import com.sedilant.yambol.domain.GetTeamIdUseCase
import com.sedilant.yambol.domain.GetTeamIdUseCaseImpl
import com.sedilant.yambol.domain.GetTeamsUseCase
import com.sedilant.yambol.domain.GetTeamsUseCaseImpl
import com.sedilant.yambol.domain.InsertPlayerUseCase
import com.sedilant.yambol.domain.InsertPlayerUseCaseImpl
import com.sedilant.yambol.domain.InsertTeamUseCase
import com.sedilant.yambol.domain.InsertTeamUseCaseImpl
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
}
