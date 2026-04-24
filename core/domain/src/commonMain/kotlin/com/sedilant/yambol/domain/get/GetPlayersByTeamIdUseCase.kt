package com.sedilant.yambol.domain.get

import com.sedilant.yambol.domain.models.PlayerDomainModel
import kotlinx.coroutines.flow.Flow

interface GetPlayersByTeamIdUseCase {
    suspend operator fun invoke(teamId: String): Flow<List<PlayerDomainModel>>
}
