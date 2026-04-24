package com.sedilant.yambol.domain

interface UpdatePlayerUseCase {
    suspend operator fun invoke(
        playerId: String,
        newName: String,
        newNumber: Int,
        teamId: String,
    )
}
