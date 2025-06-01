package com.sedilant.yambol.domain

import com.sedilant.yambol.data.StatsRecordRepository
import com.sedilant.yambol.domain.models.AbilityDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetPlayerStatsUseCaseImpl @Inject constructor(
    private val statsRecordRepository: StatsRecordRepository,
) : GetPlayerStatsUseCase {
    override suspend fun invoke(playerId: Int): Flow<List<AbilityDomainModel>> {
        return statsRecordRepository.getPlayerStats(playerId)
            .map { list ->
            list
                .map { it.mapToDomain() }
                .groupBy { it.name }
                .map { (name, group) ->
                val averageValue = group.sumOf { it.value.toDouble() } / group.size
                AbilityDomainModel(name, averageValue.toFloat())
            }
        }
    }
}
