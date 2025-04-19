package com.sedilant.yambol.domain

interface ToggleTeamObjectiveUseCase {
    suspend operator fun invoke(objectiveId: Int)
}
