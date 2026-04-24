package com.sedilant.yambol.domain

import com.sedilant.yambol.data.firestore.TeamRepository

class UpdateTeamUseCaseImpl(
    private val teamRepository: TeamRepository
) : UpdateTeamUseCase {
    override suspend fun invoke(teamId: String, newName: String) {
        teamRepository.updateName(teamId, newName.lowercase())
    }
}
