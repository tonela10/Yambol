package com.sedilant.yambol.domain.delete

import com.sedilant.yambol.data.TeamRepository
import javax.inject.Inject

class DeletePlayerUseCaseImpl @Inject constructor(
    private val teamRepository: TeamRepository,
) : DeletePlayerUseCase {
    override suspend fun invoke(playerId: Int) {
        // Note: Player stats will be automatically deleted due to CASCADE foreign key
        teamRepository.deletePlayer(playerId)
    }
}
