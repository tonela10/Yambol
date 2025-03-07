package com.sedilant.yambol.domain

import com.sedilant.yambol.data.TeamRepository
import javax.inject.Inject

class GetTeamIdUseCaseImpl @Inject constructor(
    private val teamRepository: TeamRepository
):GetTeamIdUseCase {
    override fun invoke(teamName: String): Int {
        return teamRepository.getTeamId(teamName)
    }
}
