package com.sedilant.yambol.domain.get

import com.sedilant.yambol.data.TeamRepository
import javax.inject.Inject

class GetLastTrainOfTeamUseCaseImpl @Inject constructor(
    private val teamRepository: TeamRepository
) : GetLastTrainOfTeamUseCase {
    override suspend fun invoke(teamId: Int?): Long? {
        return if (teamId != null) {
            teamRepository.getLastTrainWithTrainTaskByTeamId(teamId)
        } else null
    }
}
