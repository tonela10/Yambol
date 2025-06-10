package com.sedilant.yambol.domain.delete

interface DeletePlayerUseCase {
    suspend operator fun invoke(playerId: Int)
}
