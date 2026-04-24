package com.sedilant.yambol.domain.get

import com.sedilant.yambol.domain.models.PlayerDomainModel

interface GetPlayerByIdUseCase {
    suspend operator fun invoke(id: String): PlayerDomainModel
}
