package com.sedilant.yambol.domain

import com.sedilant.yambol.data.firestore.TeamObjectiveRepository

class DeleteTeamObjectiveUseCaseImpl(
    private val teamObjectiveRepository: TeamObjectiveRepository
) : DeleteTeamObjectiveUseCase {
    override suspend fun invoke(teamObjectiveId: String) {
        teamObjectiveRepository.delete(teamObjectiveId)
    }
}
