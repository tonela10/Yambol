package com.sedilant.yambol.domain

import com.sedilant.yambol.data.TeamRepository
import com.sedilant.yambol.domain.models.TeamObjectivesDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetTeamObjectivesUseCaseImpl @Inject constructor(
    private val teamRepository: TeamRepository,
) : GetTeamObjectivesUseCase {
    override suspend fun invoke(teamId: Int): Flow<List<TeamObjectivesDomainModel>> {
        return teamRepository.getTeamObjectives(teamId).map { list ->
            list.map {
                TeamObjectivesDomainModel(
                    description = it.description,
                    isFinish = it.isFinish,
                    id = it.id
                )
            }
        }
    }
}
