package com.sedilant.yambol.domain.get

import com.sedilant.yambol.data.team.player.PlayerRepository
import com.sedilant.yambol.ui.home.models.PlayerUiModel
import javax.inject.Inject

class GetPlayerByIdUseCaseImpl @Inject constructor(
    private val playerRepository: PlayerRepository
) : GetPlayerByIdUseCase {

    override suspend fun invoke(id: Long): PlayerUiModel {
        val player = playerRepository.getPlayerById(id)
            ?: throw IllegalArgumentException("Player with id $id not found")

        return PlayerUiModel(
            id = player.id,
            name = player.name,
            number = player.number.toString(),
            teamId = player.teamId,
        )
    }
}
