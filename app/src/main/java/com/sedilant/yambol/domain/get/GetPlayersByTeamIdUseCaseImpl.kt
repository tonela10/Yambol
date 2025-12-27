package com.sedilant.yambol.domain.get

import com.sedilant.yambol.data.team.player.PlayerRepository
import com.sedilant.yambol.domain.mapToUI
import com.sedilant.yambol.ui.home.models.PlayerUiModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetPlayersByTeamIdUseCaseImpl @Inject constructor(
    private val playerRepository: PlayerRepository
) : GetPlayersByTeamIdUseCase {
    override suspend fun invoke(teamId: Long): Flow<List<PlayerUiModel>> {
        return playerRepository.getPlayersByTeam(teamId).map { list ->
            list.map { playerEntity -> playerEntity.mapToUI() }
        }
    }
}
