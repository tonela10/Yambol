package com.sedilant.yambol.domain.get

interface GetTeamIdUseCase {
    suspend operator fun invoke(teamName: String): Int?
}
