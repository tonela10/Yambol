package com.sedilant.yambol.domain

import com.sedilant.yambol.data.TeamRepository
import javax.inject.Inject

class UpdateTeamObjectiveUseCaseImpl @Inject constructor(
    private val repository: TeamRepository
) : UpdateTeamObjectiveUseCase {
    override suspend fun invoke(objectiveId: Int, description: String?, isFinish: Boolean?) {
        val objective = repository.getTeamObjectiveById(objectiveId) ?: return

        val updatedObjective = objective.copy(
            description = description ?: objective.description,
            isFinish = isFinish ?: objective.isFinish
        )

        repository.updateTeamObjective(updatedObjective)
    }
}
