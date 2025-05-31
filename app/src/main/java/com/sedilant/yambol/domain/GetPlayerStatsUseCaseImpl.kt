package com.sedilant.yambol.domain

import com.sedilant.yambol.data.StatsRecordRepository
import com.sedilant.yambol.domain.models.AbilityDomainModel
import javax.inject.Inject

class GetPlayerStatsUseCaseImpl @Inject constructor(
    private val statsRecordRepository: StatsRecordRepository,
) : GetPlayerStatsUseCase {
    override suspend fun invoke(playerId: Int): List<AbilityDomainModel> {
        val list = statsRecordRepository.getPlayerStats(playerId).map {
            it.mapToDomain()
        }
        return list.groupBy { it.name }
            .map { (name, group) ->
                val averageValue = group.sumOf { it.value.toDouble() } / group.size
                AbilityDomainModel(name, averageValue.toFloat())
            }
    }
}
