package com.sedilant.yambol.data.di

import android.app.Application
import android.content.Context
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.sedilant.yambol.data.DataStoreManager
import com.sedilant.yambol.data.YambolDatabase
import com.sedilant.yambol.data.draftTrain.TrainingDraftDao
import com.sedilant.yambol.data.draftTrain.TrainingDraftRepository
import com.sedilant.yambol.data.draftTrain.TrainingDraftRepositoryImpl
import com.sedilant.yambol.data.firebaseAuth.AuthRepository
import com.sedilant.yambol.data.firebaseAuth.AuthRepositoryImpl
import com.sedilant.yambol.data.team.TeamDao
import com.sedilant.yambol.data.team.TeamObjectivesDao
import com.sedilant.yambol.data.team.TeamRepository
import com.sedilant.yambol.data.team.TeamRepositoryImpl
import com.sedilant.yambol.data.team.TrainingDao
import com.sedilant.yambol.data.team.concept.ConceptDao
import com.sedilant.yambol.data.team.concept.ConceptRepository
import com.sedilant.yambol.data.team.concept.ConceptRepositoryImpl
import com.sedilant.yambol.data.team.player.PlayerDao
import com.sedilant.yambol.data.team.player.PlayerRepository
import com.sedilant.yambol.data.team.player.PlayerRepositoryImpl
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
    abstract fun bindTrainingDraftRepository(impl: TrainingDraftRepositoryImpl): TrainingDraftRepository

    @Binds
    abstract fun bindPlayerRepository(impl: PlayerRepositoryImpl): PlayerRepository

    @Binds
    abstract fun bindConceptRepository(impl: ConceptRepositoryImpl): ConceptRepository

    companion object {

        @Provides
        @Singleton
        fun provideContext(application: Application): Context {
            return application.applicationContext
        }

        @Provides
        fun provideConceptDao(context: Context): ConceptDao {
            return YambolDatabase.getDatabase(context).conceptDao()
        }

        @Provides
        fun providePlayerDao(context: Context): PlayerDao {
            return YambolDatabase.getDatabase(context).playerDao()
        }

        @Provides
        fun provideTeamDao(context: Context): TeamDao {
            return YambolDatabase.getDatabase(context).teamDao()
        }

        @Provides
        fun provideTeamObjectivesDao(context: Context): TeamObjectivesDao {
            return YambolDatabase.getDatabase(context).teamObjectivesDao()
        }

        @Provides
        fun provideTrainingDao(context: Context): TrainingDao {
            return YambolDatabase.getDatabase(context).trainingDao()
        }

        @Provides
        fun providesTrainingDraftDao(context: Context): TrainingDraftDao {
            return YambolDatabase.getDatabase(context).trainingDraftDao()
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
