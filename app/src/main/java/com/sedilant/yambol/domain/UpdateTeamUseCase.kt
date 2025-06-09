package com.sedilant.yambol.domain

interface UpdateTeamUseCase {
    suspend operator fun invoke(teamId: Int, newName: String)
}
