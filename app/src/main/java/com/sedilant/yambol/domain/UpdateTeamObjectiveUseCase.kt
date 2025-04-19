package com.sedilant.yambol.domain

interface UpdateTeamObjectiveUseCase {
    suspend operator fun invoke(
        objectiveId: Int,
        description: String? = null,
        isFinish: Boolean? = null
    )
}
