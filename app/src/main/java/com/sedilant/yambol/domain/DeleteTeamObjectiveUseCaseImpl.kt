package com.sedilant.yambol.domain

import com.sedilant.yambol.data.TeamRepository
import com.sedilant.yambol.data.entities.TeamObjectivesEntity
import javax.inject.Inject

class DeleteTeamObjectiveUseCaseImpl @Inject constructor(
    private val teamRepository: TeamRepository
) : DeleteTeamObjectiveUseCase {
    override suspend fun invoke(
        objectiveId: Int,
        description: String,
        isFinish: Boolean,
        teamId: Int
    ) {
        teamRepository.deleteTeamObjective(
            TeamObjectivesEntity(
                id = objectiveId,
                description = description,
                isFinish = isFinish,
                teamId = teamId,

                )
        )
    }
}
