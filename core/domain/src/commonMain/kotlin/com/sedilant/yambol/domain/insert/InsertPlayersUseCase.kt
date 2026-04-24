package com.sedilant.yambol.domain.insert

import com.sedilant.yambol.domain.models.PlayerDomainModel

interface InsertPlayersUseCase {
    suspend operator fun invoke(players: List<PlayerDomainModel>)
}
