package com.sedilant.yambol.domain.get

import com.sedilant.yambol.domain.models.AbilityDomainModel
import kotlinx.coroutines.flow.Flow

interface GetPlayerStatsUseCase {
    suspend operator fun invoke(playerId: Int): Flow<List<AbilityDomainModel>>
}
