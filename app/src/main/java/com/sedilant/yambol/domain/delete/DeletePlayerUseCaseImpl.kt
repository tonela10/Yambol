package com.sedilant.yambol.domain.delete

import com.sedilant.yambol.data.team.player.PlayerEntity
import com.sedilant.yambol.data.team.player.PlayerRepository
import javax.inject.Inject

class DeletePlayerUseCaseImpl @Inject constructor(
    private val playerRepository: PlayerRepository,
) : DeletePlayerUseCase {
    override suspend fun invoke(playerId: Long, name: String, number: Int, teamId: Long) {
        playerRepository.deletePlayer(
            player = PlayerEntity(
                id = playerId,
                name = name,
                number = number,
                teamId = teamId
            )
        )
    }
}
