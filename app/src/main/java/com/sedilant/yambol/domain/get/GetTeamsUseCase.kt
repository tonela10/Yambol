package com.sedilant.yambol.domain.get

import com.sedilant.yambol.domain.models.TeamDomainModel
import kotlinx.coroutines.flow.Flow

interface GetTeamsUseCase {
    suspend operator fun invoke(): Flow<List<TeamDomainModel>>
}
