package com.sedilant.yambol.data.di

import com.google.firebase.firestore.FirebaseFirestore
import com.sedilant.yambol.data.firestore.ConceptRepository
import com.sedilant.yambol.data.firestore.FirestoreConceptRepository
import com.sedilant.yambol.data.firestore.FirestorePlayerRepository
import com.sedilant.yambol.data.firestore.FirestoreTaskRepository
import com.sedilant.yambol.data.firestore.FirestoreTeamObjectiveRepository
import com.sedilant.yambol.data.firestore.FirestoreTeamRepository
import com.sedilant.yambol.data.firestore.FirestoreTrainRepository
import com.sedilant.yambol.data.firestore.PlayerRepository
import com.sedilant.yambol.data.firestore.TaskRepository
import com.sedilant.yambol.data.firestore.TeamObjectiveRepository
import com.sedilant.yambol.data.firestore.TeamRepository
import com.sedilant.yambol.data.firestore.TrainRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirestoreModule {

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirestoreTeamRepository(db: FirebaseFirestore): TeamRepository {
        return FirestoreTeamRepository(db)
    }

    @Provides
    @Singleton
    fun provideFirestorePlayerRepository(db: FirebaseFirestore): PlayerRepository {
        return FirestorePlayerRepository(db)
    }

    @Provides
    @Singleton
    fun provideFirestoreTaskRepository(db: FirebaseFirestore): TaskRepository {
        return FirestoreTaskRepository(db)
    }

    @Provides
    @Singleton
    fun provideFirestoreTrainRepository(db: FirebaseFirestore): TrainRepository {
        return FirestoreTrainRepository(db)
    }

    @Provides
    @Singleton
    fun provideFirestoreConceptRepository(db: FirebaseFirestore): ConceptRepository {
        return FirestoreConceptRepository(db)
    }

    @Provides
    @Singleton
    fun provideFirestoreTeamObjectiveRepository(db: FirebaseFirestore): TeamObjectiveRepository {
        return FirestoreTeamObjectiveRepository(db)
    }
}
