package com.sedilant.yambol.domain.get

interface GetLastTrainOfTeamUseCase {
    suspend operator fun invoke(teamId: Int?): Long?
}
