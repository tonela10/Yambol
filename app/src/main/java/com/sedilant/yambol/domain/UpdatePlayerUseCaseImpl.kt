package com.sedilant.yambol.domain

import com.sedilant.yambol.data.team.player.PlayerEntity
import com.sedilant.yambol.data.team.player.PlayerRepository
import javax.inject.Inject

class UpdatePlayerUseCaseImpl @Inject constructor(
    private val playerRepository: PlayerRepository
) : UpdatePlayerUseCase {
    override suspend fun invoke(
        playerId: Long,
        newName: String,
        newNumber: Int,
        teamId: Long,
    ) {
        playerRepository.updatePlayer(
            player = PlayerEntity(
                id = playerId,
                name = newName,
                number = newNumber,
                teamId = teamId
            )
        )
    }
}