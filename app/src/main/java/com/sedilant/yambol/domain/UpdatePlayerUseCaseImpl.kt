package com.sedilant.yambol.domain

import com.sedilant.yambol.data.TeamRepository
import javax.inject.Inject

class UpdatePlayerUseCaseImpl @Inject constructor(
    private val teamRepository: TeamRepository
) : UpdatePlayerUseCase {
    override suspend fun invoke(
        playerId: Int,
        newName: String,
        newNumber: Int,
    ) {
        teamRepository.updatePlayer(
            playerId = playerId,
            newName = newName.lowercase(),
            newNumber = newNumber,
            newPosition = 1
        )
    }
}