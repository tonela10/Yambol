package com.sedilant.yambol.domain

import com.sedilant.yambol.data.firestore.TeamObjectiveRepository
import javax.inject.Inject

class ToggleTeamObjectiveUseCaseImpl @Inject constructor(
    private val repository: TeamObjectiveRepository
) : ToggleTeamObjectiveUseCase {
    override suspend fun invoke(objectiveId: String, isCompleted: Boolean) {
        repository.toggleCompletion(objectiveId, isCompleted)
    }
}
