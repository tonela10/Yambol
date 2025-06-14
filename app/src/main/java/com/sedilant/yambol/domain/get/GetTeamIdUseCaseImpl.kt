package com.sedilant.yambol.domain.get

import com.sedilant.yambol.data.TeamRepository
import javax.inject.Inject

class GetTeamIdUseCaseImpl @Inject constructor(
    private val teamRepository: TeamRepository
) : GetTeamIdUseCase {
    override suspend fun invoke(teamName: String): Int? {
        return teamRepository.getTeamId(teamName.lowercase())
    }
}
