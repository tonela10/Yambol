package com.sedilant.yambol.domain.insert

import com.sedilant.yambol.data.entities.TeamEntity
import com.sedilant.yambol.data.TeamRepository
import javax.inject.Inject

class InsertTeamUseCaseImpl @Inject constructor(
    private val teamRepository: TeamRepository
) : InsertTeamUseCase {
    override suspend fun invoke(name: String) {
        teamRepository.insertTeam(
            TeamEntity(
                name = name.lowercase()
            )
        )
    }
}
