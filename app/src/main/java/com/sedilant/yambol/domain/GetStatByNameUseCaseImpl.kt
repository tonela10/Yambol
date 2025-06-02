package com.sedilant.yambol.domain

import com.sedilant.yambol.data.StatsRecordRepository
import com.sedilant.yambol.domain.models.AbilityDomainModel
import javax.inject.Inject

class GetStatByNameUseCaseImpl @Inject constructor(
    private val statsRecordRepository: StatsRecordRepository,
) : GetStatByNameUseCase {
    override suspend fun invoke(statName: String): AbilityDomainModel {
        val stat = statsRecordRepository.getStatByName(statName)
        return AbilityDomainModel(
            name = stat.name,
            id = stat.id,
            value = 0f,
        )
    }
}