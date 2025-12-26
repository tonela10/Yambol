package com.sedilant.yambol.di

import android.app.Application
import android.content.Context
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
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
import com.sedilant.yambol.data.draftTrain.TrainingDatabase
import com.sedilant.yambol.data.draftTrain.TrainingDraftDao
import com.sedilant.yambol.data.draftTrain.TrainingDraftRepository
import com.sedilant.yambol.data.draftTrain.TrainingDraftRepositoryImpl
import com.sedilant.yambol.data.firebaseAuth.AuthRepository
import com.sedilant.yambol.data.firebaseAuth.AuthRepositoryImpl
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

    @Binds
    abstract fun bindTrainingDraftRepository(impl: TrainingDraftRepositoryImpl): TrainingDraftRepository

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
        fun providesTrainingDraftDao(context: Context): TrainingDraftDao {
            return TrainingDatabase.getDatabase(context).trainingDraftDao()
        }

        @Provides
        fun provideDataStoreManager(context: Context): DataStoreManager {
            return DataStoreManager(context)
        }
        @Provides
        @Singleton
        fun provideFirebaseAuth(): FirebaseAuth {
            return FirebaseAuth.getInstance()
        }

        @Provides
        @Singleton
        fun provideAuthUI(): AuthUI {
            return AuthUI.getInstance()
        }

        @Provides
        @Singleton
        fun provideAuthRepository(
            auth: FirebaseAuth
        ): AuthRepository {
            return AuthRepositoryImpl(auth)
        }
    }
}
