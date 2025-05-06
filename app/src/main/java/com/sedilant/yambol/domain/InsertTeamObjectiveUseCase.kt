package com.sedilant.yambol.domain

interface InsertTeamObjectiveUseCase {
    suspend operator fun invoke(description: String, teamId: Int)
}
