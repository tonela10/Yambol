package com.sedilant.yambol.domain.delete

interface DeletePlayerUseCase {
    suspend operator fun invoke(playerId: Long, name: String, number: Int, teamId: Long)
}
