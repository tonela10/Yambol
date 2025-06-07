package com.sedilant.yambol.domain.insert

interface InsertTeamObjectiveUseCase {
    suspend operator fun invoke(description: String, teamId: Int)
}
