package com.sedilant.yambol.domain

import kotlinx.coroutines.flow.Flow

interface GetTeamsUseCase {
    suspend operator fun invoke(): Flow<List<TeamDomainModel>>
}
