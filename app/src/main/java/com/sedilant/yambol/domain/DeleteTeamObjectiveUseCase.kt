package com.sedilant.yambol.domain

interface DeleteTeamObjectiveUseCase {
    suspend operator fun invoke(
        objectiveId: Int, description: String, isFinish: Boolean, teamId: Int
    )
}
