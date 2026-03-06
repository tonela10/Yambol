package com.sedilant.yambol.domain

import com.sedilant.yambol.data.firestore.TeamObjectiveRepository
import javax.inject.Inject

class DeleteTeamObjectiveUseCaseImpl @Inject constructor(
    private val teamObjectiveRepository: TeamObjectiveRepository
) : DeleteTeamObjectiveUseCase {
    override suspend fun invoke(teamObjectiveId: String) {
        teamObjectiveRepository.delete(teamObjectiveId)
    }
}
