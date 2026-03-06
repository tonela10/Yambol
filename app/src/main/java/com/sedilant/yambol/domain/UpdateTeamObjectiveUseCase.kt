package com.sedilant.yambol.domain

interface UpdateTeamObjectiveUseCase {
    suspend operator fun invoke(
        objectiveId: String,
        newDescription: String,
        isCompleted: Boolean,
        teamId: String
    )
}
