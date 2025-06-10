package com.sedilant.yambol.domain

interface UpdatePlayerUseCase {
    suspend operator fun invoke(
        playerId: Int,
        newName: String,
        newNumber: Int,
        newPosition: Position
    )
}
