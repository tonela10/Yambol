package com.sedilant.yambol.data.di

import com.sedilant.yambol.data.DataStoreManager
import com.sedilant.yambol.data.YambolDatabase
import com.sedilant.yambol.data.draftTrain.TrainingDraftRepository
import com.sedilant.yambol.data.draftTrain.TrainingDraftRepositoryImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataModule = module {
    single { YambolDatabase.getDatabase(androidContext()) }
    single { get<YambolDatabase>().conceptDao() }
    single { get<YambolDatabase>().playerDao() }
    single { get<YambolDatabase>().teamDao() }
    single { get<YambolDatabase>().teamObjectivesDao() }
    single { get<YambolDatabase>().trainingDao() }
    single { get<YambolDatabase>().trainingDraftDao() }
    single { DataStoreManager(androidContext()) }
    single<TrainingDraftRepository> { TrainingDraftRepositoryImpl(get()) }
}
