package com.sedilant.yambol.domain.get

interface GetPlayerTeamIdUseCase {
    suspend operator fun invoke(playerId: Int): Int
}
