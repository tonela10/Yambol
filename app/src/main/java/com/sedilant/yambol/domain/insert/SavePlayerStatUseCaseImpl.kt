package com.sedilant.yambol.domain.insert

import com.sedilant.yambol.data.StatsRecordRepository
import com.sedilant.yambol.data.entities.AbilityRecordEntity
import javax.inject.Inject

class SavePlayerStatUseCaseImpl @Inject constructor(
    private val statsRecordRepository: StatsRecordRepository
) : SavePlayerStatUseCase {
    override suspend fun invoke(playerId: Int, statId: Int, rating: Int) {
        statsRecordRepository.insertPlayerStatUseCase(
            AbilityRecordEntity(
                playerId = playerId,
                abilityId = statId,
                value = rating,
                timestamp = System.currentTimeMillis()
            )
        )
    }
}