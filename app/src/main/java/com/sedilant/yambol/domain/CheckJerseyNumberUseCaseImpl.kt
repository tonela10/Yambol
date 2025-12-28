package com.sedilant.yambol.domain

import com.sedilant.yambol.data.team.player.PlayerRepository
import javax.inject.Inject

class CheckJerseyNumberUseCaseImpl @Inject constructor(
    private val playerRepository: PlayerRepository,
) : CheckJerseyNumberUseCase {
    override suspend fun invoke(teamId: Long, jerseyNumber: Int, excludePlayerId: Long?): Boolean {
        return playerRepository.isJerseyNumberTaken(teamId, jerseyNumber, excludePlayerId)
    }
}
