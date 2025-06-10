package com.sedilant.yambol.domain.get

import com.sedilant.yambol.data.TeamRepository
import javax.inject.Inject

class GetPlayerTeamIdUseCaseImpl @Inject constructor(
    private val teamRepository: TeamRepository
) : GetPlayerTeamIdUseCase {
    override suspend fun invoke(playerId: Int): Int {
        return teamRepository.getPlayerTeamId(playerId)
    }
}
