package com.sedilant.yambol.data

import android.content.Context

interface AppContainer {
    val teamRepository: TeamRepository
}

class DefaultAppContainer(context: Context) : AppContainer {
    override val teamRepository: TeamRepository by lazy {
        TeamRepositoryImpl(
            playerDao = TeamDatabase.getDatabase(context).playerDao()
        )
    }
}
