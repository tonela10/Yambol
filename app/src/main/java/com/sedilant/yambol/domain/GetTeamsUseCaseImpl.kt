package com.sedilant.yambol.domain

import com.sedilant.yambol.data.TeamRepository
import com.sedilant.yambol.domain.models.TeamDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetTeamsUseCaseImpl @Inject constructor(
    private val teamRepository: TeamRepository
) : GetTeamsUseCase {
    override suspend fun invoke(): Flow<List<TeamDomainModel>> {
        return teamRepository.getAllTeams().map { list ->
            list.map { it.mapToDomain() }
        }
    }
}
