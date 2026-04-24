package com.sedilant.yambol.data.di

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
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
import org.koin.dsl.module

val firestoreModule = module {
    single { Firebase.firestore }
    single<TeamRepository> { FirestoreTeamRepository(get()) }
    single<PlayerRepository> { FirestorePlayerRepository(get()) }
    single<TaskRepository> { FirestoreTaskRepository(get()) }
    single<TrainRepository> { FirestoreTrainRepository(get()) }
    single<ConceptRepository> { FirestoreConceptRepository(get()) }
    single<TeamObjectiveRepository> { FirestoreTeamObjectiveRepository(get()) }
}
