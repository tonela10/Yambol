package com.sedilant.yambol.domain

import com.sedilant.yambol.data.team.TeamRepository
import javax.inject.Inject

class ToggleTeamObjectiveUseCaseImpl @Inject constructor(
    private val repository: TeamRepository
) : ToggleTeamObjectiveUseCase {
    override suspend fun invoke(objectiveId: Int) {
        val objective = repository.getTeamObjectiveById(objectiveId) ?: return
        val updatedObjective = objective.copy(isFinish = !objective.isFinish)
        repository.updateTeamObjective(updatedObjective)
    }
}
