package com.sedilant.yambol.domain

import com.sedilant.yambol.data.TeamRepository
import com.sedilant.yambol.data.entities.TeamObjectivesEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class InsertTeamObjectiveUseCaseImpl @Inject constructor(
    private val teamRepository: TeamRepository
) : InsertTeamObjectiveUseCase{
    override suspend fun invoke(description: String, teamId: Int) {
       withContext(Dispatchers.IO){
           teamRepository.insertTeamObjective(
               TeamObjectivesEntity(
                   description = description,
                   isFinish = false,
                   teamId = teamId,
               )
           )
       }
    }
}
