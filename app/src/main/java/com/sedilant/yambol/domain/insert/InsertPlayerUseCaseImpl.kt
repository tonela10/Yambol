package com.sedilant.yambol.domain.insert

import com.sedilant.yambol.data.entities.PlayerEntity
import com.sedilant.yambol.data.TeamRepository
import com.sedilant.yambol.domain.Position
import com.sedilant.yambol.ui.home.models.PlayerUiModel
import javax.inject.Inject

class InsertPlayerUseCaseImpl @Inject constructor(
    private val teamRepository: TeamRepository
) : InsertPlayerUseCase {
    override suspend fun invoke(playerUiModel: PlayerUiModel, teamId: Int) {
        teamRepository.insertPlayer(
            PlayerEntity(
                name = playerUiModel.name.lowercase(),
                number = playerUiModel.number.toInt(),
                teamId = teamId,
                position = when (playerUiModel.position) {
                    Position.POINT_GUARD -> 1
                    Position.SHOOTING_GUARD -> 2
                    Position.SMALL_FORWARD -> 3
                    Position.POWER_FORWARD -> 4
                    Position.CENTER -> 5
                },
            )
        )
    }
}
