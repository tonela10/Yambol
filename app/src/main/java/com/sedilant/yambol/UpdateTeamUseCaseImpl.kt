package com.sedilant.yambol

import com.sedilant.yambol.data.team.TeamRepository
import com.sedilant.yambol.domain.UpdateTeamUseCase
import javax.inject.Inject

class UpdateTeamUseCaseImpl @Inject constructor(
    private val teamRepository: TeamRepository
) : UpdateTeamUseCase {
    override suspend fun invoke(teamId: Long, newName: String) {
        teamRepository.updateTeam(teamId, newName.lowercase())
    }
}
