package com.sedilant.yambol.domain.delete

import com.sedilant.yambol.data.firestore.PlayerRepository

class DeletePlayerUseCaseImpl(
    private val playerRepository: PlayerRepository,
) : DeletePlayerUseCase {
    override suspend fun invoke(playerId: String) {
        playerRepository.delete(playerId)
    }
}
