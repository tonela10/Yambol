package com.sedilant.yambol.domain.insert

import com.sedilant.yambol.data.team.TeamObjectivesEntity
import com.sedilant.yambol.data.team.TeamRepository
import javax.inject.Inject

class InsertTeamObjectiveUseCaseImpl @Inject constructor(
    private val teamRepository: TeamRepository
) : InsertTeamObjectiveUseCase {
    override suspend fun invoke(description: String, teamId: Long) {
        teamRepository.insertTeamObjective(
            TeamObjectivesEntity(
                description = description,
                isFinish = false,
                teamId = teamId,
            )
        )
    }
}
