package com.sedilant.yambol.domain.get

import com.sedilant.yambol.domain.models.AbilityDomainModel

interface GetStatByNameUseCase {
    suspend operator fun invoke(statName: String): AbilityDomainModel
}