package com.sedilant.yambol.domain.insert

import com.sedilant.yambol.data.team.player.PlayerRepository
import com.sedilant.yambol.domain.mapToEntity
import com.sedilant.yambol.ui.home.models.PlayerUiModel
import javax.inject.Inject

class InsertPlayersUseCaseImpl @Inject constructor(
    private val playerRepository: PlayerRepository,
) : InsertPlayersUseCase {
    override suspend fun invoke(players: List<PlayerUiModel>) {
        playerRepository.insertPlayers(players.map { it.mapToEntity() })
    }
}