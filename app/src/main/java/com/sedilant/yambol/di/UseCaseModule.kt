package com.sedilant.yambol.di

import com.sedilant.yambol.domain.GetPlayersUseCase
import com.sedilant.yambol.domain.GetPlayersUseCaseImpl
import com.sedilant.yambol.domain.GetTeamsUseCase
import com.sedilant.yambol.domain.GetTeamsUseCaseImpl
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
}
