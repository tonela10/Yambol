package com.sedilant.yambol.domain.insert

import com.sedilant.yambol.domain.models.PlayerDomainModel

interface InsertPlayerUseCase {
    suspend operator fun invoke(player: PlayerDomainModel)
}
