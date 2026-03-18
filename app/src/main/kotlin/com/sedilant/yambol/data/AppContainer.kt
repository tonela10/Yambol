package com.sedilant.yambol.data

import android.content.Context
import com.sedilant.yambol.data.team.TeamDatabase
import com.sedilant.yambol.data.team.TeamRepository
import com.sedilant.yambol.data.team.TeamRepositoryImpl

interface AppContainer {
    val teamRepository: TeamRepository
}

class DefaultAppContainer(context: Context) : AppContainer {
    override val teamRepository: TeamRepository by lazy {
        TeamRepositoryImpl(
            teamObjectivesDao = TeamDatabase.getDatabase(context).teamObjectivesDao(),
            trainingDao = TeamDatabase.getDatabase(context).trainingDao(),
            teamDao = TeamDatabase.getDatabase(context).teamDao()
        )
    }
}
