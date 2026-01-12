package com.sedilant.yambol

import com.sedilant.yambol.data.firestore.TeamRepository
import com.sedilant.yambol.domain.UpdateTeamUseCase
import javax.inject.Inject

class UpdateTeamUseCaseImpl @Inject constructor(
    private val teamRepository: TeamRepository
) : UpdateTeamUseCase {
    override suspend fun invoke(teamId: String, newName: String) {
        teamRepository.updateName(teamId, newName.lowercase())
    }
}
