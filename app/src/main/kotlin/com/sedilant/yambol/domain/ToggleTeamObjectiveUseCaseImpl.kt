package com.sedilant.yambol.domain

import com.sedilant.yambol.data.firestore.TeamObjectiveRepository

class ToggleTeamObjectiveUseCaseImpl(
    private val repository: TeamObjectiveRepository
) : ToggleTeamObjectiveUseCase {
    override suspend fun invoke(objectiveId: String, isCompleted: Boolean) {
        repository.toggleCompletion(objectiveId, isCompleted)
    }
}
