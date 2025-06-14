package com.sedilant.yambol.di

import android.app.Application
import android.content.Context
import com.sedilant.yambol.data.DataStoreManager
import com.sedilant.yambol.data.PlayerDao
import com.sedilant.yambol.data.StatsDao
import com.sedilant.yambol.data.StatsRecordRepository
import com.sedilant.yambol.data.StatsRecordRepositoryImpl
import com.sedilant.yambol.data.TeamDatabase
import com.sedilant.yambol.data.TeamObjectivesDao
import com.sedilant.yambol.data.TeamRepository
import com.sedilant.yambol.data.TeamRepositoryImpl
import com.sedilant.yambol.data.TrainingDao
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    abstract fun binTeamRepository(impl: TeamRepositoryImpl): TeamRepository

    @Binds
    abstract fun bindStatsRecordRepository(impl: StatsRecordRepositoryImpl): StatsRecordRepository

    companion object {

        @Provides
        @Singleton
        fun provideContext(application: Application): Context {
            return application.applicationContext
        }

        @Provides
        fun providePlayerDao(context: Context): PlayerDao {
            return TeamDatabase.getDatabase(context).playerDao()
        }

        @Provides
        fun provideTeamObjectivesDao(context: Context): TeamObjectivesDao {
            return TeamDatabase.getDatabase(context).teamObjectivesDao()
        }

        @Provides
        fun provideStatsDao(context: Context): StatsDao {
            return TeamDatabase.getDatabase(context).statsDao()
        }

        @Provides
        fun provideTrainingDao(context: Context): TrainingDao {
            return TeamDatabase.getDatabase(context).trainingDao()
        }

        @Provides
        fun provideDataStoreManager(context: Context): DataStoreManager {
            return DataStoreManager(context)
        }
    }
}
