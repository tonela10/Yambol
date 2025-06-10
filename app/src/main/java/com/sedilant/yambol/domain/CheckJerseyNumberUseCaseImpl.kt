package com.sedilant.yambol.domain

import com.sedilant.yambol.data.TeamRepository
import javax.inject.Inject

class CheckJerseyNumberUseCaseImpl @Inject constructor(
    private val teamRepository: TeamRepository
) : CheckJerseyNumberUseCase {
    override suspend fun invoke(teamId: Int, jerseyNumber: Int, excludePlayerId: Int?): Boolean {
        return teamRepository.isJerseyNumberTaken(teamId, jerseyNumber, excludePlayerId)
    }
}
