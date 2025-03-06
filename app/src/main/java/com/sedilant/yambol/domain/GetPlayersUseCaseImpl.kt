package com.sedilant.yambol.domain

import com.sedilant.yambol.data.TeamRepository
import com.sedilant.yambol.ui.home.models.PlayerUiModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetPlayersUseCaseImpl @Inject constructor(
    private val teamRepository: TeamRepository
) : GetPlayersUseCase {
    override suspend fun invoke(teamId: Int): Flow<List<PlayerUiModel>> {
        return teamRepository.getTeamPlayers(teamId).map { list ->
            list.map { it.mapToDomain() }
        }
    }
}
