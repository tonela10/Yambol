package com.sedilant.yambol.domain.insert

import com.sedilant.yambol.data.team.player.PlayerEntity
import com.sedilant.yambol.data.team.player.PlayerRepository
import com.sedilant.yambol.ui.home.models.PlayerUiModel
import javax.inject.Inject

class InsertPlayerUseCaseImpl @Inject constructor(
    private val playerRepository: PlayerRepository
) : InsertPlayerUseCase {
    override suspend fun invoke(playerUiModel: PlayerUiModel) {
        playerRepository.insertPlayer(
            PlayerEntity(
                name = playerUiModel.name.lowercase(),
                number = playerUiModel.number.toInt(),
                teamId = playerUiModel.teamId,
            )
        )
    }
}
