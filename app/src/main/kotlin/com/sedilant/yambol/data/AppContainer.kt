package com.sedilant.yambol.data

import android.content.Context
import com.sedilant.yambol.data.team.TeamRepository
import com.sedilant.yambol.data.team.TeamRepositoryImpl

interface AppContainer {
    val teamRepository: TeamRepository
}

class DefaultAppContainer(context: Context) : AppContainer {
    override val teamRepository: TeamRepository by lazy {
        TeamRepositoryImpl(
            teamObjectivesDao = YambolDatabase.getDatabase(context).teamObjectivesDao(),
            trainingDao = YambolDatabase.getDatabase(context).trainingDao(),
            teamDao = YambolDatabase.getDatabase(context).teamDao()
        )
    }
}
