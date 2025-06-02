package com.sedilant.yambol.data

import com.sedilant.yambol.data.entities.AbilityNameEntity
import com.sedilant.yambol.data.entities.AbilityRecordEntity
import com.sedilant.yambol.data.queries.AbilityRecordWithName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class StatsRecordRepositoryImpl @Inject constructor(
    private val statsDao: StatsDao
) : StatsRecordRepository {

    override suspend fun getPlayerStats(playerId: Int): Flow<List<AbilityRecordWithName>> {
        return withContext(Dispatchers.IO) {
            statsDao.getAbilityRecordsWithName(playerId)
        }
    }

    override suspend fun getStatById(statId: Int): AbilityNameEntity {
        return withContext(Dispatchers.IO) {
            statsDao.getStatById(statId = statId)
        }
    }

    override suspend fun getStatByName(statName: String): AbilityNameEntity {
        return withContext(Dispatchers.IO) {
            statsDao.getStatByName(statName)
        }
    }

    override suspend fun insertPlayerStatUseCase(abilityRecordEntity: AbilityRecordEntity) {
        return withContext(Dispatchers.IO) {
            statsDao.insertStatRecord(abilityRecordEntity)
        }
    }
}
