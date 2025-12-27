package com.sedilant.yambol.domain

interface UpdatePlayerUseCase {
    suspend operator fun invoke(
        playerId: Long,
        newName: String,
        newNumber: Int,
        teamId: Long,
    )
}
