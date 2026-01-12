package com.sedilant.yambol.domain

interface DeleteTeamObjectiveUseCase {
    suspend operator fun invoke(teamObjectiveId: String)
}
