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
        newPosition: Position
    ) {
        val positionValue = when (newPosition) {
            Position.POINT_GUARD -> 1
            Position.SHOOTING_GUARD -> 2
            Position.SMALL_FORWARD -> 3
            Position.POWER_FORWARD -> 4
            Position.CENTER -> 5
        }

        teamRepository.updatePlayer(
            playerId = playerId,
            newName = newName.lowercase(),
            newNumber = newNumber,
            newPosition = positionValue
        )
    }
}