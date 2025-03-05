package com.sedilant.yambol.domain

import com.sedilant.yambol.data.Position
import com.sedilant.yambol.data.TeamRepository
import com.sedilant.yambol.ui.home.models.PlayerUiModel
import com.sedilant.yambol.ui.home.models.TeamUiModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetTeamsUseCaseImpl @Inject constructor(
    private val teamRepository: TeamRepository
) : GetTeamsUseCase {
    override suspend fun invoke(): Flow<List<TeamUiModel>> = flow {
        teamRepository.getAllTeams().collect { teams ->
            val teamUiModels = teams.map { team ->
                val players = teamRepository.getTeamPlayer(team.id).map { playerList ->
                    playerList.map { player ->
                        PlayerUiModel(
                            name = player.name,
                            number = player.number.toString(),
                            position = when (player.position) {
                                1 -> Position.POINT_GUARD.name
                                2 -> Position.SHOOTING_GUARD.name
                                3 -> Position.SMALL_FORWARD.name
                                4 -> Position.POWER_FORWARD.name
                                5 -> Position.CENTER.name
                                else -> ""
                            }
                        )
                    }
                }

                TeamUiModel(
                    name = team.name,
                    players = players.first(),
                    tasks = emptyList() // TODO think about how to add the tasks
                )
            }

            emit(teamUiModels)
        }
    }
}
