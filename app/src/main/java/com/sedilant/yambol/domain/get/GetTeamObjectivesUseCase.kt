package com.sedilant.yambol.domain.get

import com.sedilant.yambol.domain.models.TeamObjectivesDomainModel
import kotlinx.coroutines.flow.Flow

interface GetTeamObjectivesUseCase {
    suspend operator fun invoke(teamId: Long): Flow<List<TeamObjectivesDomainModel>>
}
