package com.sedilant.yambol.domain.insert

import com.sedilant.yambol.data.firebaseAuth.AuthRepository
import com.sedilant.yambol.data.firestore.PlayerDto
import com.sedilant.yambol.data.firestore.PlayerRepository
import com.sedilant.yambol.domain.models.PlayerDomainModel

class InsertPlayersUseCaseImpl(
    private val playerRepository: PlayerRepository,
    private val authRepository: AuthRepository
) : InsertPlayersUseCase {
    override suspend fun invoke(players: List<PlayerDomainModel>) {
        val userId = authRepository.currentUser?.uid ?: return
        players.forEach { player ->
            playerRepository.upsert(
                PlayerDto(
                    name = player.name.lowercase(),
                    number = player.number.toIntOrNull(),
                    teamId = player.teamId,
                    userId = userId
                )
            )
        }
    }
}
