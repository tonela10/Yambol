package com.sedilant.yambol.data

import com.sedilant.yambol.data.queries.AbilityRecordWithName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class StatsRecordRepositoryImpl @Inject constructor(
    private val statsDao: StatsDao
) : StatsRecordRepository {

    override suspend fun getPlayerStats(playerId: Int): List<AbilityRecordWithName> {
        return withContext(Dispatchers.IO) {
            statsDao.getAbilityRecordsWithName(playerId)
        }
    }
}
