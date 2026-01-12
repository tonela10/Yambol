package com.sedilant.yambol.domain

interface UpdateTeamObjectiveUseCase {
    suspend operator fun invoke(
        objectiveId: String,
        newDescription: String,
        isFinish: Boolean,
        teamId: String
    )
}
