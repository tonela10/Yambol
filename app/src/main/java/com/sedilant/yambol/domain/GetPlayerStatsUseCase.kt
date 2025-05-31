package com.sedilant.yambol.domain

import com.sedilant.yambol.domain.models.AbilityDomainModel

interface GetPlayerStatsUseCase {
    suspend operator fun invoke(playerId: Int): List<AbilityDomainModel>
}
