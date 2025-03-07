package com.sedilant.yambol.domain

interface GetTeamIdUseCase {
    operator fun invoke(teamName: String): Int
}
