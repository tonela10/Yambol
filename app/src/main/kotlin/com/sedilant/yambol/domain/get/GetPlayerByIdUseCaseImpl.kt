package com.sedilant.yambol.domain.get

import com.sedilant.yambol.data.firestore.PlayerRepository
import com.sedilant.yambol.ui.home.models.PlayerUiModel
import javax.inject.Inject

class GetPlayerByIdUseCaseImpl @Inject constructor(
    private val playerRepository: PlayerRepository
) : GetPlayerByIdUseCase {

    override suspend fun invoke(id: String): PlayerUiModel {
        val player = playerRepository.getPlayerById(id)
            ?: throw IllegalArgumentException("Player with id $id not found")

        return PlayerUiModel(
            id = player.id,
            name = player.name,
            number = player.number?.toString() ?: "", // Number is nullable in DTO but needed as string here
            teamId = player.teamId,
        )
    }
}
