package com.sedilant.yambol.domain

interface CheckJerseyNumberUseCase {
    suspend operator fun invoke(
        teamId: Long,
        jerseyNumber: Int,
        excludePlayerId: Long? = null
    ): Boolean
}
