package com.sedilant.yambol.domain.get

import com.sedilant.yambol.data.firestore.PlayerRepository
import com.sedilant.yambol.domain.models.PlayerDomainModel

class GetPlayerByIdUseCaseImpl(
    private val playerRepository: PlayerRepository
) : GetPlayerByIdUseCase {

    override suspend fun invoke(id: String): PlayerDomainModel {
        val player = playerRepository.getPlayerById(id)
            ?: throw IllegalArgumentException("Player with id $id not found")

        return PlayerDomainModel(
            id = player.id,
            name = player.name,
            number = player.number?.toString() ?: "",
            teamId = player.teamId,
        )
    }
}
