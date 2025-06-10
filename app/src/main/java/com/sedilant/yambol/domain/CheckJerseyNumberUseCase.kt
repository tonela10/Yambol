package com.sedilant.yambol.domain

interface CheckJerseyNumberUseCase {
    suspend operator fun invoke(
        teamId: Int,
        jerseyNumber: Int,
        excludePlayerId: Int? = null
    ): Boolean
}
