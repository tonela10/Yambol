package com.sedilant.yambol.domain

interface GetTeamIdUseCase {
    suspend operator fun invoke(teamName: String): Int
}
