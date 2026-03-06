package com.sedilant.yambol.domain

interface CheckJerseyNumberUseCase {
    suspend operator fun invoke(
        teamId: String,
        jerseyNumber: Int,
        excludePlayerId: String? = null
    ): Boolean
}
