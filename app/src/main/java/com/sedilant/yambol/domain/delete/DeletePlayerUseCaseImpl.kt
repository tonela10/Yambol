package com.sedilant.yambol.domain.delete

import com.sedilant.yambol.data.firestore.PlayerRepository
import javax.inject.Inject

class DeletePlayerUseCaseImpl @Inject constructor(
    private val playerRepository: PlayerRepository,
) : DeletePlayerUseCase {
    override suspend fun invoke(playerId: String) {
        playerRepository.delete(playerId)
    }
}
